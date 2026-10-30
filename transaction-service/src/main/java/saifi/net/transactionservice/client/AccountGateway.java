package saifi.net.transactionservice.client;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountGateway {

    private final AccountClient client;

    @CircuitBreaker(name = "accountService", fallbackMethod = "getAccountFallback")
    @Retry(name = "accountService") // lecture idempotente : retry autorisé
    public AccountDto getAccount(UUID id) {
        return client.getAccount(id);
    }

    /** Pas de @Retry ici : un débit rejoué après timeout pourrait être appliqué deux fois. */
    @CircuitBreaker(name = "accountService", fallbackMethod = "adjustBalanceFallback")
    public AccountDto adjustBalance(UUID id, BigDecimal delta) {
        return client.adjustBalance(id, new BalanceAdjustment(delta));
    }

    // ---------- fallbacks ----------
    public AccountDto getAccountFallback(UUID id, Throwable t) {
        throw translate(id, t);
    }

    public AccountDto adjustBalanceFallback(UUID id, BigDecimal delta, Throwable t) {
        throw translate(id, t);
    }

    private RuntimeException translate(UUID id, Throwable t) {
        log.warn("Appel account-service en échec pour {} : {}", id, t.toString());
        if (t instanceof FeignException.NotFound) {
            return new ResourceNotFoundException("Compte introuvable : " + id);
        }
        if (t instanceof FeignException fe && fe.status() >= 400 && fe.status() < 500) {
            return new BusinessRuleException("Rejeté par account-service : " + fe.contentUTF8());
        }
        return new AccountServiceUnavailableException(
                "account-service est momentanément indisponible, réessayez plus tard", t);
    }
}
