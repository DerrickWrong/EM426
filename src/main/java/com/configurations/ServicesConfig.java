package com.configurations;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
 
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;

@Configuration
@PropertySource("classpath:system.properties")
public class ServicesConfig {

	@Value( "${reddit.client}" )
	private String clientId;
	
	@Value( "${reddit.secret}" )
	private String secretClient;
	
	@Value("${reddit.tokenName}")
	private String tokenName;
	 
	@Bean
	RedditAPIManager connectReddit() {
		
		RedditClient redditClient = null;
		UserAgent userAgent = new UserAgent("bot", "com.mit.em426", "v0.1", tokenName);
		
		UUID id = UUID.randomUUID();
		
		try {
		
		// Create our credentials
		Credentials credentials = Credentials.userless(clientId, secretClient, id);

		OkHttpNetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);  
		// Authenticate our client
		redditClient = OAuthHelper.automatic(adapter, credentials);
	 
		
		}catch(Exception e) {
			System.out.println(e);
		}
		
		return new RedditAPIManager(redditClient);
	} 
	
}
