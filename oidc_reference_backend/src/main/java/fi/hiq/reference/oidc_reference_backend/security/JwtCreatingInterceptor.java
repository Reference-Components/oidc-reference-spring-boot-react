package fi.hiq.reference.oidc_reference_backend.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtCreatingInterceptor implements HandlerInterceptor {
  @Resource
  JwtService jwtService;

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
      String jwt = "";
      Object principal = authentication.getPrincipal();
      if (principal instanceof UserDetails) {
        jwt = jwtService.generateToken(((UserDetails) principal).getUsername());
      } else if (principal instanceof OidcUser) {
        jwt = jwtService.generateToken(((OidcUser) principal).getEmail());
      }
      response.addCookie(new Cookie("jwt-token", jwt));
    }

    // This is the most important part. JWT could be created and added to response elsewhere if wanted
    SecurityContextHolder.getContext().setAuthentication(null);
  }
}
