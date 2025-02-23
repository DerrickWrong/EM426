package com.configurations;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.pagination.Paginator;
import net.dean.jraw.references.SubredditReference;

public class RedditAPIManager {
 	
	private RedditClient client;
	private boolean connected2Internet = false;

	public boolean isConnected2Internet() {
		return connected2Internet;
	}

	public RedditAPIManager(RedditClient client) {
		this.client = client;
	}

	public Listing<Submission> getRedditPostBySubredditTitle(String subName) {

		if (client == null) {
			connected2Internet = false;
			return Listing.empty(); // no internet

		}
		connected2Internet = true;
		return this.getPaginator(subName).next();
	}

	public DefaultPaginator<Submission> getPaginator(String subName) {

		SubredditReference subRed = this.client.subreddit(subName);

		DefaultPaginator<Submission> paginator = subRed.posts().limit(Paginator.RECOMMENDED_MAX_LIMIT)
				.sorting(SubredditSort.TOP).timePeriod(TimePeriod.MONTH).build();

		return paginator;
	}
	
	

}
