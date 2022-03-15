package fi.hiq.reference.oidc_reference_backend.security;

import fi.hiq.reference.oidc_reference_backend.entity.Authority;
import fi.hiq.reference.oidc_reference_backend.entity.User;
import fi.hiq.reference.oidc_reference_backend.repository.UserRepository;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersistingOidcUserService extends OidcUserService {
  @Resource
  private UserRepository userRepository;

  @Override
  public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
    OidcUser oidcUser = super.loadUser(userRequest);

    try {
      processOidcUser(oidcUser);
      return oidcUser;
    } catch (Exception ex) {
      throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
    }
  }

  private void processOidcUser(OidcUser oidcUser) {
    GoogleUserInfo googleUserInfo = new GoogleUserInfo(oidcUser.getAttributes());

    Optional<User> userOptional = userRepository.findByEmail(googleUserInfo.getEmail());
    if (userOptional.isEmpty()) {
      User user = new User();
      user.setSub(googleUserInfo.getId());
      user.setEmail(googleUserInfo.getEmail());
      user.setName(googleUserInfo.getName());

      List<Authority> authorities = oidcUser.getAuthorities().stream()
          .map(GrantedAuthority::getAuthority)
          .map(auth -> new Authority(null, auth, user))
          .collect(Collectors.toList());
      user.setAuthorities(authorities);

      userRepository.save(user);
    }
  }
}
