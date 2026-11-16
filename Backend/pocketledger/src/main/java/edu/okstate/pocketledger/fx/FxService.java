package edu.okstate.pocketledger.fx;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import edu.okstate.pocketledger.common.NotFoundException;

// looks up historical exchange rate from outside provider
// wrap it instead of call from frontend so changing provider only changes this file
@Service 
public class FxService {
    
    // the .app domain redirects here
    private static final String PROVIDER = "https://api.frankfurter.dev/v2";

    private final RestClient restClient = RestClient.builder().baseUrl(PROVIDER).build();


    public FxRateResponse getRate(String from, String to, LocalDate date) {

        // currency codes are uppercase
        String fromCode = from.toUpperCase();
        String toCode = to.toUpperCase();

        // converting a currency to itself is 1
        if (fromCode.equals(toCode)) {
            return new FxRateResponse(fromCode, toCode, date, BigDecimal.ONE);
        }

        ProviderRate body;

        try {
            // GET url
            body = restClient.get()
            .uri("/rate/{base}/{quote}?date={date}", fromCode, toCode, date)
            .retrieve()
            .body(ProviderRate.class);
        } catch(HttpClientErrorException exception) {
            // provder answered but said no
            // 422 is a currency it doesn't recognize
            // 404 is a date or path it doesn't have
            throw new NotFoundException("No exchange rate for " + fromCode + " to " + toCode + " on " + date);
        } catch(RestClientException exception) {
            // provider didn't answer or sent something we can't read
            throw new FxUnavailableException();
        }

        // 200 with an unusable body is still something we can't use
        if (body == null || body.rate() == null) {
            throw new FxUnavailableException();
        }

        // pass back date from provider so the response always says which day the rate is actually from
        return new FxRateResponse(fromCode, toCode, body.date(), body.rate());
    }
}
