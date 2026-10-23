package com.appointment.webapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Slows down password guessing: after too many failed sign-ins for the same username from the same address
 * (or from one address overall) further attempts are refused for a while. In memory, per instance.
 */
@Component
public class LoginThrottle {

    private static final long WINDOW_MS = 15 * 60_000L;
    private static final int MAX_PER_USER = 8;
    private static final int MAX_PER_ADDRESS = 40;

    private record Hits(long start, int count) {
    }

    private final ConcurrentHashMap<String, Hits> hits = new ConcurrentHashMap<>();

    private static String userKey(String ip, String username) {
        return "u|" + ip + "|" + (username == null ? "" : username.trim().toLowerCase(Locale.ROOT));
    }

    private static String ipKey(String ip) {
        return "i|" + ip;
    }

    public boolean blocked(String ip, String username) {
        return count(userKey(ip, username)) >= MAX_PER_USER || count(ipKey(ip)) >= MAX_PER_ADDRESS;
    }

    public void failed(String ip, String username) {
        bump(userKey(ip, username));
        bump(ipKey(ip));
    }

    public void succeeded(String ip, String username) {
        hits.remove(userKey(ip, username));
    }

    private int count(String key) {
        Hits h = hits.get(key);
        return h == null || System.currentTimeMillis() - h.start > WINDOW_MS ? 0 : h.count;
    }

    private void bump(String key) {
        long now = System.currentTimeMillis();
        if (hits.size() > 20_000) hits.values().removeIf(h -> now - h.start > WINDOW_MS);
        hits.merge(key, new Hits(now, 1), (old, one) -> now - old.start > WINDOW_MS ? one : new Hits(old.start, old.count + 1));
    }

    /** Refuses sign-in attempts from a blocked address/username before any password is checked. */
    public OncePerRequestFilter filter() {
        return new OncePerRequestFilter() {
            @Override
            protected boolean shouldNotFilter(HttpServletRequest request) {
                return !"POST".equalsIgnoreCase(request.getMethod()) || !request.getServletPath().equals("/login");
            }

            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                    throws ServletException, IOException {
                if (blocked(request.getRemoteAddr(), request.getParameter("username"))) {
                    response.sendRedirect(request.getContextPath() + "/login?error=locked");
                    return;
                }
                chain.doFilter(request, response);
            }
        };
    }
}
