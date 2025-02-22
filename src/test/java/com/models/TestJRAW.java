package com.models;

import static org.junit.jupiter.api.Assertions.*;
 
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource; 

import com.models.demands.News;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.CommentSort;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission; 
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.pagination.Paginator; 
import net.dean.jraw.references.SubredditReference;
import net.dean.jraw.tree.RootCommentNode;

@SpringBootTest
@TestPropertySource(locations="classpath:system.properties")
class TestJRAW {

	@Value( "${reddit.client}" )
	private String clientId;
	
	@Value( "${reddit.secret}" )
	private String secretClient;
	
	@Test
	void test() {
		
		UserAgent userAgent = new UserAgent("bot", "com.mit.em426", "v0.1", "ProfessionalWind6302");
		
		UUID id = UUID.randomUUID();
		
		// Create our credentials
		Credentials credentials = Credentials.userless(clientId, secretClient, id);

		// Authenticate our client
		RedditClient reddit = OAuthHelper.automatic(new OkHttpNetworkAdapter(userAgent), credentials);
		
		SubredditReference subRed = reddit.subreddit("wallstreetbets");		 
		
		DefaultPaginator<Submission> paginator = subRed.posts()
        .limit(Paginator.RECOMMENDED_MAX_LIMIT)
        .sorting(SubredditSort.TOP)
        .timePeriod(TimePeriod.MONTH)
        .build();
		
		// Request the first page
        Listing<Submission> firstPage = paginator.next();
		
        int numPage = firstPage.size();
        
        Submission post = firstPage.getFirst();
        String text = post.getTitle();
        String postURL = post.getUrl();
        Integer comments = post.getCommentCount();
        
        News news = new News(text, post.getId(), post.getUrl(), post.getCommentCount());
        
        RootCommentNode comm = reddit.submission(post.getId()).comments();
        int totalComm = comm.getReplies().size();
        
        Assertions.assertEquals(post.getCommentCount(), totalComm);
        
        post = firstPage.get(1);
        String text2 = post.getTitle();
        postURL = post.getUrl();
        comments = post.getCommentCount();
        
		fail("Not yet implemented");
	}

}
