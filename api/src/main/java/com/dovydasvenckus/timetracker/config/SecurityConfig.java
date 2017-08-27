package com.dovydasvenckus.timetracker.config;

import com.dovydasvenckus.timetracker.security.Role;
import com.dovydasvenckus.timetracker.user.UserCreateDTO;
import com.dovydasvenckus.timetracker.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.stream.Stream;

@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private Environment environment;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .and().authorizeRequests()
                .antMatchers("/", "/public/**").permitAll()
                .anyRequest().fullyAuthenticated()
                .and()
                .csrf().disable()
                .formLogin()
                .usernameParameter("email")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .deleteCookies("remember-me")
                .logoutSuccessUrl("/")
                .permitAll()
                .and()
                .rememberMe();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        Stream<String> activeProfiles = Arrays.stream(environment.getActiveProfiles());
        boolean isDevProfileActive = activeProfiles.
                filter(profile -> "dev".equalsIgnoreCase(profile))
                .findAny()
                .isPresent();

        if (isDevProfileActive) {
            createDummyUser();
        }

        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    private void createDummyUser() {
        UserCreateDTO user = new UserCreateDTO();
        user.setEmail("test@mail.com");
        user.setPassword("test");
        user.setPasswordRepeated("test");
        user.setRole(Role.ADMIN);
        userService.create(user);
    }
}
