package com.marcionavarro.authserver.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class AuthorizationServerConfig {

    @Value("${app.security.issuer}")
    private String issuer;

    // ---------------------------------------------------------
    // Filter chain 1: endpoints do Authorization Server
    // (ex: /oauth2/authorize, /oauth2/token, /userinfo, etc.)
    // ---------------------------------------------------------
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();

        http
            .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
            .with(authorizationServerConfigurer, as -> as
                // Habilita suporte a OpenID Connect (ID token, /userinfo, discovery)
                .oidc(Customizer.withDefaults())
            )
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated()
            )
            // Redireciona para /login quando o browser acessa um endpoint protegido
            .exceptionHandling(ex -> ex
                .defaultAuthenticationEntryPointFor(
                    new LoginUrlAuthenticationEntryPoint("/login"),
                    new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                )
            );

        return http.build();
    }

    // ---------------------------------------------------------
    // Filter chain 2: demais rotas (tela de login)
    // ---------------------------------------------------------
    @Bean
    @Order(1)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form //Customizer.withDefaults()
                .loginPage("/login")
                .permitAll()
            );

        return http.build();
    }

    // ---------------------------------------------------------
    // Clientes OAuth2 registrados (in-memory por enquanto)
    // ---------------------------------------------------------
    @Bean
    public RegisteredClientRepository registeredClientRepository() {

        RegisteredClient manualClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("manual-client")
            // {noop} = sem encoding de senha, apenas para desenvolvimento
            .clientSecret("{noop}my-client-secret")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            // URL de callback do Next.js (Auth.js)
//            .redirectUri("http://localhost:3000/api/auth/callback/spring")
            // URL de callback para testes manuais (browser)
//            .redirectUri("http://localhost:9000/authorized")
//            .postLogoutRedirectUri("http://localhost:3000")
            // URL de callback do Postman para testes manuais
            .redirectUri("https://oauth.pstmn.io/v1/callback")
            .postLogoutRedirectUri("http://localhost:3000/api/post-logout")
            // Scopes disponíveis para este cliente
            .scope(OidcScopes.OPENID)       // obrigatório para OIDC / ID token
            .scope(OidcScopes.PROFILE)
            .scope(OidcScopes.EMAIL)
            .clientSettings(ClientSettings.builder()
                                .requireAuthorizationConsent(false)
                                .build())
            .build();

        return new InMemoryRegisteredClientRepository(manualClient);
    }

    // ---------------------------------------------------------
    // Usuários in-memory (serão migrados para banco no commit 3)
    // ---------------------------------------------------------
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {

        UserDetails lita = User.builder()
            .username("lita@example.com")
            .password(passwordEncoder.encode("admin123"))
            .authorities(List.of())
            .build();

        UserDetails marcio = User.builder()
            .username("marcio@example.com")
            .password(passwordEncoder.encode("admin123"))
            .authorities(List.of())
            .build();


        return new InMemoryUserDetailsManager(lita, marcio);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // ---------------------------------------------------------
    // Par de chaves RSA para assinar os JWTs
    // (gerado em memória — será externalizado em produção)
    // ---------------------------------------------------------
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .build();

        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }

    private static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    // Necessário para que o AS possa decodificar seus próprios tokens
    // (usado internamente pelo endpoint /userinfo e por introspection)
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    // ---------------------------------------------------------
    // Configurações gerais do Authorization Server
    // ---------------------------------------------------------
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
            .issuer(issuer)
            .build();
    }
}
