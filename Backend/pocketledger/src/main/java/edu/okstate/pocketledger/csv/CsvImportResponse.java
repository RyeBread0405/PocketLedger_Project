package edu.okstate.pocketledger.csv;


// what POST /api/import sends back
// imported = saved, skipped = row we couldn't use, failed = row that errored
public record CsvImportResponse(int imported, int skipped, int failed) { }