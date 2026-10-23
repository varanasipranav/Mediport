package com.appointment.webapp.tenancy;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Every request that touches clinic data must carry a site id. The site id comes only from the signed-in user's session
 * (never from a header or parameter). No site id => the request is blocked.
 * Public paths (login, webhook, signed Rx links) work without a session and find their clinic another way.
 */
public class SiteFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SiteFilter.class);

    private static final java.util.regex.Pattern PUBLIC_RX = java.util.regex.Pattern.compile("^/(rx/pdf|verify/rx)(?:/s/([a-z][a-z0-9]{1,29}))?/[0-9]+/.*$");

    private final TenantRegistry registry;
    private final String legacySiteId;

    public SiteFilter(TenantRegistry registry, String legacySiteId) {
        this.registry = registry;
        this.legacySiteId = legacySiteId;
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean signedIn = auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof TenantUser;

        if (!signedIn) {
            // Signed prescription links are public: the clinic is named in the link (/s/{site}/...), and old links
            // without it belong to the first clinic. The link's signature is checked with that clinic's own key.
            var m = PUBLIC_RX.matcher(request.getRequestURI().substring(request.getContextPath().length()));
            if (m.matches()) {
                String site = m.group(2) != null ? m.group(2) : legacySiteId;
                TenantInfo clinic = registry.find(site).filter(TenantInfo::active).orElse(null);
                if (clinic == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.setContentType("text/plain");
                    response.getWriter().write("Not found");
                    return;
                }
                try {
                    TenantContext.set(clinic);
                    chain.doFilter(request, response);
                } finally {
                    TenantContext.clear();
                }
                return;
            }
            // Everything else without a signed-in user is stopped by Spring Security's authorization rules
            // (redirect to /login) or fails closed on its first database access. Nothing here touches a clinic database.
            chain.doFilter(request, response);
            return;
        }

        TenantUser user = (TenantUser) auth.getPrincipal();
        TenantInfo tenant = registry.find(user.getSiteId()).orElse(null);
        if (tenant == null || !tenant.active()) {
            log.warn("Blocked request: clinic {} is unavailable", user.getSiteId());
            var session = request.getSession(false);
            if (session != null) session.invalidate();
            SecurityContextHolder.clearContext();
            response.sendRedirect(request.getContextPath() + "/login?error=clinic");
            return;
        }
        try {
            TenantContext.set(tenant);
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
