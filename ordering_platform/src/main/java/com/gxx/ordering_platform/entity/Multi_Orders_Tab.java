package com.gxx.ordering_platform.entity;

import java.util.Date;

import lombok.Data;

@Data
public class Multi_Orders_Tab {

	private int O_ID;
	private int O_MID;
	private int O_UID;
	private int O_TID;
	private float O_TotlePrice;
	private int O_PayStatue;
	private Date O_OrderingTime;
	private Date O_PayTime;
	private String O_Remarks;
	private int O_TotleNum;
	private String O_UniqSearchID;
	private String O_OutTradeNo;
	private String T_Name;
}
