package com.psl.cc.analytics.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

	private String clientId = "ashish";
	private String clientSecret = "secret";
	private String privateKey = "-----BEGIN RSA PRIVATE KEY-----\r\n"
			+ "MIICXAIBAAKBgQCUd/FoXBwVF7C0XSsSryHFiKc6YDx1wTdUfIh4o8TUwozszNX/\r\n"
			+ "mD5xE7IgZ3ULIIMLLBoz6V4dn0lx4nDwXwle75CYYTNmxhqE4RyZ5h/U9iBS7O1L\r\n"
			+ "U1cnzfXoppDGmoI+1HNK+L4fzfcT+97RUCZiRtLLSUbV+Z8OskG/jDJzDwIDAQAB\r\n"
			+ "AoGAFTMjR3GPmPtvAfIjymEzg09GAbHoMDBMxEWb/w3zdyXolY+SJxMIJ4FWeAYN\r\n"
			+ "0m7sB1Zez3bNN5GdHSPmWIxZe/stByiZGqCb0YwjhKSxti224GJPS9I9xg1nCj2c\r\n"
			+ "VuOVJv76SPiEv8/tuRNoK/QiyxkXRLOdja8fXuy3foi0AHECQQDHDNi7ZEJ0YMgR\r\n"
			+ "oOIWXvucMyInThexrlno53HQrg+UPSZReJjHgYRV9AnT6gYFLW94gvyVbntlPdOi\r\n"
			+ "G+tYudEFAkEAvvJQQ1/EsIe522+o7IW8UIq0isKK2x5fneZ6pccER95+cAOBlepd\r\n"
			+ "hAGy99Rr+aYweFkrQIENQJYG62yXvQEAAwJBAKV5RouyM5SwCGKvToSufZlC4Pev\r\n"
			+ "8f6iJNh736BNs2HW3A4KpWflgfA6qhjjihGvzeVvby+C404s8czoTOQ7xUkCQBO1\r\n"
			+ "1Z0uzJHgYMoK+6f2ohq6RqqwLD80InCdMvnb0lBM4kZTxlOgTqjqt/unHMI3andx\r\n"
			+ "1OoiqCiLlAlHO16SNNsCQFTMTf6IQlTL6pP7pnQZRppvtA2xJ2Cu8on0Lp6MN/11\r\n"
			+ "37HMItzYB8XPVSsIv8FhujAq/S74EMIgbIYXzaS1PCI=\r\n" + "-----END RSA PRIVATE KEY-----\r\n" + "";
	private String publicKey = "-----BEGIN PUBLIC KEY-----\r\n"
			+ "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCUd/FoXBwVF7C0XSsSryHFiKc6\r\n"
			+ "YDx1wTdUfIh4o8TUwozszNX/mD5xE7IgZ3ULIIMLLBoz6V4dn0lx4nDwXwle75CY\r\n"
			+ "YTNmxhqE4RyZ5h/U9iBS7O1LU1cnzfXoppDGmoI+1HNK+L4fzfcT+97RUCZiRtLL\r\n" + "SUbV+Z8OskG/jDJzDwIDAQAB\r\n"
			+ "-----END PUBLIC KEY-----";

	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;

	@Autowired
	@Qualifier("passwordEncoder")
	private PasswordEncoder passwordEncoder;

	@Override
	public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
	}

	@Bean
	@Primary
	public DefaultTokenServices tokenServices() {
		final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenStore(tokenStore());
		defaultTokenServices.setSupportRefreshToken(true);
		return defaultTokenServices;
	}

	@Override
	public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//		final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
//		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
//		endpoints.tokenStore(tokenStore()).tokenEnhancer(tokenEnhancerChain)
//				.authenticationManager(authenticationManager);
		endpoints.authenticationManager(authenticationManager).tokenStore(tokenStore())
				.accessTokenConverter(accessTokenConverter());
	}

	@Override
	public void configure(final ClientDetailsServiceConfigurer clients) throws Exception { // @formatter:off
//		clients.inMemory().withClient("barClientIdPassword").secret(passwordEncoder.encode("secret"))
//				.authorizedGrantTypes("password", "authorization_code", "refresh_token").scopes("bar", "read", "write")
//				.accessTokenValiditySeconds(3600) // 1 hour
//				.refreshTokenValiditySeconds(2592000) // 30 days
//				.and().withClient("testImplicitClientId").authorizedGrantTypes("implicit")
//				.scopes("read", "write", "foo", "bar").autoApprove(true).redirectUris("http://www.example.com");
		clients.inMemory().withClient(clientId).secret(passwordEncoder.encode(clientSecret))
				.scopes("read", "write", "ashish")
				.authorizedGrantTypes("password", "authorization_code", "refresh_token")
				.accessTokenValiditySeconds(20000).autoApprove(true).refreshTokenValiditySeconds(2592000);
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setSigningKey(privateKey);
		converter.setVerifierKey(publicKey);
		// final KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new
		// ClassPathResource("mytest.jks"), "mypass".toCharArray());
		// converter.setKeyPair(keyStoreKeyFactory.getKeyPair("mytest"));
		return converter;
	}

	@Bean
	public TokenEnhancer tokenEnhancer() {
		return new CustomTokenEnhancer();
	}

}
