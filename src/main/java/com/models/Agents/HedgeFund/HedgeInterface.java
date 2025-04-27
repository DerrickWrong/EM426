package com.models.Agents.HedgeFund;

import com.models.demands.ShareInfo;

public interface HedgeInterface {
	
	void observe(ShareInfo shareinfo);
	
	void react();
	
}
