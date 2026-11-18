package edu.okstate.pocketledger.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.okstate.pocketledger.category.Category;
import edu.okstate.pocketledger.category.CategoryRepository;
import edu.okstate.pocketledger.common.NotFoundException;
import edu.okstate.pocketledger.transaction.CreateTransactionRequest;
import edu.okstate.pocketledger.transaction.TransactionService;
import edu.okstate.pocketledger.ai.GeminiCategorizationService;


// turns uploaded bank csv into transactions on one account
// expected: a header row with date, description, and amount columns
// if categorize is on, each row's description goes to Gemini for a category
@Service 
public class CsvImportService {
    
    // the 2 date formats banks usually export
    private static final DateTimeFormatter ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter US = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    // where a row goes when all else fails
    private static final String DEFAULT_CATEGORY = "Other";
    
    private final TransactionService transactionService;
    private final CategoryRepository categoryRepository;
    private final GeminiCategorizationService geminiCategorizationService;

    public CsvImportService(TransactionService transactionService, CategoryRepository categoryRepository, GeminiCategorizationService geminiCategorizationService) {
        this.transactionService = transactionService;
        this.categoryRepository = categoryRepository;
        this.geminiCategorizationService = geminiCategorizationService;
    }

    public CsvImportResponse importCsv(MultipartFile file, Long accountId, Integer userId, boolean categorize) {
        Integer otherCategoryId = findOtherCategory().getId();

        int imported = 0;
        int skipped = 0;
        int failed = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                // empty file my friends
                return new CsvImportResponse(0, 0, 0);
            }

            // figure out columns here so order in the file doesn't matter
            String[] headers = split(headerLine);
            int dateColumn = columnNamed(headers, "date");
            int descriptionColumn = columnNamed(headers, "description");
            int amountColumn = columnNamed(headers, "amount");

            if (dateColumn < 0 || descriptionColumn < 0 || amountColumn < 0) {
                throw new InvalidCsvException("The file needs date, the description, and amount columns in its first row.");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] values = split(line);

                // a row missing 1/3 columns isn't usable
                if (values.length <= Math.max(dateColumn, Math.max(descriptionColumn, amountColumn))) {
                    skipped++;
                    continue;
                }

                LocalDate date = parseDate(values[dateColumn]);
                BigDecimal amount = parseAmount(values[amountColumn]);
                String label = values[descriptionColumn].trim();

                // labels are needed now #organized
                // date or amount we can't read gets SKIPPED so no guessing
                if (date == null || amount == null || label.isEmpty()) {
                    skipped++;
                    continue;
                }

                // ask Gemini for a category, if it can't answer put it in other
                Integer categoryId = otherCategoryId;
                if (categorize) {
                    try {
                        Category suggested = geminiCategorizationService.suggestCategory(label, userId);

                        if (suggested != null) {
                            categoryId = suggested.getId();
                        }
                    } catch (RuntimeException exception) {
                        // keep other and move on
                        // row still imports
                    }
                }

                try {
                    // reuse normal create path, so it's basically a manual entry
                    transactionService.createTransaction(new CreateTransactionRequest(amount, label, null, date, categoryId, accountId), userId);
                    imported++;
                } catch (RuntimeException exception) {
                    failed++;
                }
            }
        } catch (IOException exception) {
            throw new InvalidCsvException("The file could not be read.");
        }

        return new CsvImportResponse(imported, skipped, failed);
    }

    // load the categories
    private Category findOtherCategory() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().filter(c -> DEFAULT_CATEGORY.equalsIgnoreCase(c.getCategoryLabel())).findFirst().orElseThrow(() -> new NotFoundException("No " + DEFAULT_CATEGORY + " category to import into"));
    }

    // finds column whose header has the word to matchy matchy
    private int columnNamed(String[] headers, String word) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().toLowerCase().contains(word)) {
                return i;
            }
        }
        return -1;
    }

    private LocalDate parseDate(String value) {
        String text = value.trim();
        for (DateTimeFormatter format : List.of(ISO, US)) {
            try {
                return LocalDate.parse(text, format);
            } catch (RuntimeException ignored) {
                // try the next format
            }
        }
        return null;
    }

    // strips the stuff banks put in amount columns ('$', ',')
    private BigDecimal parseAmount(String value) {
        String text = value.trim().replace("$", "").replace(",", "").replace("\"", "");
        if (text.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    // breaks up a line when a comma is found
    private String[] split(String line) {
        return line.split(",", -1);
    }
}
