package com.gxx.ordering_platform.entity;

import lombok.Data;

@Data
public class TR {

	int tabnum;
	int tabPersonNum;
	
	int tradeNum;
	int openingNum;
	int numberOfDiners;
	
	float attendance;
	float openingRate;
	float turnoverRate;
}
