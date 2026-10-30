package saifi.net.accountservice.mcp;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import saifi.net.accountservice.domain.Account;
import saifi.net.accountservice.service.AccountService;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountMcpTools {

    private final AccountService service;

    @Tool(description = "Liste tous les comptes bancaires (id, référence client, statut, devise, solde)")
    public List<Account> listAccounts() { return service.list(); }

    @Tool(description = "Retourne un compte bancaire à partir de son identifiant UUID")
    public Account getAccount(@ToolParam(description = "UUID du compte") String accountId) {
        return service.getById(UUID.fromString(accountId));
    }

    @Tool(description = "Retourne le solde courant d'un compte bancaire")
    public double getAccountBalance(@ToolParam(description = "UUID du compte") String accountId) {
        return service.getBalance(UUID.fromString(accountId));
    }

    @Tool(description = "Crée un nouveau compte bancaire actif")
    public Account createAccount(
            @ToolParam(description = "Référence du client") String customerReference,
            @ToolParam(description = "Code devise ISO sur 3 lettres, ex: EUR, MAD") String currency,
            @ToolParam(description = "Solde initial, >= 0") double initialBalance) {
        return service.create(customerReference, currency, initialBalance);
    }

    @Tool(description = "Bloque un compte bancaire")
    public Account blockAccount(@ToolParam(description = "UUID du compte") String accountId) {
        return service.block(UUID.fromString(accountId));
    }

    @Tool(description = "Réactive un compte bancaire bloqué")
    public Account activateAccount(@ToolParam(description = "UUID du compte") String accountId) {
        return service.activate(UUID.fromString(accountId));
    }
}

