package edu.okstate.pocketledger.ai;

import java.util.List;

// contract wraps list in an outer object
public record CategorizeResponse(List<CategorizeResult> results) { }
