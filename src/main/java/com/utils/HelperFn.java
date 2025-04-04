package com.utils;
  
public final class HelperFn {

	private HelperFn() {
		
	}
	
	public static double getDerivative(double t, double tp1, int duration) {
		
		return (tp1 - t) / duration;
	
	}
	
}
