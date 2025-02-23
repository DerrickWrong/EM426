package com.models.demands;

import com.MIT.agents.Demand;

public class News extends Demand {

	private final String postId;
	private final String title;
	private final String sourceURL;
	private final Integer numOfComments;

	public String getPostId() {
		return postId;
	}

	public String getTitle() {
		return title;
	}

	public String getSourceURL() {
		return sourceURL;
	}

	public Integer getNumOfComments() {
		return numOfComments;
	}

	public float getScore() {
		return score;
	}

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
