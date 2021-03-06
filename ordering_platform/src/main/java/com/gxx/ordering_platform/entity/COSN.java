package com.gxx.ordering_platform.entity;

import lombok.Data;

@Data
public class COSN {

	// tab tabtype
	int ttid;
	String ttname;
	int tid;
	String tname;
	
	// orders
	int orderNum;
	int numberOfDiners;
	
	float totalOrderPrice;
	
	float totalPricePOrder;
	float totalPricePPerson;
}
