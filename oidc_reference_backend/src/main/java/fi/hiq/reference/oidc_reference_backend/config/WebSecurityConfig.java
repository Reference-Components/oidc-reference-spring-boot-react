package fi.hiq.reference.oidc_reference_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Value("${frontend.baseurl}")
  private String frontendBaseUrl;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        .mvcMatchers("/", "/login", "/current-user").permitAll()
        .anyRequest().authenticated()
        .and()
        .oauth2Login(oauthLogin -> oauthLogin.loginPage("/login").userInfoEndpoint().oidcUserService(new OidcUserService()))
        // Remove exceptionHandling() if automatic re-authentication is desired when user is unauthenticated or login is expired
        .exceptionHandling().defaultAuthenticationEntryPointFor(new Http403ForbiddenEntryPoint(), AnyRequestMatcher.INSTANCE)
        .and()
        .logout().permitAll().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
        .and()
        .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
        .cors();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of(frontendBaseUrl));
    configuration.setAllowedMethods(List.of("OPTIONS", "GET", "POST"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }

}
