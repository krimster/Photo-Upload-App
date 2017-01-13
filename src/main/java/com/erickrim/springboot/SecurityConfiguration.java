package com.erickrim.springboot;

import com.erickrim.springboot.services.SpringDataUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by krime on 1/13/17.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/" ,"/images", "/main.css", "/webjars/**").permitAll() //if yo do a get call against / it would be permitted
                    .antMatchers(HttpMethod.POST, "/images").hasRole("USER")
                    .antMatchers("/imageMessages/**").permitAll()
                    //.anyRequest().fullyAuthenticated()
                    .and()
                .formLogin() // need a form login
                    .permitAll() // allow access to form without login
                    .and()
                .logout()
                    .logoutSuccessUrl("/");
    }

//    @Autowired
//    public void configureInMemoryUsers(AuthenticationManagerBuilder auth) throws Exception {
//
//        auth.inMemoryAuthentication()
//                .withUser("eric").password("krim").roles("ADMIN", "USER")
//                .and()
//                .withUser("rob").password("winch").roles("USER")
//                .and()
//                .withUser("baddie1").password("password").roles("USER").disabled(true)
//                .and()
//                .withUser("baddie2").password("password").roles("USER").accountLocked(true);
//    }

    @Autowired
     public void configureJpaBasedUsers(AuthenticationManagerBuilder auth,
                                        SpringDataUserDetailsService userDetailsService) throws Exception {
        auth.userDetailsService(userDetailsService);

     }


}
