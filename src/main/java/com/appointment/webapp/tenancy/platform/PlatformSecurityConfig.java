package com.appointment.webapp.tenancy.platform;

import com.appointment.webapp.tenancy.MasterRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.util.List;

/**
 * The operator console (/platform/**) is a separate security world: its own login, its own operators in the master
 * database, and its own slot in the session so signing in here never touches a clinic login in the same browser.
 */
@Configuration
@ConditionalOnProperty(name = "app.tenancy.enabled", havingValue = "true")
public class PlatformSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain platformChain(HttpSecurity http, MasterRepository master) throws Exception {
        UserDetailsService users = username -> master.findPlatformUser(username)
                .map(u -> (org.springframework.security.core.userdetails.UserDetails) new User(u[0], u[1], List.of(new SimpleGrantedAuthority("ROLE_PLATFORM_ADMIN"))))
                .orElseThrow(() -> new UsernameNotFoundException("Unknown operator"));
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(users);
        provider.setPasswordEncoder(new BCryptPasswordEncoder(11));

        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.setSpringSecurityContextKey("PLATFORM_SECURITY_CONTEXT");

        var csrf = new org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler();
        csrf.setCsrfRequestAttributeName(null);

        http.securityMatcher("/platform/**")
                .authenticationProvider(provider)
                .securityContext(c -> c.securityContextRepository(repo))
                .csrf(c -> c.csrfTokenRepository(new org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository())
                        .csrfTokenRequestHandler(csrf))
                .headers(h -> h.frameOptions(f -> f.deny()).cacheControl(cc -> { }))
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/platform/login").permitAll()
                        .anyRequest().hasRole("PLATFORM_ADMIN"))
                .formLogin(f -> f.loginPage("/platform/login").loginProcessingUrl("/platform/login")
                        .defaultSuccessUrl("/platform", true).failureUrl("/platform/login?error"))
                .logout(l -> l.logoutUrl("/platform/logout").invalidateHttpSession(false)
                        .logoutSuccessUrl("/platform/login?logout"))
                .sessionManagement(s -> s.sessionFixation(f -> f.none()));
        return http.build();
    }
}
