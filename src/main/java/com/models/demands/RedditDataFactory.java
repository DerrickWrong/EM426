package com.models.demands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
  

import jakarta.annotation.PostConstruct;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.pagination.Paginator;
import net.dean.jraw.references.SubredditReference;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Scheduler;

@Component
public class RedditDataFactory {

	@Autowired
	RedditClient redditClient;

	@Autowired
	@Qualifier("ioScheduler")
	Scheduler ioScheduler;
 

	FluxSink<String> subredditProcessingStream;

	Map<String, List<News>> redditPostMap = new HashMap<>(); // subreddit name and posts

	@PostConstruct
	void setUp() {

		Flux<String> redditStream = Flux.create(pub -> {
			this.subredditProcessingStream = pub;
		});

		redditStream.publishOn(ioScheduler).subscribe(subredditTitle -> {

			// invoke web service to grab the subreddit
			DefaultPaginator<Submission> page = getRedditPostBySubredditTitle(subredditTitle);

			Listing<Submission> posts = page.next();

			if (!posts.isEmpty()) {

				// bind to just the first 10 or so posts from first page
				int count = posts.size() < 10 ? posts.size() : 10;

				for (int i = 0; i < count; i++) {

					String title = posts.get(i).getTitle();
					String id = posts.get(i).getId();
					String url = posts.get(i).getUrl();
					int commentCount = posts.get(i).getCommentCount();

					News news = new News(title, id, url, commentCount);
					
					this.redditPostMap.get(subredditTitle).add(news);
				}
			}
		});
 
	}

	DefaultPaginator<Submission> getRedditPostBySubredditTitle(String subName) {

		SubredditReference subRed = redditClient.subreddit(subName);

		DefaultPaginator<Submission> paginator = subRed.posts().limit(Paginator.RECOMMENDED_MAX_LIMIT)
				.sorting(SubredditSort.TOP).timePeriod(TimePeriod.MONTH).build();

		return paginator;
	}

	public void pollingPost(String subredditName) {

		this.redditPostMap.put(subredditName, new ArrayList<>());

		this.subredditProcessingStream.next(subredditName);

	}

	public void pushPosts(String subredditName) {

		List<News> arr = this.redditPostMap.get(subredditName);

		for (int i = 0; i < arr.size(); i++) {

			//demandStream.publish(DemandTypeEnum.NEWS, arr.get(i));
		}

	}

	public List<News> getRedditPosts(String subredditName) {
		return this.redditPostMap.get(subredditName);
	}

}
