package org.graduatework.backend.config;

import org.graduatework.backend.auth.SimpleAuthenticationFilter;
import org.graduatework.backend.auth.SimpleAuthenticationProvider;
import org.graduatework.backend.auth.SimpleCorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableOAuth2Client
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .addFilterBefore(new SimpleAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new SimpleCorsFilter(), ChannelProcessingFilter.class)
                .authorizeRequests()
                .antMatchers("/signUp", "/activation", "/events", "/", "/search", "/top")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
//                .successHandler(new LoginSuccessHandler())
//                .failureHandler(new LoginFailureHandler())
                .usernameParameter("username").passwordParameter("password")
                .loginPage("/login")
                .successForwardUrl("/")
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .deleteCookies("JSESSIONID")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
//                .logoutSuccessHandler(new LogoutSuccessHandler())
                .and()
                .csrf().disable();
    }

    @Override
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        //auth.authenticationProvider(new SimpleAuthenticationProvider(userDetailsService()));
        /*auth.inMemoryAuthentication()
                .withUser("ihar").password(passwordEncoder().encode("mazyr")).roles("USER")
                .and()
                .withUser("anton").password(passwordEncoder().encode("minsk")).roles("USER");*/
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(Collections.singletonList(new SimpleAuthenticationProvider(userDetailsService())));
    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
}
