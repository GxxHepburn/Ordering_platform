package com.gxx.ordering_platform.entity;

import lombok.Data;

@Data
public class ReturnOrdersPTimes {

	String times;
	int totalRefundNumbers;
	int totalReturnNum;
	float totalRefundPrice;
}
