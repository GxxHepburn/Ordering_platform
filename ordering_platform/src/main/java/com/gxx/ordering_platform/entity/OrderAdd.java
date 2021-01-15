package com.gxx.ordering_platform.entity;

import java.util.Date;

import lombok.Data;

@Data
public class OrderAdd {

	private int OA_ID;
	private int OA_OID;
	private int OA_Sort;
	private int OA_MID;
	private int OA_UID;
	private int OA_TID;
	private float OA_TotlePrice;
	private Date OA_OrderingTime;
	private int OA_TotleNum;
	private int OA_ReturnNum;
	
	private String OA_IsTaking;
}
