package cl.pedidos360.security;

import java.nio.charset.StandardCharsets;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {
  @Bean JwtDecoder jwtDecoder(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.issuer}") String issuer,@Value("${app.jwt.jwk-set-uri:}") String jwkSetUri){
    if(jwkSetUri!=null&&!jwkSetUri.isBlank()){
      var decoder=NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
      decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(new JwtTimestampValidator(),new JwtIssuerValidator(issuer)));
      return decoder;
    }
    var key=new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA256");
    var decoder=NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer)); return decoder;
  }
  @Bean SecurityFilterChain security(HttpSecurity http)throws Exception{
    return http.csrf(csrf->csrf.disable()).cors(Customizer.withDefaults())
      .authorizeHttpRequests(a->a.requestMatchers("/actuator/**").permitAll().requestMatchers(HttpMethod.OPTIONS,"/**").permitAll().anyRequest().authenticated())
      .oauth2ResourceServer(o->o.jwt(Customizer.withDefaults())).build();
  }
  @Bean CorsConfigurationSource cors(){var c=new CorsConfiguration();c.setAllowedOrigins(java.util.List.of("http://localhost:4200"));c.setAllowedMethods(java.util.List.of("GET","POST","PUT","DELETE","OPTIONS"));c.setAllowedHeaders(java.util.List.of("Authorization","Content-Type"));var s=new UrlBasedCorsConfigurationSource();s.registerCorsConfiguration("/**",c);return s;}
}
