package fi.hiq.reference.oidc_reference_backend.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserInfoController {

  @GetMapping("/current-user")
  public OidcUser getOidcUserPrincipal(@AuthenticationPrincipal OidcUser principal) {
    return principal;
  }
}
