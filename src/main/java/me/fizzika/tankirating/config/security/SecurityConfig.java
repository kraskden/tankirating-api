package me.fizzika.tankirating.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Resource
    private CaptchaFilter captchaFilter;

    @Value("${app.admin.users}")
    private String adminUsersConfig;

    @Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(withDefaults())
                .formLogin().disable()
                .cors().and().csrf().disable()
                .authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .addFilterBefore(captchaFilter, AnonymousAuthenticationFilter.class)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .build();
    }

    @Bean
    public UserDetailsService users() {
        List<UserDetails> users = Arrays.stream(adminUsersConfig.split(";"))
                .map(this::buildAdminUser)
                .collect(toList());
        return new InMemoryUserDetailsManager(users);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private UserDetails buildAdminUser(String userConfig) {
        String[] loginPswdPair = userConfig.split(":");
        return User.builder()
                .roles("ADMIN")
                .username(loginPswdPair[0])
                .password(passwordEncoder().encode(loginPswdPair[1]))
                .build();
    }
}
