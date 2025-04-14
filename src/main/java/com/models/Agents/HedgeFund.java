package com.models.Agents;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.configurations.AgentStateConfig.HedgieState;
import com.github.pnavais.machine.StateMachine;
import com.github.pnavais.machine.api.message.Messages;
import com.models.StockExchange;
import com.models.demands.Share;
import com.models.demands.ShareInfo;
import com.models.demands.StockOrder;
import com.models.demands.StockOrder.type;
import com.utils.HelperFn;
import com.utils.SimAgentTypeEnum;

import em426.agents.Agent;
import em426.api.ActState;
import jakarta.annotation.PostConstruct;
import javafx.util.Pair;
import reactor.core.publisher.Flux; 

@Component
@Scope("prototype")
public class HedgeFund extends Agent {

	private double originalPrinciple = 7.5E9; // 7 billion dollar
	private double shortBidPercent = 0.95;
	private double marginReqPercent = 0.5;
	private int numberOfDump = 10; // default is 10 (1 is to short everything at once)
	private double currSellingPrice;
	private double balance;
	private double cashoutProfitAt;

	private StockLender lender;
  
	@Autowired
	Flux<ShareInfo> shareInfoFlux;

	@Autowired
	@Qualifier("completedOrderFlux")
	Flux<StockOrder> processedOrderFlux;

	@Autowired
	@Qualifier("HedgieStateMachine")
	StateMachine stateMachine;

	@Autowired
	StockExchange stockExchange;

	@Autowired
	Flux<Pair<UUID, Share>> marginCallFlux;

	public HedgeFund() {
	}

	public void setParameter(StockLender lender, double principle, double shortBidPercent, double marginReqPercent,
			int numberOfDump, double cashOutProfitAt) {
		this.lender = lender;

		this.originalPrinciple = principle;
		this.shortBidPercent = shortBidPercent;
		this.marginReqPercent = marginReqPercent;
		this.numberOfDump = numberOfDump;
		this.cashoutProfitAt = cashOutProfitAt;
	}

	@PostConstruct
	public void init() {

		double fund = this.originalPrinciple / this.numberOfDump;

		this.stateMachine.send(Messages.EMPTY);

		this.shareInfoFlux.subscribe(s -> {

			if (stateMachine.getCurrent() == HedgieState.BNS) {

				this.shortingStock(fund, s);
			}

			double der = HelperFn.getDerivative(this.currSellingPrice, s.getCurrentPrice(), 1);
			double stat = (der / this.currSellingPrice);

			if (stat > 0 && stat >= cashoutProfitAt) {
				// cover position
				this.coverPosition();
			}

		});

		// Processed Orders
		this.processedOrderFlux.filter(f -> {

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
		this.marginCallFlux.filter(f -> {

			return this.stateMachine.getCurrent() != HedgieState.COVER;

		}).subscribe(call -> {
  
			
			if(this.stateMachine.getAllTransitions() == HedgieState.COVER) {
				return;
			}
			
			this.coverPosition();
			this.stateMachine.send(HedgieState.CMMESSAGE);  
			// this is when the hedgie had to buy back all the stocks
			System.out.println("Hedgie getting Margin call!!!");
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

	private void coverPosition() {

		Share borrowedShares = this.lender.getBorrowersTab().get(this.getId());

		StockOrder currOrder = this.stockExchange.getStockListing().sellOrderQueue.peek();

		double bidPrice = currOrder.getBidPrice() * 1.05;

		StockOrder coverOrder = new StockOrder(this.getId(), type.COVER, bidPrice, borrowedShares.getQuantity(),
				SimAgentTypeEnum.Hedgie, currOrder.getOrderRequestedAtTime() + 1L);
		
		this.stockExchange.processImmediateBuyOrder(coverOrder.getOrderRequestedAtTime(), coverOrder);
		
	}

}
