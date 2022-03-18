package fi.hiq.reference.oidc_reference_backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {
  @Value("${frontend.baseurl}")
  private String frontendBaseUrl;

  @GetMapping({"/"})
  public void root(HttpServletResponse response) {
    response.setStatus(HttpStatus.FOUND.value());
    response.setHeader(HttpHeaders.LOCATION, frontendBaseUrl);
  }

}
