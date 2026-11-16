package edu.okstate.pocketledger.csv;

import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


// POST /api/import with a CSV file and an accountId
// file arrives as multipart form because JSON doesn't carry a file
@RestController
@RequestMapping("/api/import")
public class CsvImportController {
    
    private final CsvImportService csvImportService;

    public CsvImportController(CsvImportService csvImportService) {
        this.csvImportService = csvImportService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CsvImportResponse importCsv(@RequestParam("file") MultipartFile file, @RequestParam Long accountId, @RequestParam(defaultValue = "false") boolean categorize, JwtAuthenticationToken authentication) {
        Integer userId = Integer.valueOf(authentication.getToken().getSubject());


        return csvImportService.importCsv(file, accountId, userId, categorize);
    }
}
