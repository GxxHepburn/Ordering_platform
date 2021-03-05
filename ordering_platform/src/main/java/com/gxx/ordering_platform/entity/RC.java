package com.gxx.ordering_platform.entity;

import java.util.Date;

import lombok.Data;

@Data
public class RC {

	//orders-
	String ousid;
	// 桌台 tab tabtype
	int ttid;
	String ttname;
	int tid;
	String tname;
	
	// sku orderreturndetail-
	String spec;
	String propOne;
	String propTwo;
	
	// food foodtype
	int ftid;
	String ftname;
	int fid;
	String fname;
	
	// food orderreturndetail
	String unit;
	float price;
	String num;
	float totalPrice;
	
	// orders orderreturn-
	Date orderTime;
	Date returnTime;
}
