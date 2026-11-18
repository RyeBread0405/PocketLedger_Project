package edu.okstate.pocketledger.ai;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;


// body of POST /api/ai/categorize
// caller picks which transactions to re-categorize
public record CategorizeRequest(@NotEmpty List<Long> transactionIds) { }
