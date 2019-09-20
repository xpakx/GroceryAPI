package com.example.groceryapi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter 
{
  private UserDetailsService customUserDetailsService;

  @Autowired
  public void setCustomUserDetailsService(CustomUserDetailsService customUserDetailsService) 
  {
    this.customUserDetailsService = customUserDetailsService;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) 
  {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(customUserDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    auth.authenticationProvider(provider);
  }


  @Override
  protected void configure(HttpSecurity http) throws Exception 
  {
    http.httpBasic().and()
      .logout().and()
      .authorizeRequests()
        .antMatchers(HttpMethod.POST,"/product").hasRole("EMPLOYEE")
        .antMatchers(HttpMethod.PUT,"/product/**").hasRole("EMPLOYEE")
        .antMatchers(HttpMethod.DELETE,"/product/**").hasRole("EMPLOYEE")
        .antMatchers(HttpMethod.POST,"/producer").hasRole("EMPLOYEE")
        .antMatchers(HttpMethod.PUT,"/producer/**").hasRole("EMPLOYEE")
        .antMatchers(HttpMethod.DELETE,"/producer/**").hasRole("EMPLOYEE")
        .antMatchers(HttpMethod.POST,"/category").hasRole("EMPLOYEE")
        .antMatchers(HttpMethod.PUT,"/category/**").hasRole("EMPLOYEE")
        .antMatchers(HttpMethod.DELETE,"/category/**").hasRole("EMPLOYEE")
        .antMatchers(HttpMethod.GET,"/order/all").hasRole("USER")
        .antMatchers(HttpMethod.PUT,"/order/**").hasRole("EMPLOYEE")
        .anyRequest().permitAll()
      .and()
      .csrf()
      .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
      .ignoringAntMatchers("/user").and()
      .sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }
  
  @Bean
  PasswordEncoder passwordEncoder() 
  {
    return new BCryptPasswordEncoder();
  }
  
}
