package edu.okstate.pocketledger.fx;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

// format of what the provider sends (not what goes to frontend)
// date is the day the rate is actually from (might be earlier than when requested; skips weekends)
record ProviderRate(LocalDate date, String base, String quote, BigDecimal rate) { }
