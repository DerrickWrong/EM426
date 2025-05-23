package com.models.Agents;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.configurations.AgentStateFactory;
import com.configurations.AgentStateFactory.HedgieState;
import com.github.pnavais.machine.StateMachine;
import com.github.pnavais.machine.api.message.Messages;
import com.models.StockExchange;
import com.models.demands.Share;
import com.models.demands.ShareInfo;
import com.models.demands.StockOrder;
import com.models.demands.StockOrder.type;
import com.utils.SimAgentTypeEnum;

import em426.agents.Agent;
import em426.api.ActState;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import javafx.util.Pair;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler; 

@Component
@Scope("prototype")
public class HedgeFund extends Agent {

	private double originalPrinciple = 7.5E9; // 7 billion dollar
	private double shortBidPercent = 0.95;
	private double marginReqPercent = 0.5;
	private int numberOfDump = 10; // default is 10 (1 is to short everything at once)
	private double currSellingPrice;
	private double balance;

	private StockLender lender;
  
	@Autowired
	Flux<ShareInfo> shareInfoFlux;

	@Autowired
	@Qualifier("completedOrderFlux")
	Flux<StockOrder> processedOrderFlux;

	@Autowired
	AgentStateFactory agentFactory; 
	
	StateMachine stateMachine;

	@Autowired
	StockExchange stockExchange;

	@Autowired
	Flux<Pair<UUID, Share>> marginCallFlux;
	
	@Autowired
	@Qualifier("sellScheduler")
	Scheduler sellerScheduler;
	
	Disposable a1, a2, a3;

	public HedgeFund() {
	}

	public void setParameter(StockLender lender, double principle, double shortBidPercent, double marginReqPercent,
			int numberOfDump, double cashOutProfitAt) {
		this.lender = lender;

		this.originalPrinciple = principle;
		this.shortBidPercent = shortBidPercent;
		this.marginReqPercent = marginReqPercent;
		this.numberOfDump = numberOfDump;
	}
	
	@PreDestroy
	void destroy() {
		
		this.a1.dispose();
		this.a2.dispose();
		this.a3.dispose();
	}

	@PostConstruct
	public void init() {
		
		this.stateMachine = this.agentFactory.HedgieStateMachine();
		
		double fund = this.originalPrinciple / this.numberOfDump;

		this.stateMachine.send(Messages.EMPTY);

		this.a1 = shareInfoFlux.publishOn(sellerScheduler).subscribe(s -> {

			if (stateMachine.getCurrent() == HedgieState.BNS) {

				this.shortingStock(fund, s);
			}


		});

		// Processed Orders
		this.a2 = processedOrderFlux.filter(f -> {

			return (f.getUUID() == this.getId()) && (f.getActState() == ActState.COMPLETE);

		}).subscribe(po -> {

			StockOrder currOrder = this.stockExchange.getStockListing().sellOrderQueue.peek();

			// TODO - hack
			if (currOrder == null) {
				return;
			}

			if (currOrder.getUUID() == this.getId()) {
				return; // can't short more stock until supply has been consumed
			}
			
			if(po.getOrderType() == type.COVER ) {
				this.stateMachine.send(HedgieState.CMMESSAGE); // move to cover state & lock the agent
			}
			
			stateMachine.send(HedgieState.IDLEMSG);

		});

		// signal for getting margin call
		this.a3 = marginCallFlux.filter(f -> {

			return this.stateMachine.getCurrent() != HedgieState.COVER;

		}).subscribe(call -> {
  
			
			if(this.stateMachine.getAllTransitions() == HedgieState.COVER) {
				return;
			}
			
			//this.coverPosition();
			this.stateMachine.send(HedgieState.CMMESSAGE);  
			// this is when the hedgie had to buy back all the stocks
			//System.out.println("Hedgie getting Margin call!!!");
		});
	}

	private void shortingStock(double fund, ShareInfo s) {

		// kick off the simulation by sending shorting order
		currSellingPrice = shortBidPercent * s.getCurrentPrice();
		int share2Short = (int) Math.round(fund / (marginReqPercent * s.getCurrentPrice()));
		balance = fund - share2Short * s.getCurrentPrice();

		StockOrder order = new StockOrder(this.getId(), type.SHORT, currSellingPrice, share2Short,
				SimAgentTypeEnum.Hedgie, s.getTimestamp());

		this.lender.borrowStock(order);

		this.balance = this.balance - fund;

		System.out.println("Hedgie: selling " + share2Short + " which is " + (100.0 * share2Short / s.getCurrVolume())
				+ " % of all shares @ $" + currSellingPrice + ".");

		// move to BNS state
		stateMachine.send(Messages.EMPTY);
	}

}
