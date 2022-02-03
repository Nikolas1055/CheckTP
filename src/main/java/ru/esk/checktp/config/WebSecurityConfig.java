package ru.esk.checktp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import ru.esk.checktp.component.LoginSuccessHandler;
import ru.esk.checktp.service.UserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final LoginSuccessHandler loginSuccessHandler;

    @Autowired
    public WebSecurityConfig(LoginSuccessHandler loginSuccessHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable();
        http
                .sessionManagement()
                .maximumSessions(1)
                .expiredUrl("/login?invalid-session=true");

        http
                .authorizeRequests()
                .antMatchers("/login", "/webjars/**")
                .permitAll();

        http
                .authorizeRequests()
                .antMatchers("/index/**", "/")
                .authenticated();

        http
                .authorizeRequests()
                .antMatchers("/admin/**")
                .access("hasRole('ROLE_ADMIN')");

        http
                .authorizeRequests()
                .antMatchers("/chief/**")
                .access("hasAnyRole('ROLE_CHIEF', 'ROLE_ADMIN')");

        http
                .authorizeRequests()
                .antMatchers("/master/**")
                .access("hasAnyRole('ROLE_MASTER', 'ROLE_CHIEF', 'ROLE_ADMIN')");

        http
                .authorizeRequests()
                .and()
                .exceptionHandling()
                .accessDeniedPage("/403");
        http
                .authorizeRequests()
                .and()
                .formLogin()
                .loginProcessingUrl("/j_spring_security_check")
                .loginPage("/login")
                .successHandler(loginSuccessHandler)
                .failureUrl("/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login");

        http
                .authorizeRequests()
                .and()
                .rememberMe()
                .tokenRepository(this.persistentTokenRepository())
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds(24 * 60 * 60);
    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth, UserService userService) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        return new InMemoryTokenRepositoryImpl();
    }
}
