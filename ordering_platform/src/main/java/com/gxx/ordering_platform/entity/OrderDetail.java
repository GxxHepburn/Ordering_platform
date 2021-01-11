package com.gxx.ordering_platform.entity;

import lombok.Data;

@Data
public class OrderDetail {

	private int OD_ID;
	private int OD_OID;
	
	private int OD_OAID;
	
	private int OD_FID;
	private int OD_FoodState;
	private float OD_RealPrice;
	private String OD_Spec;
	private String OD_PropOne;
	private String OD_PropTwo;
	private int OD_Num;
	private int OD_RealNum;
	private String OD_FName;
}
