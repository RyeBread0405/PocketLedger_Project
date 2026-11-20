package edu.okstate.pocketledger.ai;

// outcome of one transaction
// source is "ai" when Gemini responds and "default" when it doesn't and we use Other
// confidence is null, we just get category but no score
public record CategorizeResult(Long id, Integer categoryId, String source, Double confidence) { }
