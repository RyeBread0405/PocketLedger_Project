package edu.okstate.pocketledger.ai;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.okstate.pocketledger.category.Category;
import edu.okstate.pocketledger.category.CategoryRepository;
import edu.okstate.pocketledger.common.NotFoundException;
import edu.okstate.pocketledger.transaction.Transaction;
import edu.okstate.pocketledger.transaction.TransactionRepository;


// re-categorizes transactions (named by the caller)
// wraps Gemini service and handles ownership, fallbacks, and saving
@Service
public class AiCategorizationService {
    private static final String DEFAULT_CATEGORY = "Other";

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final GeminiCategorizationService geminiCategorizationService;

    public AiCategorizationService(TransactionRepository transactionRepository, CategoryRepository categoryRepository, GeminiCategorizationService geminiCategorizationService) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.geminiCategorizationService = geminiCategorizationService;
    }

    @Transactional 
    public CategorizeResponse categorize(CategorizeRequest request, Integer userId) {
        Category other = findOtherCategory();
        List<CategorizeResult> results = new ArrayList<>();

        for (Long transactionId : request.transactionIds()) {
            Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new NotFoundException("Transaction not found: " + transactionId));

            if (!transaction.getAccount().getUser().getId().equals(userId)) {
                throw new AccessDeniedException("That transaction belongs to someone else");
            }

            Category chosen = other;
            String source = "default";

            try {
                Category suggested = geminiCategorizationService.suggestCategory(transaction.getLabel(), userId);
                if (suggested != null) {
                    chosen = suggested;
                    source = "ai";
                }
            } catch (RuntimeException exception) {
                // nothing usable from Gemini, transaction stays in other
            }

            transaction.setCategory(chosen);
            transactionRepository.save(transaction);

            // confidence null
            results.add(new CategorizeResult(transaction.getId(), chosen.getId(), source, null));
        }
        return new CategorizeResponse(results);
    }

    private Category findOtherCategory() {
        return categoryRepository.findAll().stream().filter(c -> DEFAULT_CATEGORY.equalsIgnoreCase(c.getCategoryLabel())).findFirst().orElseThrow(() -> new NotFoundException("No " + DEFAULT_CATEGORY + " category to fall back to"));
    }
}
