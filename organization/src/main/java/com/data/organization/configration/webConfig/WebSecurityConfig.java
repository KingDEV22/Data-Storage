package com.data.organization.configration.webConfig;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.data.organization.configration.jwt.AuthEntryPointJwt;
import com.data.organization.configration.jwt.JwtTokenVerifier;
import com.data.organization.configration.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import com.data.organization.configration.jwt.JwtUtil;
import com.data.organization.configration.jwt.jwtConfig.JwtConfig;
import com.data.organization.service.OrgUserServiceImpl;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final OrgUserServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private AuthEntryPointJwt unauthorizedHandler;

    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;
    private final JwtUtil jwtUtil;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/org/register").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .addFilter(
                        new JwtUsernameAndPasswordAuthenticationFilter(authenticationManagerBean(), jwtConfig,
                                jwtUtil, secretKey))
                .addFilterAfter(new JwtTokenVerifier(secretKey, jwtConfig, jwtUtil, userDetailsService),
                        JwtUsernameAndPasswordAuthenticationFilter.class);
    }

}
