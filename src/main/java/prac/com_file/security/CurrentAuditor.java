package prac.com_file.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import prac.com_file.model.User;

import java.util.Optional;

@Component
public class CurrentAuditor implements AuditorAware<User> {

    private static final Logger log = LoggerFactory.getLogger(CurrentAuditor.class);

    @Override
    public Optional<User> getCurrentAuditor() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                if (authentication.getPrincipal() instanceof User user) {
                    return Optional.of(user);
                }
            }
        } catch (Exception e) {
            log.error("Authentication:{}", e.getMessage());
        }
        return Optional.empty();
    }

    public User getLoggedInUserOrThrow() throws Exception {
        return getCurrentAuditor().orElseThrow(() -> new Exception("Invalid login user"));
    }

    public String getUsernameOrThrow() throws Exception {
        return getLoggedInUserOrThrow().getUsername();
    }
}
