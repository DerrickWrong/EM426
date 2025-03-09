package com.models.demands;

import org.springframework.stereotype.Component;

import com.MIT.agents.AgentActable;

@Component
public class HedgeFund implements AgentActable {
	
	public enum State{
		IDLE, SELLNWAIT, BUY2COVER, BANKRUPT;
	}
	
	private float balanace = 0f;
	private float currentBalance = 0f;
	
	private Integer borrow2Short = 50;
	private Integer triggerCover = 100;
	private Integer decay2Sell = 1;
	private Integer cashoutAt = 10; // stock dropped to 10% of its purchase value
	
	
	
	@Override
	public void act() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void select() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void observe() {
		// TODO Auto-generated method stub
		
	}
	
	
}
