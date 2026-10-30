package saifi.net.transactionservice.service;

import saifi.net.transactionservice.domain.Transaction;

public record TransferResult(Transaction debit, Transaction credit) {}
