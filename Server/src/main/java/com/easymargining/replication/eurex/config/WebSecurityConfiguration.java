package com.easymargining.replication.eurex.config;


import com.easymargining.replication.eurex.domain.services.security.UserRepositoryUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Created by Gilles Marchal on 18/01/2016.
 */
@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
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
        /*
        http
            .authorizeRequests()
                .antMatchers("/landing/**","/libs/**",
                                "/src/api/**","/src/css/**","/src/data/**","/src/fonts/**",
                                "/src/img/**","/src/js/**","/src/l10n/**","/src/tpl/blocks/**",
                                "/src/tpl/page_signup.html","/src/tpl/page_signin.html",
                                "/src/tpl/page_404.html","/src/tpl/page_forgotpwd.html",
                                "/src/index.html",
                                "/src/app.html").permitAll()
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
                */
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/index.html", "/landing/**", "/libs/**", "/",
                             "/src/api/**", "/src/css/**", "/src/data/**", "/src/fonts/**",
                             "/src/img/**", "/src/js/**", "/src/l10n/**", "/src/tpl/blocks/**",
                             "/src/tpl/app.html", "/src/tpl/page_404.html", "/src/tpl/page_forgotpwd.html",
                             "/src/tpl/page_signin.html", "/src/tpl/page_signup.html", "/src/index.html",
                             "/api/users/current", "/api/users/signup").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                    .loginPage("/src/index.html#/access/signin")
                    .defaultSuccessUrl("/src/index.html")
                .and()
                .httpBasic().and().csrf()
                .csrfTokenRepository(csrfTokenRepository()).and()
                .addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);
        /*
        http.httpBasic().and().authorizeRequests()
                .antMatchers("/landing/css/**","/landing/fonts/**","/landing/img/**","/landing/js/**",
                        "/landing/tpl/**","/landing/**","/libs/**",
                        "/src/api/**","/src/css/**","/src/data/**","/src/fonts/**",
                        "/src/img/**","/src/js/**","/src/l10n/**","/src/tpl/blocks/**",
                        "/src/tpl/page_signup.html","/src/tpl/page_signin.html",
                        "/src/tpl/page_404.html","/src/tpl/page_forgotpwd.html",
                        "/src/index.html",
                        "/src/app.html",
                        "/").permitAll().anyRequest()
                .authenticated().and().csrf()
                .csrfTokenRepository(csrfTokenRepository()).and()
                .addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);
        */
    }
    // @formatter:on

    private Filter csrfHeaderFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class
                        .getName());
                if (csrf != null) {
                    Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
                    String token = csrf.getToken();
                    if (cookie == null || token != null
                            && !token.equals(cookie.getValue())) {
                        cookie = new Cookie("XSRF-TOKEN", token);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }

    // @formatter:off
    @Autowired
    public void registerAuthentication(AuthenticationManagerBuilder auth) throws Exception {

        /*
        auth.inMemoryAuthentication()
                .withUser("user@g.fr").password("password").roles("USER").and()
                .withUser("admin").password("password").roles("USER", "ADMIN").and();
        */

        auth
            .userDetailsService(userDetailsService);
    }
    // @formatter:on
}
