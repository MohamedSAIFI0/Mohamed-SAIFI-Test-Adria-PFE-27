package saifi.net.accountservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import saifi.net.accountservice.repository.AccountRepository;
import saifi.net.accountservice.service.AccountService;

@Slf4j
@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seed(AccountRepository repo, AccountService service) {
        return args -> {
            if (repo.count() > 0) return;
            var a = service.create("CUST-001", "EUR", 2000);
            var b = service.create("CUST-002", "EUR", 500);
            var c = service.create("CUST-003", "MAD", 10000);
            log.info("Comptes de démo : {} | {} | {}", a.getId(), b.getId(), c.getId());
        };
    }
}