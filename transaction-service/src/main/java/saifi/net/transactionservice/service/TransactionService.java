package saifi.net.transactionservice.service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final AccountGateway accounts;
    private final BankTransactionProperties props;

    /** Opération (débit) sur un compte : applique les règles métier de l'énoncé. */
    @Transactional
    public Transaction create(TransactionRequest req) {
        validateAmount(req.amount());

        AccountDto account = accounts.getAccount(req.accountId());   // consultation du solde
        checkActive(account);
        checkCurrency(account, req.currency());
        checkFunds(account, req.amount());

        Transaction tx = repository.save(newTransaction(req.accountId(), req.amount(),
                req.currency(), req.merchantCategory(), req.country()));

        // Mise à jour du solde APRÈS l'enregistrement. Si l'appel échoue, l'exception annule l'INSERT local.
        accounts.adjustBalance(req.accountId(), req.amount().negate());

        // Partie F : publier l'événement Kafka bank.transactions.created ici (après commit).
        log.info("Transaction {} enregistrée sur le compte {}", tx.getId(), tx.getAccountId());
        return tx;
    }

    /** Virement : débit du compte source, crédit du compte destination, compensation si le crédit échoue. */
    @Transactional
    public TransferResult transfer(TransferRequest req) {
        if (req.fromAccountId().equals(req.toAccountId())) {
            throw new BusinessRuleException("Les comptes source et destination doivent être différents");
        }
        validateAmount(req.amount());

        AccountDto from = accounts.getAccount(req.fromAccountId());
        AccountDto to = accounts.getAccount(req.toAccountId());
        checkActive(from);
        checkActive(to);
        checkCurrency(from, req.currency());
        checkCurrency(to, req.currency());
        checkFunds(from, req.amount());

        accounts.adjustBalance(from.id(), req.amount().negate());
        try {
            accounts.adjustBalance(to.id(), req.amount());
        } catch (RuntimeException e) {
            try {
                accounts.adjustBalance(from.id(), req.amount());      // compensation
            } catch (RuntimeException ce) {
                log.error("ECHEC DE COMPENSATION : {} doit être recrédité de {} {}",
                        from.id(), req.amount(), req.currency(), ce);
            }
            throw e;
        }

        Transaction debit = repository.save(newTransaction(from.id(), req.amount(),
                req.currency(), "TRANSFER_OUT", req.country()));
        Transaction credit = repository.save(newTransaction(to.id(), req.amount(),
                req.currency(), "TRANSFER_IN", req.country()));
        return new TransferResult(debit, credit);
    }

    @Transactional(readOnly = true)
    public Transaction getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public List<Transaction> listByAccount(UUID accountId) {
        return repository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    // ---------- règles métier ----------
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new BusinessRuleException("Le montant doit être strictement positif");
        }
        if (amount.compareTo(props.maxAmount()) > 0) {
            throw new BusinessRuleException("Le montant dépasse le plafond autorisé : " + props.maxAmount());
        }
    }

    private void checkActive(AccountDto a) {
        if (!"ACTIVE".equals(a.status())) {
            throw new BusinessRuleException("Le compte " + a.id() + " n'est pas actif");
        }
    }

    private void checkCurrency(AccountDto a, String currency) {
        if (currency == null || !a.currency().equalsIgnoreCase(currency)) {
            throw new BusinessRuleException("Devise " + currency + " différente de celle du compte "
                    + a.id() + " (" + a.currency() + ")");
        }
    }

    private void checkFunds(AccountDto a, BigDecimal amount) {
        if (BigDecimal.valueOf(a.balance()).compareTo(amount) < 0) {
            throw new BusinessRuleException("Solde insuffisant sur le compte " + a.id());
        }
    }

    private Transaction newTransaction(UUID accountId, BigDecimal amount, String currency,
                                       String merchantCategory, String country) {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .accountId(accountId)
                .amount(amount)
                .currency(currency.toUpperCase())
                .merchantCategory(merchantCategory)
                .country(country)
                .createdAt(Instant.now())
                .build();
    }
}

