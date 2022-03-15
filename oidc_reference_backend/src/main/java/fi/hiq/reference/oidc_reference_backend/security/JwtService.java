package fi.hiq.reference.oidc_reference_backend.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SimpleSecurityContext;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.JWTProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
final class JwtService {
  @Value("${jwt.validity.minutes}")
  private long jwtValidityMinutes;
  @Resource
  private DirectEncrypter directEncrypter;
  @Resource
  private JWTProcessor<SimpleSecurityContext> jwtProcessor;

  String generateToken(String username) {
    Map<String, Object> claims = new HashMap<>();

    return doGenerateToken(claims, username);
  }

  String getUsernameFromToken(String token) {
    return getClaimFromToken(token, JWTClaimsSet::getSubject);
  }

  boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = getUsernameFromToken(token);
    return (username.equals(userDetails.getUsername()) && isTokenTimeframeValid(token));
  }

  private boolean isTokenTimeframeValid(String token) {
    return !isTokenExpired(token) && !isTokenStartTimeInTheFuture(token);
  }

  private <T> T getClaimFromToken(String token, Function<JWTClaimsSet, T> claimsResolver) {
    final JWTClaimsSet claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  private JWTClaimsSet getAllClaimsFromToken(String token) {
    try {
      return jwtProcessor.process(token, null);
    } catch (ParseException | BadJOSEException | JOSEException e) {
      return new JWTClaimsSet.Builder().build();
    }
  }

  private boolean isTokenExpired(String token) {
    final Date expirationTime = getClaimFromToken(token, JWTClaimsSet::getExpirationTime);
    return expirationTime.before(new Date());
  }

  private boolean isTokenStartTimeInTheFuture(String token) {
    final Date now = new Date();
    final Date issueTime = getClaimFromToken(token, JWTClaimsSet::getIssueTime);
    final Date notBeforeTime = getClaimFromToken(token, JWTClaimsSet::getNotBeforeTime);

    return issueTime.after(now) || notBeforeTime.after(now);
  }

  private String doGenerateToken(Map<String, Object> claims, String subject) {
    JWTClaimsSet.Builder jwtClaimSetBuilder = new JWTClaimsSet.Builder();

    claims.forEach(jwtClaimSetBuilder::claim);

    final long now = System.currentTimeMillis();
    JWTClaimsSet jwtClaimsSet = jwtClaimSetBuilder
        .subject(subject)
        .notBeforeTime(new Date(now))
        .issueTime(new Date(now))
        .expirationTime(new Date(now + jwtValidityMinutes * 60 * 1000))
        .build();

    EncryptedJWT jwt = new EncryptedJWT(JwtEncrypter.JWE_HEADER, jwtClaimsSet);
    try {
      jwt.encrypt(directEncrypter);
    } catch (JOSEException | IllegalStateException e) {
      System.out.println("Error: " + e);
      return null;
    }

    return jwt.serialize();
  }
}
