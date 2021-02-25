package com.gxx.ordering_platform.entity;

import java.util.Date;

import lombok.Data;

@Data
public class OrdersPHour {

	String hours;
	float totalPrice;
	int totalOrdersNumbers;
	int numberOfDinners;
	float pricePOrder;
	float pricePPeople;
}
