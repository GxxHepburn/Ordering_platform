package com.gxx.ordering_platform.entity;

import lombok.Data;

@Data
public class RS2 {

	// refundPrice + getPrice
	float totalPrice;
	// SUM(orders.O_TotlePrice)
	float getPrice;
	// SUM(orderreturn.OR_TotlePrice)
	float refundPrice;
	// COUNT(DISTINCT  orders.UID) orders
	int userNum;
	// SUM(orders.O_NumberOfDiners)
	int numberOfDiners;
	// COUNT(*) orders
	int orderingCount;
	float averagePOrderingCount;
	float averagePNumberOfDiners;
}
