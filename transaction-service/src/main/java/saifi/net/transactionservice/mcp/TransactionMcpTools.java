package saifi.net.transactionservice.mcp;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import saifi.net.transactionservice.domain.Transaction;
import saifi.net.transactionservice.service.TransactionRequest;
import saifi.net.transactionservice.service.TransactionService;
import saifi.net.transactionservice.service.TransferRequest;
import saifi.net.transactionservice.service.TransferResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionMcpTools {

    private final TransactionService service;

    @Tool(description = "Enregistre une transaction (débit) sur un compte. Le montant doit être > 0 et <= plafond, "
            + "le compte actif, la devise identique à celle du compte et le solde suffisant.")
    public Transaction createTransaction(
            @ToolParam(description = "UUID du compte") String accountId,
            @ToolParam(description = "Montant strictement positif") double amount,
            @ToolParam(description = "Devise ISO, ex: EUR") String currency,
            @ToolParam(description = "Catégorie du commerçant, ex: GROCERY", required = false) String merchantCategory,
            @ToolParam(description = "Code pays, ex: MA", required = false) String country) {
        return service.create(new TransactionRequest(UUID.fromString(accountId),
                BigDecimal.valueOf(amount), currency, merchantCategory, country));
    }

    @Tool(description = "Effectue un virement d'un compte vers un autre compte de même devise")
    public TransferResult transfer(
            @ToolParam(description = "UUID du compte source") String fromAccountId,
            @ToolParam(description = "UUID du compte destination") String toAccountId,
            @ToolParam(description = "Montant strictement positif") double amount,
            @ToolParam(description = "Devise ISO, ex: EUR") String currency) {
        return service.transfer(new TransferRequest(UUID.fromString(fromAccountId),
                UUID.fromString(toAccountId), BigDecimal.valueOf(amount), currency, null));
    }

    @Tool(description = "Retourne une transaction à partir de son UUID")
    public Transaction getTransaction(@ToolParam(description = "UUID de la transaction") String transactionId) {
        return service.getById(UUID.fromString(transactionId));
    }

    @Tool(description = "Liste les transactions d'un compte, de la plus récente à la plus ancienne")
    public List<Transaction> listTransactions(@ToolParam(description = "UUID du compte") String accountId) {
        return service.listByAccount(UUID.fromString(accountId));
    }
}
