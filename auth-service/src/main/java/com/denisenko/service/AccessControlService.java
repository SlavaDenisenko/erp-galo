package com.denisenko.service;

import com.denisenko.config.AccessRulesConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

@ApplicationScoped
public class AccessControlService {
    private static final Logger log = LoggerFactory.getLogger(AccessControlService.class);

    @Inject
    AccessRulesConfig config;

    public boolean isAllowed(String path, String method, String role) {
        boolean result = config.rules().stream()
                .filter(r -> method.equalsIgnoreCase(r.method()) && pathMatches(path, r.path()))
                .anyMatch(r -> r.roles().contains(role.toLowerCase(Locale.ROOT)));

        log.debug("Access check: method={} path={} role={} => {}", method, path, role, result);
        return result;
    }

    private boolean pathMatches(String requestedPath, String rulePath) {
        String regex = rulePath.replaceAll("\\{[^/]+}", "[^/]+");
        return requestedPath.split("\\?")[0].matches(regex);
    }
}
