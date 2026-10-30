package saifi.net.accountservice.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import saifi.net.accountservice.domain.Account;
import saifi.net.accountservice.service.AccountService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountRestController {

    private final AccountService service;

    public record CreateAccountRequest(
            @NotBlank String customerReference,
            @NotBlank @Size(min = 3, max = 3) String currency,
            @PositiveOrZero Double initialBalance) {}

    public record BalanceAdjustment(@NotNull BigDecimal delta) {}

    public record BalanceResponse(UUID accountId, String currency, double balance) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account create(@Valid @RequestBody CreateAccountRequest r) {
        return service.create(r.customerReference(), r.currency(),
                r.initialBalance() == null ? 0 : r.initialBalance());
    }

    @GetMapping
    public List<Account> list() { return service.list(); }

    @GetMapping("/{id}")
    public Account get(@PathVariable UUID id) { return service.getById(id); }

    @GetMapping("/{id}/balance")
    public BalanceResponse balance(@PathVariable UUID id) {
        Account a = service.getById(id);
        return new BalanceResponse(a.getId(), a.getCurrency(), a.getBalance());
    }

    /** Appelé par transaction-service (Feign). */
    @PutMapping("/{id}/balance")
    public Account adjustBalance(@PathVariable UUID id, @Valid @RequestBody BalanceAdjustment body) {
        return service.adjustBalance(id, body.delta());
    }

    @PostMapping("/{id}/block")
    public Account block(@PathVariable UUID id) { return service.block(id); }

    @PostMapping("/{id}/activate")
    public Account activate(@PathVariable UUID id) { return service.activate(id); }
}
