package com.gxx.ordering_platform.entity;

import lombok.Data;

@Data
public class Pay {
	
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
}
