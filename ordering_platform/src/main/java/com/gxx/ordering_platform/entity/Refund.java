package com.gxx.ordering_platform.entity;

import lombok.Data;

@Data
public class Refund {

	private int R_ID;
	private int R_MID;
	private int R_UID;
	private int R_OID;
	private int R_PID;
	private int R_ORID;
	private int R_Is_OfLine;
	
	private String R_Transaction_Id;
	private String R_Out_Trade_No;
	private String R_Out_Refund_No;
	private String R_Refund_Id;
	private String R_Refund_Fee;
	private String R_Total_Fee;
	
	private String R_Submit_Time;
	private String R_Nonce_Str;
	private String R_Sign;
	private String R_Return_Msg;
	private String R_Mch_Id;
	private String R_Sub_Mch_Id;
	private String R_Cash_Fee;
	private String R_Coupon_Refund_Fee;
	private String R_Refund_Channel;
	private String R_Appid;
	private String R_Result_Code;
	private String R_Coupon_Refund_Count;
	private String R_Cash_Refund_Fee;
	private String R_Return_Code;
	
	private String R_Success_Time;
	private String R_Settlement_Total_Fee;
	private String R_Refund_Request_Source;
	private String R_Refund_Status;
	private String R_Settlement_Refund_Fee;
	private String R_Refund_Recv_Account;
	private String R_Refund_Account;
}
