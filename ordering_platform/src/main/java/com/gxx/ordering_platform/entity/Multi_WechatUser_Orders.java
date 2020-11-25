package com.gxx.ordering_platform.entity;

import java.util.Date;

import lombok.Data;

@Data
public class Multi_WechatUser_Orders {

	private int U_ID;
	private String U_OpenId;
	private Date U_RegisterTime;
	private Date U_LoginTime;
	
	private Date O_OrderingTime;
}
