package com.example.userservice.security;

import com.example.userservice.service.UserService;
import com.example.userservice.service.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

import static org.springframework.security.web.server.authorization.IpAddressReactiveAuthorizationManager.hasIpAddress;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurity {

    private final Environment env;
    private final UserServiceImpl userService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.
                    csrf().disable().headers()
                    .frameOptions().sameOrigin()
                .and()
                    .csrf().ignoringRequestMatchers("/h2-console/**").disable()
                    .authorizeHttpRequests().requestMatchers("/h2-console/**").permitAll()
                .and()
                    .authorizeHttpRequests()
                    .anyRequest().permitAll()
                .and()
                    .addFilter(getAuthenticationFilter());
                //.access(hasIpAddress("192.168.0.8"))

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager() throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider(){
        return new CustomAuthenticationProvider(userService, bCryptPasswordEncoder);
    }


    private static AuthorizationManager<RequestAuthorizationContext> hasIpAddress(String ipAddress) {
        IpAddressMatcher ipAddressMatcher = new IpAddressMatcher(ipAddress);
        return (authentication, context) -> {
            HttpServletRequest request = context.getRequest();
            return new AuthorizationDecision(ipAddressMatcher.matches(request));
        };
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        authenticationFilter.setAuthenticationManager(authenticationManager());

        return authenticationFilter;
    }
}
