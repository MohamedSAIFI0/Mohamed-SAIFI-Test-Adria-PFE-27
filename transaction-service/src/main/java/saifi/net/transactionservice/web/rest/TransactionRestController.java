package saifi.net.transactionservice.web.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import saifi.net.transactionservice.domain.Transaction;
import saifi.net.transactionservice.service.TransactionRequest;
import saifi.net.transactionservice.service.TransactionService;
import saifi.net.transactionservice.service.TransferRequest;
import saifi.net.transactionservice.service.TransferResult;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionRestController {

    private final TransactionService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction create(@Valid @RequestBody TransactionRequest request) {
        return service.create(request);
    }

    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferResult transfer(@Valid @RequestBody TransferRequest request) {
        return service.transfer(request);
    }

    @GetMapping("/{id}")
    public Transaction get(@PathVariable UUID id) { return service.getById(id); }

    @GetMapping
    public List<Transaction> listByAccount(@RequestParam UUID accountId) {
        return service.listByAccount(accountId);
    }
}
