package edu.okstate.pocketledger.netsavings;

import org.springframework.stereotype.Service;

import edu.okstate.pocketledger.budget.BudgetService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
public class NetSavingsService{

    private final BudgetService budgetService;

    public NetSavingsService(BudgetService budgetService){
        this.budgetService = budgetService;
    }

    public BigDecimal calculateNetSavings(Integer userId){
        BigDecimal netSavings = BigDecimal.ZERO;
        YearMonth currentMonth = YearMonth.now();

        for (LocalDate budgetMonth : budgetService.getBudgetMonths(userId)){

            YearMonth month = YearMonth.from(budgetMonth);

            if(month.isBefore(currentMonth)){
                var budget = budgetService.getMonthlyBudget(userId, month.toString());

                BigDecimal monthlySavings = budget.totalPlanned().subtract(budget.totalSpent());

                netSavings = netSavings.add(monthlySavings);
            }
        }

        return netSavings;
    }
}

