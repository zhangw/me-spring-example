package com.gigy.config;

import java.util.Arrays;

import javax.sql.DataSource;

import com.gigy.service.CustomizeJdbcClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

@Configuration
@EnableAuthorizationServer
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

	@Autowired
	@Qualifier("userDetailsService")
	private UserDetailsService userDetailsService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtAccessTokenConverter jwtAccessTokenConverter;

	@Autowired
	private DataSource dataSource;

	@Value("${gigy.oauth.tokenTimeout:3600}")
	private int expiration;

	@Autowired
	private RedisConnectionFactory redisConnectionFactory;

	// password encryptor
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter(){
		return new JwtAccessTokenConverter();
	}

	@Bean
	public TokenEnhancer tokenEnhancer(){
		return new CustomerEnhancer();
	}

	@Bean
	public TokenStore tokenStore(){
		return new RedisTokenStore(redisConnectionFactory);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer configurer) throws Exception {
		configurer.authenticationManager(authenticationManager);
		configurer.userDetailsService(userDetailsService);
		//configurer.accessTokenConverter(jwtAccessTokenConverter);

		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		TokenEnhancer tokenEnhancer = tokenEnhancer();
		((CustomerEnhancer)tokenEnhancer).setClientDetailsService(configurer.getClientDetailsService());

		//TODO: 暂时不使用JWT
		//tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer, jwtAccessTokenConverter));

		//NOTICE: 使用自定义的TokenEnhancer返回Token
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer));

		configurer.tokenEnhancer(tokenEnhancerChain);

		//使用redis存放access_token
		configurer.tokenStore(tokenStore());


	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

		/*
		clients.inMemory().withClient("gigy").secret("secret").accessTokenValiditySeconds(expiration)
				.scopes("read", "write").authorizedGrantTypes("client_credentials", "password", "refresh_token").resourceIds("resource");
		*/

		/*
		Map<String, String> additionalInfo = new HashMap<>(1);
		additionalInfo.put("region", "CN");
		clients.inMemory().withClient("gigy").secret("secret").accessTokenValiditySeconds(expiration)
			.authorizedGrantTypes("client_credentials").scopes("trade").additionalInformation(additionalInfo);
		*/

		/*
		clients.jdbc(dataSource).withClient("gigy").secret("secret").accessTokenValiditySeconds(expiration)
			.authorizedGrantTypes("client_credentials").scopes("trade").additionalInformation(additionalInfo);
		*/

		//clients.jdbc(dataSource);
		//使用自定义的JdbcClientDetailsService
		clients.withClientDetails(new CustomizeJdbcClientDetailsService(dataSource));
	}

}
