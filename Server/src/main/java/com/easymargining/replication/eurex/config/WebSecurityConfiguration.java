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
@EnableGlobalMethodSecurity(securedEnabled = false)
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

        /*
        http
                .csrf().disable()
                .authorizeRequests()
                //.antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .defaultSuccessUrl("/web/index.htlm")
                .and()
                .httpBasic();
        */

        http
            .authorizeRequests()
                .antMatchers("/landing/**","/libs/**",
                                "/src/api/**","/src/css/**","/src/data/**","/src/fonts/**",
                                "/src/img/**","/src/js/**","/src/l10n/**","/src/tpl/blocks/**",
                                "/src/tpl/page_signup.html","/src/tpl/page_signin.html",
                                "/src/tpl/page_404.html","/src/tpl/page_forgotpwd.html",
                                "/src/index.html#/access/signin",
                                "/src/index.html#/access/signup",
                                "/src/index.html#/access/forgotpwd",
                                "/src/index.html").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/src/index.html#/access/signin")
                .defaultSuccessUrl("/src/index.html")
                .and()
            .httpBasic()
                .and()
            .logout()
                .logoutUrl("/src/index.html#/access/signin");

    }
    // @formatter:on

    // @formatter:off
    @Autowired
    public void registerAuthentication(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
                .withUser("user").password("password").roles("USER").and()
                .withUser("admin").password("password").roles("USER", "ADMIN").and();
        /*
        auth
            .userDetailsService(userDetailsService);
        */
    }
    // @formatter:on
}
