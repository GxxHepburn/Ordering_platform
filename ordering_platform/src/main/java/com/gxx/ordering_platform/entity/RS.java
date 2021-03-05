package com.gxx.ordering_platform.entity;

import lombok.Data;

@Data
public class RS {

	// sku orderreturndetail-
	String spec;
	String propOne;
	String propTwo;
	
	// food foodtype
	int ftid;
	String ftname;
	int fid;
	String fname;
	
	// orderreturndetail
	String unit;
	float price;
	int num;
	float totalPrice;
	
	int orderNum;
}
