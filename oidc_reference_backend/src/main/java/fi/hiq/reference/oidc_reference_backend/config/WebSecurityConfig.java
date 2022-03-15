package fi.hiq.reference.oidc_reference_backend.config;

import fi.hiq.reference.oidc_reference_backend.security.PersistingOidcUserService;
import fi.hiq.reference.oidc_reference_backend.security.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.Resource;
import java.util.List;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Value("${frontend.baseurl}")
  private String frontendBaseUrl;
  @Resource
  private PersistingOidcUserService customOidcUserService;
  @Resource
  private JwtRequestFilter jwtRequestFilter;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        .mvcMatchers("/", "/login", "/current-user").permitAll()
        .anyRequest().authenticated()
        .and()
        .oauth2Login(oauthLogin -> oauthLogin.loginPage("/login").userInfoEndpoint().oidcUserService(customOidcUserService))
        // Remove exceptionHandling() if automatic re-authentication is desired when user is unauthenticated or login is expired
        .exceptionHandling().defaultAuthenticationEntryPointFor(new Http403ForbiddenEntryPoint(), AnyRequestMatcher.INSTANCE)
        .and()
        .logout().permitAll().deleteCookies("jwt-token").logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
        .and()
        .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
        .cors()
        .and()
        .addFilterBefore(jwtRequestFilter, OAuth2AuthorizationRequestRedirectFilter.class)
        .sessionManagement().maximumSessions(1).and().sessionCreationPolicy(SessionCreationPolicy.NEVER);
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
