package edu.okstate.pocketledger.budget;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional;

import edu.okstate.pocketledger.category.Category;
import edu.okstate.pocketledger.category.CategoryRepository;
import edu.okstate.pocketledger.common.NotFoundException;
import edu.okstate.pocketledger.transaction.Transaction;
import edu.okstate.pocketledger.transaction.TransactionRepository;
import edu.okstate.pocketledger.user.User;
import edu.okstate.pocketledger.user.UserRepository;


// builds the monthly budget
// planned amounts read from budget_entry
// spent added up from transactions
@Service
public class BudgetService {
    
    private final BudgetRepository budgetRepository;
    private final BudgetEntryRepository budgetEntryRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public BudgetService(
        BudgetRepository budgetRepository,
        BudgetEntryRepository budgetEntryRepository,
        TransactionRepository transactionRepository,
        CategoryRepository categoryRepository,
        UserRepository userRepository
    ) {
        this.budgetRepository = budgetRepository;
        this.budgetEntryRepository = budgetEntryRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    // GET /api/budgets/{month}
    public BudgetResponse getMonthlyBudget(Integer userId, String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        // planed amounts, empty list if no budget set for this month yet
        List<BudgetEntry> entries = 
        budgetRepository.findByUser_IdAndMonth(userId, firstDay).map(budget -> budgetEntryRepository.findByBudget_Id(budget.getId()))
        .orElse(List.of());

        // everything spent this month across all accounts
        List<Transaction> transactions = 
        transactionRepository.findByAccount_User_IdAndDateBetween(userId, firstDay, lastDay);

        return buildResponse(month, entries, transactions);
    }

    // PUT /api/budgets
    // frontend sends whole month at once, so this replaces what was saved before
    @Transactional 
    public BudgetResponse saveMonthlyBudget(Integer userId, SaveBudgetRequest request) {
        YearMonth yearMonth = YearMonth.parse(request.month());
        LocalDate firstDay = yearMonth.atDay(1);

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        // reuse existing budget if it exists, otherwise start a new one
        Budget budget = budgetRepository.findByUser_IdAndMonth(userId, firstDay).orElseGet(() -> budgetRepository.save(new Budget(firstDay, user)));


        // clear old lines and write new ones. Flush deletes before writing to avoid error.
        budgetEntryRepository.deleteAll(budgetEntryRepository.findByBudget_Id(budget.getId()));
        budgetEntryRepository.flush();

        List<BudgetEntry> entries = new ArrayList<>();

        for (BudgetAmountRequest line : request.budgets()) {
            Category category = categoryRepository.findById(line.categoryId()).orElseThrow(() -> new NotFoundException("Category not found"));
            entries.add(new BudgetEntry(budget, category, line.amount()));
        }

        budgetEntryRepository.saveAll(entries);

        LocalDate lastDay = yearMonth.atEndOfMonth();
        List<Transaction> transactions = transactionRepository.findByAccount_User_IdAndDateBetween(userId, firstDay, lastDay);

        return buildResponse(request.month(), entries, transactions);
    }





        // shared by both endpoints so response format looks the same
        private BudgetResponse buildResponse(String month, List<BudgetEntry> entries, List<Transaction> transactions) {

            // add category spendings
            // flip sign for positive "spent" since expenses are stored negative
            Map<Integer, BigDecimal> spentByCategory = new LinkedHashMap<>();
            Map<Integer, String> categoryNames = new LinkedHashMap<>();

            for (Transaction transaction : transactions) {
                Integer categoryId = transaction.getCategory().getId();

                // store name
                categoryNames.put(categoryId, transaction.getCategory().getCategoryLabel());

                spentByCategory.merge(categoryId, transaction.getAmount().negate(), BigDecimal::add);
            }

            // planned, one entry per category user set an amount for
            Map<Integer, BigDecimal> plannedByCategory = new LinkedHashMap<>();
            for (BudgetEntry entry : entries) {
                Integer categoryId = entry.getCategory().getId();

                // picks up names for cateogries budgeted for but never spent in
                // would be missed by transaction loop ^
                categoryNames.put(categoryId, entry.getCategory().getCategoryLabel());
                plannedByCategory.put(categoryId, entry.getAllotted());
            }

            List<BudgetCategoryResponse> categories = new ArrayList<>();
            BigDecimal totalPlanned = BigDecimal.ZERO;
            BigDecimal totalSpent = BigDecimal.ZERO;

            for (Map.Entry<Integer, String> named : categoryNames.entrySet()) {

                // getOrDefault covers 2 one-sided cases: budgeted but not spend in yet gives spent 0,
                //  spent in but never budgeted gies planned 0
                BigDecimal planned = plannedByCategory.getOrDefault(named.getKey(), BigDecimal.ZERO);
                BigDecimal spent = spentByCategory.getOrDefault(named.getKey(), BigDecimal.ZERO);

                // remaining goes negative when user overspends
                categories.add(new BudgetCategoryResponse(named.getKey(), named.getValue(), planned, spent, planned.subtract(spent)));

                // 2 totals added up
                totalPlanned = totalPlanned.add(planned);
                totalSpent = totalSpent.add(spent);
            }

            return new BudgetResponse(month, totalPlanned, totalSpent, categories);
        }
    }
