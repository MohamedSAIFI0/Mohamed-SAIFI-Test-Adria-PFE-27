package saifi.net.accountservice.web.graphql;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import saifi.net.accountservice.domain.Account;
import saifi.net.accountservice.service.AccountService;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AccountGraphQLController {

    private final AccountService service;

    public record CreateAccountInput(String customerReference, String currency, Double initialBalance) {}

    @QueryMapping
    public List<Account> accounts() { return service.list(); }

    @QueryMapping
    public Account accountById(@Argument UUID id) { return service.getById(id); }

    @QueryMapping
    public double accountBalance(@Argument UUID id) { return service.getBalance(id); }

    @MutationMapping
    public Account createAccount(@Argument CreateAccountInput input) {
        return service.create(input.customerReference(), input.currency(),
                input.initialBalance() == null ? 0 : input.initialBalance());
    }

    @MutationMapping
    public Account blockAccount(@Argument UUID id) { return service.block(id); }

    @MutationMapping
    public Account activateAccount(@Argument UUID id) { return service.activate(id); }
}
