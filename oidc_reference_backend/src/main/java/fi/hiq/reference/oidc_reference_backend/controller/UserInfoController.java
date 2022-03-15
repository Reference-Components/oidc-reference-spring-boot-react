package fi.hiq.reference.oidc_reference_backend.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserInfoController {

  @GetMapping("/current-user")
  public Object getUserPrincipal() {
    Object principal = SecurityContextHolder.getContext().getAuthentication();
    if (principal instanceof AnonymousAuthenticationToken) {
      return null;
    } else {
      return principal;
    }
  }
}
