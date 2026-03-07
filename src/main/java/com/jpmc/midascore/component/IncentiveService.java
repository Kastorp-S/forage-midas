package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class IncentiveService {

    private final RestTemplate restTemplate;
    private final String incentiveApiUrl = "http://localhost:8080/incentive";

    public IncentiveService() {
        this.restTemplate = new RestTemplate();
    }

    public float getIncentive(Transaction transaction) {
        try {
            Incentive incentive = restTemplate.postForObject(incentiveApiUrl, transaction, Incentive.class);
            if (incentive != null) {
                return incentive.getAmount();
            }
        } catch (Exception e) {
            System.out.println("Incentive API error: " + e.getMessage());
        }
        return 0;
    }
}
