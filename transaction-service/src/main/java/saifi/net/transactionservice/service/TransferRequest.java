package saifi.net.transactionservice.service;

public record TransferRequest(
        @NotNull UUID fromAccountId,
        @NotNull UUID toAccountId,
        @NotNull @Positive BigDecimal amount,
        @NotBlank String currency,
        String country) {}
