package com.psl.cc.analytics.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {
	private static String REALM = "CRM_REALM";
	private static final int TEN_DAYS = 60 * 60 * 24 * 10;
	private static final int ONE_DAY = 60 * 60 * 24;
	private static final int THIRTY_DAYS = 60 * 60 * 24 * 30;
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
	private PasswordEncoder encoder;

	@Autowired
	private TokenStore tokenStore;

	@Autowired
	private ClientDetailsService clientDetailsService;

	@Autowired
	private UserApprovalHandler userApprovalHandler;

	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsService crmUserDetailsService;

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

		clients.inMemory().withClient("ashish").secret(encoder.encode("secret"))
				.authorizedGrantTypes("password", "refresh_token").authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
				.scopes("read", "write", "trust").accessTokenValiditySeconds(ONE_DAY)
				.refreshTokenValiditySeconds(THIRTY_DAYS);

	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler)
				.authenticationManager(authenticationManager).userDetailsService(crmUserDetailsService)
				.accessTokenConverter(accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setSigningKey(privateKey);
		converter.setVerifierKey(publicKey);
		return converter;
	}

	@Bean
	public TokenStore tokenStore() {
//		return new InMemoryTokenStore();
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	@Autowired
	public TokenStoreUserApprovalHandler userApprovalHandler(TokenStore tokenStore) {
		TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();
		handler.setTokenStore(tokenStore);
		handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
		handler.setClientDetailsService(clientDetailsService);
		return handler;
	}

	@Bean
	@Autowired
	public ApprovalStore approvalStore(TokenStore tokenStore) throws Exception {
		TokenApprovalStore store = new TokenApprovalStore();
		store.setTokenStore(tokenStore);
		return store;
	}

}
