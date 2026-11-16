package edu.okstate.pocketledger.fx;

import java.math.BigDecimal;
import java.time.LocalDate;

// what GET /api/fx/rate sends back (contract)
public record FxRateResponse(String from, String to, LocalDate date, BigDecimal rate) { }
