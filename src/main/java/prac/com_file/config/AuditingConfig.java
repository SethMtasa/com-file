package prac.com_file.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * This class configures Spring Data JPA Auditing.
 * The @EnableJpaAuditing annotation activates the auditing feature.
 * The auditorAware() bean provides the current user's name to the
 * AuditingEntityListener, which then populates the @CreatedBy/@LastModifiedBy fields.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AuditingConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("Anonymous User"); // Fallback for unauthenticated operations
            }

            // Assuming your UserDetails object returns the username via getName()
            return Optional.ofNullable(authentication.getName());
        };
    }
}
