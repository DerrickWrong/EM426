package com.models.demands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.models.interfaces.DemandTypeEnum;

import jakarta.annotation.PostConstruct;
import javafx.util.Pair;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.pagination.Paginator;
import net.dean.jraw.references.SubredditReference;
import reactor.core.publisher.Sinks.Many;
import reactor.core.scheduler.Scheduler;

@Component
public class RedditDataFactory {

	@Autowired
	RedditClient redditClient;

	@Autowired
	@Qualifier("ioScheduler")
	Scheduler ioScheduler;

	@Autowired
	Many<Pair<DemandTypeEnum, String>> messageSink;

	Map<String, List<News>> redditPostMap = new HashMap<>(); // subreddit name and posts

	@PostConstruct
	void setUp() {

		messageSink.asFlux().publishOn(ioScheduler).subscribe(d -> {

			String redditTitle = d.getValue();

			if (!redditPostMap.containsKey(redditTitle)) {

				redditPostMap.put(redditTitle, new ArrayList<>());
			}

			// invoke web service to grab the subreddit
			DefaultPaginator<Submission> page = getRedditPostBySubredditTitle(redditTitle);

			Listing<Submission> posts = page.next();

			if (!posts.isEmpty()) {

				// bind to just the first 10 or so posts from first page
				int count = posts.size();// < 10 ? posts.size() : 10;

				for (int i = 0; i < count; i++) {

					String title = posts.get(i).getTitle();
					String id = posts.get(i).getId();
					String url = posts.get(i).getUrl();
					int commentCount = posts.get(i).getCommentCount();

					News news = new News(title, id, url, commentCount);

					this.redditPostMap.get(redditTitle).add(news);
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

	public List<News> getPosts(String redditTitle) {

		return this.redditPostMap.get(redditTitle);
	}

}
