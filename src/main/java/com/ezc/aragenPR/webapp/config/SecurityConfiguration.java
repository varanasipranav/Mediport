package com.ezc.aragenPR.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

import com.ezc.aragenPR.webapp.security.CustomAuthenticationProvider;
import com.ezc.aragenPR.webapp.security.CustomRememberMeServices;

@Configuration
@EnableWebSecurity
@PropertySource({ "classpath:messages_en.properties" })
public class SecurityConfiguration {
	@Value("${message.sessionExpired}") 
    String sessionExpired;
    
	 @Autowired
	 PersistenceJPAConfig pJPAConfig;
	 //private DataSource dataSource;;
	 
	
  // private CustomUserServiceImpl userDetailsService;
	@Autowired
   private UserDetailsService userDetailsService;

   @Autowired
   private AuthenticationSuccessHandler myAuthenticationSuccessHandler;

   @Autowired
   private LogoutSuccessHandler myLogoutSuccessHandler;

   @Autowired
   private AuthenticationFailureHandler authenticationFailureHandler;

//   @Autowired
//   private CustomWebAuthenticationDetailsSource authenticationDetailsSource;

//   @Autowired
//   private UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
//        	.csrf().disable() 
        .headers()
        //.contentSecurityPolicy("script-src 'self'  https://stackpath.bootstrapcdn.com https://code.jquery.com https://cdnjs.cloudflare.com 'unsafe-eval' 'unsafe-inline'; object-src 'self' https://stackpath.bootstrapcdn.com https://code.jquery.com https://cdnjs.cloudflare.com")

        //.and()
		//.addHeaderWriter(new StaticHeadersWriter("X-Content-Security-Policy","script-src 'self'"))
		.and()
        	     .authorizeRequests()
        	     .requestMatchers("/bootstrap/**", "/dist/**", "/plugins/**").permitAll()
                .requestMatchers("/login*","/login*","/favicon.ico" ,"/offline/*",
                		//"/logout*",
                		"/signin/**", "/signup/**", "/customLogin",
                        "/user/registration*", "/registrationConfirm*", "/expiredAccount*", "/registration*",
                        "/badUser*", "/user/resendRegistrationToken*" ,"/forgetPassword*", "/user/resetPassword*",
                        "/user/changePassword*", "/emailError*", "/resources/**","/old/user/registration*","/successRegister*","/qrcode*","/rest-template-service"
                       )
                .permitAll()
                .requestMatchers("/invalidSession*").anonymous()
                .requestMatchers("/user/updatePassword*","/user/savePassword*","/updatePassword*")
                .hasAuthority("CHANGE_PASSWORD_PRIVILEGE")
                .requestMatchers("/user/**","/priceapr/**","/modal/**").fullyAuthenticated()	
                
                //.hasAuthority("READ_PRIVILEGE")
                .and()
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard/index")
                .failureUrl("/login?error=true")
                .successHandler(myAuthenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                //.authenticationDetailsSource(authenticationDetailsSource)
            .permitAll()
                .and()
            .sessionManagement()
                .invalidSessionUrl("/login?message="+sessionExpired)
                .maximumSessions(1).sessionRegistry(sessionRegistry()).and()
                .sessionFixation().none()
            .and()
            .logout()
                .logoutSuccessHandler(myLogoutSuccessHandler)
                .invalidateHttpSession(false)
                .logoutSuccessUrl("/logout.html?logSucc=true")
                .deleteCookies("JSESSIONID")
                .permitAll()
             .and()
                .rememberMe()
                .rememberMeServices(rememberMeServices()).key("theKey");
        return http.build();
    }
    @Bean
    public PersistentTokenRepository tokenRepository() {
      JdbcTokenRepositoryImpl jdbcTokenRepositoryImpl=new JdbcTokenRepositoryImpl();
      jdbcTokenRepositoryImpl.setDataSource(pJPAConfig.dataSource());
      return jdbcTokenRepositoryImpl;
    }
    
    @Bean
    public DaoAuthenticationProvider authProvider() {
        final CustomAuthenticationProvider authProvider = new CustomAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
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
        CustomRememberMeServices rememberMeServices = new CustomRememberMeServices("theKey", userDetailsService, tokenRepository()); //new InMemoryTokenRepositoryImpl()
        return rememberMeServices;
    }
    
    @Bean
    public HttpFirewall defaultHttpFirewall() {
        return new DefaultHttpFirewall();
    }

}