package com.gxx.ordering_platform.entity;

import java.util.Date;

import lombok.Data;

@Data
public class Multi_Orders_Tab_Tabtype {

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
	private int O_isPayNow;
	private String O_OutTradeNo;
	private float O_ReturnNum;
	private int  O_NumberOfDiners;
	
	private String T_Name;
	private String TT_Name;
}
