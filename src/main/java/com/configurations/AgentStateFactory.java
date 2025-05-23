package com.configurations;
  
import org.springframework.stereotype.Component;

import com.github.pnavais.machine.StateMachine; 
import com.github.pnavais.machine.api.message.Messages;
import com.github.pnavais.machine.model.State;

// This class is for defining the states and transitions for all the agents.

@Component
public class AgentStateFactory {

	// Retail Investors States
	public static class ApeState{
		public static State IDLEAPE = new State("Ape Idle");
		public static State OBSERVE = new State("Ape Observe");
		public static State BUY = new State("Ape Buy");
		public static State HOLD = new State("Ape Hold and Wait");
		public static State SELL = new State("Ape Sell");
		
		public static String SELLMESSAGE = "Panic Sell";
		public static String BUYMOREMESSAGE = "Buy More Stonk!"; 
	}
	
 
	public StateMachine ApeStateMachine() {
		
		// See Ape States Diagram for more information
		return StateMachine.newBuilder()
				.from(ApeState.IDLEAPE).to(ApeState.OBSERVE).on(Messages.EMPTY)
				.from(ApeState.OBSERVE).to(ApeState.BUY).on(ApeState.BUYMOREMESSAGE)
				.from(ApeState.BUY).to(ApeState.HOLD).on(Messages.EMPTY)
				.from(ApeState.HOLD).to(ApeState.SELL).on(ApeState.SELLMESSAGE)
				.from(ApeState.HOLD).to(ApeState.BUY).on(ApeState.BUYMOREMESSAGE)
				.from(ApeState.SELL).to(ApeState.BUY).on(ApeState.BUYMOREMESSAGE).build();	
	}
	
	// Hedgie(s?) States
	public static class HedgieState{
		
		public static State IDLE = new State("Hedgie Observing");
		public static State BNS = new State("Hedgie Borrow and Short");
		public static State WAITING = new State("Hedgie waiting to turn profit");
		public static State COVER = new State("Hedgie buy back Stock");
		public static State SHORTMORE = new State("Hedgie borrow more to short");
		public static State BANKRUPT = new State("Hedgie unable to cover position");
		
		public static String IDLEMSG = "back to idle";
		public static String SMMESSAGE = "Short more";
		public static String CMMESSAGE = "Cover Now";
		public static String GMMESSAGE = "Game Over";
	}
	 
	public StateMachine HedgieStateMachine(){
		// See Hedgies State Diagram for more information
		return StateMachine.newBuilder()
				.from(HedgieState.IDLE).to(HedgieState.BNS).on(Messages.EMPTY)
				.from(HedgieState.BNS).to(HedgieState.WAITING).on(Messages.EMPTY)
				.from(HedgieState.BNS).to(HedgieState.IDLE).on(HedgieState.IDLEMSG)
				.from(HedgieState.WAITING).to(HedgieState.COVER).on(HedgieState.CMMESSAGE)
				.from(HedgieState.WAITING).to(HedgieState.SHORTMORE).on(HedgieState.SMMESSAGE)
				.from(HedgieState.WAITING).to(HedgieState.COVER).on(HedgieState.CMMESSAGE)
				.from(HedgieState.COVER).to(HedgieState.BANKRUPT).on(HedgieState.GMMESSAGE).build();
	}
	
	// Broker State
	public static class BrokerState{
		
		public static State IDLE = new State("Broker Idling");
		public static State PROCESSING = new State("Broker Processing Orders");
		public static State HALTBUY = new State("Broker halting all buy orders");
		 
		public static String HALTMESSAGE = "Halt all buy orders";
		public static String IDLEMESSAGE = "Return to Idle";
	}
	 
	StateMachine BrokerStateMachine() {
		// See Broker State Diagram for more information
		return StateMachine.newBuilder()
				.from(BrokerState.IDLE).to(BrokerState.PROCESSING).on(Messages.EMPTY)
				.from(BrokerState.PROCESSING).to(BrokerState.HALTBUY).on(BrokerState.HALTMESSAGE)
				.from(BrokerState.HALTBUY).to(BrokerState.IDLE).on(BrokerState.IDLEMESSAGE).build();
	}
	
	// Institutional Realtors
	public static class IInvestorState{
		
		public static State IDLE = new State("Institutional Investor hold and wait");
		public static State SELL = new State("Institutional Investor panic sell");
		public static State BUY = new State("Institutional Investor re-invest");
		public static State NOHOLD = new State("Institutional Investor not holding");
		
		public static String TUMMESSAGE = "Price tumpled";
		public static String RIMESSAGE = "Re-invest message";
	}
	 
	public StateMachine InstitutionalStateMachine() {
		
		// See Institutional Investor diagram for more information
		return StateMachine.newBuilder()
				.from(IInvestorState.IDLE).to(IInvestorState.SELL).on(IInvestorState.TUMMESSAGE)
				.from(IInvestorState.SELL).to(IInvestorState.NOHOLD).on(Messages.EMPTY)
				.from(IInvestorState.NOHOLD).to(IInvestorState.BUY).on(IInvestorState.RIMESSAGE)
				.from(IInvestorState.BUY).to(IInvestorState.IDLE).on(Messages.EMPTY).build();
	}
	
	public static class MarketState{
		
		public static State IDLE = new State("Market IDLE");
		public static State SELL1P = new State("Market selling 1%");
		public static State SELL5P = new State("Market selling 5%");
		public static State SELL10P = new State("Market selling 10%");
		public static State BUY1P = new State("Market buying 1%");
		public static State BUY5P = new State("Market buying 5%");
		public static State BUY10P = new State("Market buying 10%");
		
		public static String BACK2IDLE = "IDLE";
		public static String SELLNOW = "Sell";
		public static String BUYNOW = "Buy";
		public static String NEXTSTATE = "NEXT";
	}
	 
	public StateMachine MarketStateMachine() {
		return StateMachine.newBuilder()
				.from(MarketState.IDLE).to(MarketState.SELL1P).on(MarketState.SELLNOW)
				.from(MarketState.SELL1P).to(MarketState.SELL5P).on(MarketState.NEXTSTATE)
				.from(MarketState.SELL5P).to(MarketState.SELL10P).on(MarketState.NEXTSTATE)
				.from(MarketState.SELL10P).to(MarketState.IDLE).on(MarketState.BACK2IDLE)
				.from(MarketState.SELL5P).to(MarketState.IDLE).on(MarketState.BACK2IDLE)
				.from(MarketState.SELL1P).to(MarketState.IDLE).on(MarketState.BACK2IDLE)
				.from(MarketState.IDLE).to(MarketState.BUY1P).on(MarketState.BUYNOW)
				.from(MarketState.BUY1P).to(MarketState.BUY5P).on(MarketState.NEXTSTATE)
				.from(MarketState.BUY5P).to(MarketState.BUY10P).on(MarketState.NEXTSTATE)
				.from(MarketState.BUY10P).to(MarketState.IDLE).on(MarketState.BACK2IDLE)
				.from(MarketState.BUY5P).to(MarketState.IDLE).on(MarketState.BACK2IDLE)
				.from(MarketState.BUY1P).to(MarketState.IDLE).on(MarketState.BACK2IDLE).build();
	}

	// Listing Company State

}
