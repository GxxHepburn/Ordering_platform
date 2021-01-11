package com.gxx.ordering_platform.entity;

import lombok.Data;

@Data
public class OrderReturnDetail {

	private int ORD_ID;
	private int ORD_ORID;
	private int ORD_OID;
	private int ORD_FID;
	private int ORD_FoodState;
	private float ORD_RealPrice;
	private String ORD_Spec;
	private String ORD_PropOne;
	private String ORD_PropTwo;
	private int ORD_Num;
	private String ORD_FName;
}
