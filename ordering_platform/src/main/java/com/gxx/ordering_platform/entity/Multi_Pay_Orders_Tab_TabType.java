package com.gxx.ordering_platform.entity;

import java.util.Date;

import lombok.Data;

@Data
public class Multi_Pay_Orders_Tab_TabType {

	private int P_ID;
	private int P_MID;
	private int P_OID;
	private int P_UID;
	private String P_Appid;
	private String P_Attach;
	private String P_Bank_Type;
	private String P_Fee_Type;
	private String P_Is_Subscribe;
	private String P_Mch_Id;
	private String P_Nonce_Str;
	private String P_Openid;
	private String P_Out_Trade_No;
	private String P_Result_Code;
	private String P_Return_Code;
	private String P_Sign;
	private String P_Time_End;
	private String P_Totle_Fee;
	private String P_Coupon_Fee;
	private String P_Coupon_Count;
	private String P_Coupon_Type;
	private String P_Coupon_Id;
	private String P_Trade_Type;
	private String P_Transaction_Id;
	
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
}
