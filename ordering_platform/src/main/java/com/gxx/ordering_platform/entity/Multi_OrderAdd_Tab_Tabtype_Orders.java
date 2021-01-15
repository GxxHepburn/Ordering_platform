package com.gxx.ordering_platform.entity;

import java.util.Date;

import lombok.Data;

@Data
public class Multi_OrderAdd_Tab_Tabtype_Orders {

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
	
	private int T_ID;
	private int T_TTID;
	private int T_Statue;
	private int T_PeopleOfDiners;
	private int T_Sort;
	private String T_Name;
	
	private int TT_ID;
	private int TT_MID;
	private String TT_Name;
	private int TT_Sort;
	private int TT_Statue;
	
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
	private int O_NumberOfDiners;
}
