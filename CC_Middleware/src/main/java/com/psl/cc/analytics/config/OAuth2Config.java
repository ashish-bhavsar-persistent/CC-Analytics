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
			+ "MIICXQIBAAKBgQDS58oly4FDJdBbh07MAobG0PLBgTTc/kyTuXV7bHvYwoNwma8w\r\n"
			+ "HkGxeq95rVVkdVVfvNuAOLJjCTAEAupeR/5hwvgoTvH5wpXzE0Gc22IJiVVZmduw\r\n"
			+ "KYSLxjoYUXiXQwV3ZWjYUQecdaq/x40NAEhWW4vA0RW18zi07T4ye37ziQIDAQAB\r\n"
			+ "AoGAAadzvGwmLWKkibM7+PmkGPcWkFH1Gi6cZyQzOa6WIhMUeNObviLenQe7TJcB\r\n"
			+ "bnJFIyUWC6FmNMSjrcRgFjE2W4ZF+km/FMMCMytO1cgG0WUK1ODcy+TGEdm+44iV\r\n"
			+ "n2IQRZVQAoQw5yeeRCT032mySAXbowjPlHP/6fhmdhHvgQECQQD7XKdKEh9t7AzK\r\n"
			+ "P4MbK0cGVvecJPwQmk19aYnKtQ1/kgfDYTiIyZ7ClQTj3IYt6LZwQonD1NJctoUy\r\n"
			+ "abJPkEsxAkEA1swIoJTkP0Q1ORF+7L2F0ZLXry7bKN75pRWlswNOJzvUcmtR/FQ7\r\n"
			+ "MuAO8x7pJzHxh9g11SpYV5IjnrlfGjbn2QJBAO5ytJfFnV8PYAq0OVEauuO+uGG9\r\n"
			+ "jAjL99qStY5ANq/f/dFQdur8KMj5yIvH9Nm0Ou0/kaTeTTh6RNzP+8ESZuECQFk/\r\n"
			+ "2AnZM+mxzqeSxHlb2mGVMTdiMcAOvg5BK2NxaSTWMFUGkL9WxG5EORH98wTNNL2s\r\n"
			+ "MdoLyT9Bwrkt7v02eSkCQQCantSoncciBbbJ3xj3etsCV4oExXmH3a053eAyawbr\r\n"
			+ "/axsJzEyMTlMbAf4Maeh50MqpqOjtpuuYFRgPz4qbdhM\r\n" + "-----END RSA PRIVATE KEY-----";
	private String publicKey = "-----BEGIN PUBLIC KEY-----\r\n"
			+ "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDS58oly4FDJdBbh07MAobG0PLB\r\n"
			+ "gTTc/kyTuXV7bHvYwoNwma8wHkGxeq95rVVkdVVfvNuAOLJjCTAEAupeR/5hwvgo\r\n"
			+ "TvH5wpXzE0Gc22IJiVVZmduwKYSLxjoYUXiXQwV3ZWjYUQecdaq/x40NAEhWW4vA\r\n" + "0RW18zi07T4ye37ziQIDAQAB\r\n"
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
