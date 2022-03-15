package fi.hiq.reference.oidc_reference_backend.security;

import fi.hiq.reference.oidc_reference_backend.entity.User;
import fi.hiq.reference.oidc_reference_backend.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
  @Resource
  private JwtService jwtService;
  @Resource
  private UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    final String requestTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    String username = null;
    String jwtToken = null;
    if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
      jwtToken = requestTokenHeader.substring(7);
      username = jwtService.getUsernameFromToken(jwtToken);
    }

    if (StringUtils.hasLength(username)) {
      UserDetails userDetails = null;

      Optional<User> userOptional = userRepository.findByEmail(username);
      if (userOptional.isPresent()) {
        User user = userOptional.get();
        List<SimpleGrantedAuthority> authorities = user.getAuthorities().stream()
            .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityString()))
            .collect(Collectors.toList());
        userDetails = new org.springframework.security.core.userdetails.User(username, "", authorities);
      }

      if (userDetails != null && jwtService.isTokenValid(jwtToken, userDetails)) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());

        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(token);
      }
    }

    chain.doFilter(request, response);
  }
}
