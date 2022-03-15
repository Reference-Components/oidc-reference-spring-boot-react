package fi.hiq.reference.oidc_reference_backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {
  @Value("${frontend.baseurl}")
  private String frontendBaseUrl;
  @Value("${oauth2.provider.google.redirect.path}")
  private String googleRedirectPath;

  @GetMapping({"/", "/login"})
  public void login(HttpServletResponse response,
                    UriComponentsBuilder uriComponentsBuilder) {

    final String location;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
      location = frontendBaseUrl;
    } else {
      location = uriComponentsBuilder.path(googleRedirectPath).toUriString();
    }

    response.setStatus(HttpStatus.FOUND.value());
    response.setHeader(HttpHeaders.LOCATION, location);
  }

}
