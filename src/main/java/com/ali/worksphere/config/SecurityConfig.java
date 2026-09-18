package com.ali.worksphere.config;

import com.ali.worksphere.security.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        http.csrf(csrf -> csrf.disable());
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/auth/register", "/auth/login","/auth/refresh","/auth/logout").permitAll();
            auth.requestMatchers(HttpMethod.POST,"/employees").hasAnyRole("HR","ADMIN");
            auth.requestMatchers(HttpMethod.GET,"/employees").hasAnyRole("MANAGER","HR","ADMIN");
            auth.requestMatchers(HttpMethod.GET,"/employees/pending").hasAnyRole("HR","ADMIN");
            auth.requestMatchers(HttpMethod.POST, "/employees/*/approve").hasAnyRole("HR","ADMIN");
            auth.requestMatchers(HttpMethod.PUT,"/employees/**").hasAnyRole("HR","ADMIN");
            auth.requestMatchers(HttpMethod.DELETE,"/employees/**").hasRole("ADMIN");
            auth.requestMatchers(HttpMethod.GET,"/departments/*").hasAnyRole("MANAGER","HR","ADMIN");
            auth.requestMatchers(HttpMethod.GET,"/departments").hasAnyRole("MANAGER","HR","ADMIN");
            auth.requestMatchers(HttpMethod.POST,"/departments/*/manager").hasAnyRole("HR","ADMIN");
            auth.requestMatchers(HttpMethod.PUT, "/departments/*/manager").hasAnyRole("HR","ADMIN");
            auth.requestMatchers(HttpMethod.DELETE,"/departments/*/manager").hasAnyRole("HR","ADMIN");
            auth.requestMatchers(HttpMethod.POST,"/departments").hasRole("ADMIN");
            auth.requestMatchers(HttpMethod.PUT,"/departments/**").hasRole("ADMIN");
            auth.requestMatchers(HttpMethod.DELETE,"/departments/*").hasRole("ADMIN");
            auth.requestMatchers(HttpMethod.POST, "/projects").hasRole("MANAGER");
            auth.requestMatchers(HttpMethod.POST, "/projects/*/manager").hasRole("MANAGER");
            auth.requestMatchers(HttpMethod.PUT, "/projects/*/manager").hasRole("MANAGER");
            auth.requestMatchers(HttpMethod.DELETE, "/projects/*/manager").hasRole("MANAGER");
            auth.requestMatchers(HttpMethod.POST, "/projects/*/employees").hasRole("MANAGER");
            auth.requestMatchers(HttpMethod.DELETE, "/projects/*/employees/*").hasRole("MANAGER");
            auth.requestMatchers(HttpMethod.GET, "/projects/*").hasAnyRole("MANAGER", "HR", "ADMIN");
            auth.requestMatchers(HttpMethod.GET, "/projects").hasAnyRole("MANAGER", "HR", "ADMIN");
            auth.requestMatchers(HttpMethod.GET, "/projects/status/*").hasAnyRole("MANAGER", "HR", "ADMIN");
            auth.requestMatchers(HttpMethod.PUT, "/projects/*").hasRole("MANAGER");
            auth.requestMatchers(HttpMethod.DELETE, "/projects/*").hasRole("MANAGER");
            auth.requestMatchers(HttpMethod.GET,"/employees/me").authenticated();
            auth.requestMatchers(HttpMethod.GET, "/departments/me").hasAnyRole("EMPLOYEE","MANAGER","HR","ADMIN");
            auth.requestMatchers(HttpMethod.GET, "/projects/me").hasRole("EMPLOYEE");
            auth.requestMatchers(HttpMethod.GET,"/employees/my-team").hasRole("MANAGER");
            auth.requestMatchers(HttpMethod.GET,"/projects/my-department").hasRole("MANAGER");
            auth.anyRequest().authenticated();
        });

        http.exceptionHandling(exception -> {
            exception.authenticationEntryPoint((request, response, authException) -> {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized");
            });
        });

        return http.build();
    }
}
