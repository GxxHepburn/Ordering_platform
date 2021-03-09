package com.gxx.ordering_platform.entity;

import java.util.Date;

import lombok.Data;

@Data
public class SD {

	String ouid;
	
	int ttid;
	String ttname;
	int tid;
	String tname;
	
	Date orderingTime;
	Date payTime;
	Integer continuedTime;
	String payMethod;
	float payPrice;
	int numberOfDiners;
	float averageNumberOfDiners;
}
