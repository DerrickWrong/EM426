package com.models;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;
import java.util.PriorityQueue;

import org.junit.jupiter.api.Test;

import com.models.demands.Share;

class StockOrderTest {

	@Test
	void test() {
		
		PriorityQueue<Integer> floatingShareQueue = new PriorityQueue<>(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				
				return Integer.compare(o1, o2);
			}
		});		
	}
 

}
