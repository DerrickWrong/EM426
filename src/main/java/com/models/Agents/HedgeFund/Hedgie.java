package com.models.Agents.HedgeFund;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.configurations.AgentStateConfig.HedgieState;
import com.github.pnavais.machine.StateMachine;
import com.github.pnavais.machine.api.message.Messages;
import com.models.demands.ShareInfo;
import com.models.demands.StockOrder;
import com.models.demands.StockOrder.type;

import em426.agents.Agent;
import jakarta.annotation.PostConstruct;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class Hedgie extends Agent {

	private double originalPrinciple = 7.5E9; // 7 billion dollar
	private double shortBidPercent = 0.95;
	private double marginReqPercent = 0.5;

	private DoubleProperty balance = new SimpleDoubleProperty(originalPrinciple);
	private DoubleProperty currBalance = new SimpleDoubleProperty(0);
	private DoubleProperty borrow2ShortRatio = new SimpleDoubleProperty(50); // % to borrow from institutional investors
	private DoubleProperty trigger2Cover = new SimpleDoubleProperty(50); // need to sell more or pay a higher premium
	private DoubleProperty getMarginCall = new SimpleDoubleProperty(80); // % of loss to trigger margin call
	private DoubleProperty cashoutProfitAt = new SimpleDoubleProperty(30); // % of profit to trigger a cashout

	@Autowired
	Sinks.Many<StockOrder> orderSink;

	@Autowired
	Flux<ShareInfo> shareInfoFlux;

	@Autowired
	@Qualifier("HedgieStateMachine")
	StateMachine stateMachine;

	@PostConstruct
	public void init() {

		this.shareInfoFlux.subscribe(s -> {

			if ((balance.get() == this.originalPrinciple) && stateMachine.getCurrent() == HedgieState.IDLE) {

				// kick off the simulation by sending shorting order
				double sellingPrice = shortBidPercent * s.getCurrentPrice();
				double share2Short = Math.round(originalPrinciple / (marginReqPercent * s.getCurrentPrice()));
				balance.set(originalPrinciple - share2Short * s.getCurrentPrice());
				StockOrder order = new StockOrder(this.getId(), type.SHORT, sellingPrice, share2Short);
				this.orderSink.tryEmitNext(order);

				System.out.println("Hedgie: selling " + share2Short + " which is "
						+ (100.0 * share2Short / s.getCurrVolume()) + " % of all shares @ $" + sellingPrice + ".");

				// move to BNS state
				stateMachine.send(Messages.EMPTY);
			}

			System.out.println("Hedgie in state " + stateMachine.getCurrent().getName());

		});

	}

	public DoubleProperty getBalance() {
		return balance;
	}

	public DoubleProperty getCurrBalance() {
		return currBalance;
	}

	public DoubleProperty getBorrow2ShortRatio() {
		return borrow2ShortRatio;
	}

	public DoubleProperty getTrigger2Cover() {
		return trigger2Cover;
	}

	public DoubleProperty getGetMarginCall() {
		return getMarginCall;
	}

	public DoubleProperty getCashoutProfitAt() {
		return cashoutProfitAt;
	}

}
