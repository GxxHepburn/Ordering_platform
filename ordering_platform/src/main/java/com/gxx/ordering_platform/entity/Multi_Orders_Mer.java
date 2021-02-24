package com.gxx.ordering_platform.entity;

import java.util.Date;

import lombok.Data;

@Data
public class Multi_Orders_Mer {

	private int O_ID;
	private int O_MID;
	private int O_UID;
	private int O_TID;
	private float O_TotlePrice;
	private int O_PayMethod;
	private int O_PayStatue;
	private Date O_OrderingTime;
	private Date O_PayTime;
	private String O_Remarks;
	private int O_TotleNum;
	private String O_UniqSearchID;
	private int O_IsPayNow;
	private String O_OutTradeNo;
	private float O_ReturnNum;
	private int O_NumberOfDiners;
	
	private int M_ID;
	private String M_Name;
	private String M_Address;
	private String M_Phone;
	private String M_BeginTime;
	private String M_EndTime;
	private Date M_RegisterTime;
	private String M_img;
}
