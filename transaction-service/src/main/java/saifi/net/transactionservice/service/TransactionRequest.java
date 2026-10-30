package saifi.net.transactionservice.service;

public record TransactionRequest(
        @NotNull UUID accountId,
        @NotNull @Positive BigDecimal amount,
        @NotBlank String currency,
        String merchantCategory,
        String country) {}
