package com.models.demands;

import com.MIT.agents.Demand;

public class News extends Demand  {

	public final String postId;
	public final String title;
	public final String sourceURL;
	public final Integer numOfComments;
	
	public final float score;
	
	public News(String title, String id, String url, Integer numOfComments) {
		
		this.title = title;
		this.postId = id;
		this.sourceURL = url;
		this.numOfComments = numOfComments;
		
		// TODO The content and replies will be processed by a NN and assign a score
		this.score = (float) Math.random();
	}
	
}
