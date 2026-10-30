package saifi.net.transactionservice.web.graphql;

import saifi.net.transactionservice.domain.Transaction;
import saifi.net.transactionservice.service.TransactionRequest;
import saifi.net.transactionservice.service.TransactionService;
import saifi.net.transactionservice.service.TransferRequest;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class TransactionGraphQLController {

    private final TransactionService service;

    @QueryMapping
    public Transaction transactionById(@Argument UUID id) { return service.getById(id); }

    @QueryMapping
    public List<Transaction> transactionsByAccount(@Argument UUID accountId) {
        return service.listByAccount(accountId);
    }

    @MutationMapping
    public Transaction createTransaction(@Argument TransactionRequest input) {
        return service.create(input);
    }

    @MutationMapping
    public TransferResult transfer(@Argument TransferRequest input) {
        return service.transfer(input);
    }
}
