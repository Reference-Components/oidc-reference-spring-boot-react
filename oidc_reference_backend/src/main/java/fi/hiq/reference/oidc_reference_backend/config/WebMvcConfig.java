package fi.hiq.reference.oidc_reference_backend.config;

import fi.hiq.reference.oidc_reference_backend.security.JwtCreatingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
  @Resource
  private JwtCreatingInterceptor jwtCreatingInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(jwtCreatingInterceptor).addPathPatterns("/**");
  }
}
