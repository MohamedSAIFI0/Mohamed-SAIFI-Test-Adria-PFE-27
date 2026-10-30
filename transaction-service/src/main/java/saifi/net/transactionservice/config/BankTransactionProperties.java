package saifi.net.transactionservice.config;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "bank.transaction")
public record BankTransactionProperties(BigDecimal maxAmount) {}
