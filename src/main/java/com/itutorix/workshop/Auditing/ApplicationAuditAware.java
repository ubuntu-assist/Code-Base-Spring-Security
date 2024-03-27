package com.itutorix.workshop.Auditing;

import com.itutorix.workshop.user.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class ApplicationAuditAware implements AuditorAware<Integer> {

    /**
     * This method returns the current authenticated user's ID as an {@link Optional} object.
     * If there is no authenticated user or the user is anonymous, it returns an empty {@link Optional}.
     *
     * @return an {@link Optional} containing the authenticated user's ID, or an empty {@link Optional} if no user is authenticated or the user is anonymous.
     */
    @Override
    public Optional<Integer> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        User userPrincipal = (User) authentication.getPrincipal();
        return Optional.ofNullable(userPrincipal.getId());
    }
}
