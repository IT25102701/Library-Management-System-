package com.sliit.smartlibrary.config;

import com.sliit.smartlibrary.enums.AccountStatus;
import com.sliit.smartlibrary.repository.UserRepository;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.*;

@Configuration
public class SecurityConfig {
    @Bean
    UserDetailsService userDetailsService(UserRepository users) {
        return username -> users.findByEmailIgnoreCase(username).map(u -> {
            if (u.getStatus() != AccountStatus.ACTIVE) throw new UsernameNotFoundException("Account is inactive");
            return User.withUsername(u.getEmail()).password(u.getPassword()).roles(u.getRole().name()).build();
        }).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }
    @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration c) throws Exception { return c.getAuthenticationManager(); }
    @Bean SecurityContextRepository securityContextRepository(){ return new HttpSessionSecurityContextRepository(); }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/browse.html", "/book-detail.html", "/resources.html", "/login.html", "/register.html", "/css/**", "/js/**", "/assets/**", "/api/auth/**", "/api/public/**").permitAll()
                .requestMatchers("/admin-dashboard.html", "/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/librarian-dashboard.html", "/api/staff/**").hasAnyRole("ADMIN","LIBRARIAN")
                .requestMatchers("/member-dashboard.html", "/api/member/**").authenticated()
                .anyRequest().permitAll())
            .formLogin(f -> f.disable())
            .httpBasic(b -> b.disable())
            .logout(l -> l.disable());
        return http.build();
    }
}
