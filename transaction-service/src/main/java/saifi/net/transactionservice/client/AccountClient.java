package saifi.net.transactionservice.client;

import java.util.UUID;

@FeignClient(name = "account-service", url = "${clients.account-service.url:}")
public interface AccountClient {

    @GetMapping("/api/accounts/{id}")
    AccountDto getAccount(@PathVariable("id") UUID id);

    @PutMapping("/api/accounts/{id}/balance")
    AccountDto adjustBalance(@PathVariable("id") UUID id, @RequestBody BalanceAdjustment adjustment);
}

