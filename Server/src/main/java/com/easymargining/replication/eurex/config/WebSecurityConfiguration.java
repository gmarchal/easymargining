package com.easymargining.replication.eurex.config;


import com.easymargining.replication.eurex.domain.services.security.UserRepositoryUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;


/**
 * Created by Gilles Marchal on 18/01/2016.
 */
@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses = UserRepositoryUserDetailsService.class)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Configuration
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    static class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {
        @Autowired
        private PermissionEvaluator permissionEvaluator;

        @Override
        protected MethodSecurityExpressionHandler createExpressionHandler() {
            DefaultMethodSecurityExpressionHandler handler =
                    new DefaultMethodSecurityExpressionHandler();
            handler.setPermissionEvaluator(permissionEvaluator);
            return handler;
        }
    }

    // @formatter:off
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
            .authorizeRequests()
                .antMatchers("/resources/**","/app/**","/**","/signup").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/index.html")
                .permitAll()
                .and()
            .logout()
                .permitAll();
    }
    // @formatter:on

    // @formatter:off
    @Autowired
    public void registerAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userDetailsService);
    }
    // @formatter:on
}
