package saifi.net.transactionservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "account-service", url = "${clients.account-service.url:}")
public interface AccountClient {

    @GetMapping("/api/accounts/{id}")
    AccountDto getAccount(@PathVariable("id") UUID id);

    @PutMapping("/api/accounts/{id}/balance")
    AccountDto adjustBalance(@PathVariable("id") UUID id, @RequestBody BalanceAdjustment adjustment);
}

