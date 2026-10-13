package com.appointment.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

import com.appointment.webapp.security.CustomAuthenticationProvider;
import com.appointment.webapp.security.CustomRememberMeServices;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@PropertySource({ "classpath:messages_en.properties" })
public class SecurityConfiguration {
	@Value("${message.sessionExpired:Session expired. Please login again.}")
    String sessionExpired;

	@Value("${app.remember-me.key:}")
	private String rememberMeKey;

	@Autowired
	private javax.sql.DataSource dataSource;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private AuthenticationSuccessHandler myAuthenticationSuccessHandler;

	@Autowired
	private LogoutSuccessHandler myLogoutSuccessHandler;

	@Autowired
	private AuthenticationFailureHandler authenticationFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        	.csrf(csrf -> csrf
        		.csrfTokenRepository(new org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository())
        		// Load the token eagerly: with the deferred default, long pages (login) render the form after the
        		// response buffer has flushed and the token can no longer create a session.
        		.csrfTokenRequestHandler(eagerCsrfHandler())
        		.ignoringRequestMatchers("/api/whatsapp/webhook", "/api/whatsapp/webhook/**")  // Disable CSRF for WhatsApp webhooks
        	)
        	// Keep Spring Security's default headers (nosniff, HSTS, cache control); allow same-origin framing
        	.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
        	.authorizeHttpRequests(auth -> auth
        		.requestMatchers(
        			"/resources/**", "/static/**", "/adminlte/**",
        			"/css/**", "/js/**", "/images/**",
        			"/bootstrap/**", "/dist/**", "/plugins/**",
        			"/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.gif", "/**/*.ico"
        		).permitAll()
        		// WhatsApp Webhook endpoints (must be publicly accessible)
        		.requestMatchers("/api/whatsapp/webhook", "/api/whatsapp/webhook/**").permitAll()
        		.requestMatchers("/verify/rx/**", "/rx/pdf/**").permitAll()
                .requestMatchers("/login*", "/logout.html", "/favicon.ico", "/offline/*",
                		"/signin/**", "/signup/**", "/customLogin",
                        "/user/registration*", "/registrationConfirm*", "/expiredAccount*", "/registration*",
                        "/badUser*", "/user/resendRegistrationToken*", "/forgetPassword*", "/user/resetPassword*",
                        "/user/changePassword*", "/emailError*", "/old/user/registration*",
                        "/successRegister*", "/qrcode*", "/rest-template-service")
                .permitAll()
                .requestMatchers("/invalidSession*").anonymous()
                .requestMatchers("/user/updatePassword*", "/user/savePassword*", "/updatePassword*")
                	.hasAuthority("CHANGE_PASSWORD_PRIVILEGE")
                .anyRequest().authenticated()
        	)
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard/index")
                .failureUrl("/login?error=true")
                .successHandler(myAuthenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .permitAll()
            )
            .sessionManagement(session -> session
                .invalidSessionUrl("/login?message=" + sessionExpired)
                .sessionFixation().none()
                .maximumSessions(1)
                .sessionRegistry(sessionRegistry())
            )
            .logout(logout -> logout
                .logoutSuccessHandler(myLogoutSuccessHandler)
                .invalidateHttpSession(false)
                .logoutSuccessUrl("/logout.html?logSucc=true")
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .rememberMeServices(rememberMeServices())
                .key(rememberMeKey())
            );

        return http.build();
    }

    // Replace JDBC-backed persistent tokens with in-memory implementation to avoid deprecated JdbcTokenRepositoryImpl
    @Bean
    public PersistentTokenRepository tokenRepository() {
        return new InMemoryTokenRepositoryImpl();
    }

    // Boot 4 no longer registers this dialect automatically; without it every sec:authorize tag is ignored
    // and each role sees every other role's menu.
    @Bean
    public org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect springSecurityDialect() {
        return new org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect();
    }

    private static org.springframework.security.web.csrf.CsrfTokenRequestHandler eagerCsrfHandler() {
        org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler handler =
                new org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler();
        handler.setCsrfRequestAttributeName(null);
        return handler;
    }

    // Falls back to a random per-start key when none is configured (remember-me cookies then reset on restart)
    private String rememberMeKey() {
        if (rememberMeKey == null || rememberMeKey.isBlank()) {
            rememberMeKey = java.util.UUID.randomUUID().toString();
        }
        return rememberMeKey;
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        final CustomAuthenticationProvider authProvider = new CustomAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        CustomRememberMeServices rememberMeServices = new CustomRememberMeServices(rememberMeKey(), userDetailsService, tokenRepository());
        return rememberMeServices;
    }

    @Bean
    public HttpFirewall defaultHttpFirewall() {
        return new DefaultHttpFirewall();
    }

    // Completely bypass security for static resources
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
            "/resources/**", "/static/**", "/css/**", "/js/**", "/images/**",
            "/plugins/**", "/adminlte/**", "/bootstrap/**", "/dist/**",
            "/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.gif", "/**/*.ico", "/**/*.woff", "/**/*.woff2", "/**/*.ttf"
        );
    }
}
