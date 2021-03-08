package com.gxx.ordering_platform.entity;

import java.util.Date;

import lombok.Data;

@Data
public class CD {

	String searchID;
	
	float totalPrice;
	Date lastOrderingTime;
	
	int orderingNum;
	float averagePrice;
}
