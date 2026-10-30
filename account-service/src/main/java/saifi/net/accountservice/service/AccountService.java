package saifi.net.accountservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saifi.net.accountservice.domain.Account;
import saifi.net.accountservice.domain.AccountStatus;
import saifi.net.accountservice.exception.BusinessRuleException;
import saifi.net.accountservice.exception.ResourceNotFoundException;
import saifi.net.accountservice.repository.AccountRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;

    @Transactional
    public Account create(String customerReference, String currency, double initialBalance) {
        if (initialBalance < 0) {
            throw new BusinessRuleException("Le solde initial ne peut pas être négatif");
        }
        Account account = Account.builder()
                .id(UUID.randomUUID())
                .customerReference(customerReference)
                .status(AccountStatus.ACTIVE)
                .currency(currency.toUpperCase())
                .balance(initialBalance)
                .build();
        return repository.save(account);
    }

    @Transactional(readOnly = true)
    public Account getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public List<Account> list() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public double getBalance(UUID id) {
        return getById(id).getBalance();
    }

    @Transactional
    public Account adjustBalance(UUID id, BigDecimal delta) {
        Account account = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte introuvable : " + id));
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessRuleException("Le compte " + id + " est bloqué");
        }
        BigDecimal newBalance = BigDecimal.valueOf(account.getBalance())
                .add(delta)
                .setScale(2, RoundingMode.HALF_UP);
        if (newBalance.signum() < 0) {
            throw new BusinessRuleException("Solde insuffisant sur le compte " + id);
        }
        account.setBalance(newBalance.doubleValue());
        return repository.save(account);
    }

    @Transactional
    public Account block(UUID id) {
        Account a = getById(id);
        a.setStatus(AccountStatus.BLOCKED);
        return repository.save(a);
    }

    @Transactional
    public Account activate(UUID id) {
        Account a = getById(id);
        a.setStatus(AccountStatus.ACTIVE);
        return repository.save(a);
    }
}
