package com.gxx.ordering_platform.entity;

import java.util.Date;

import lombok.Data;

@Data
public class WechatUser {

	private int u_ID;
	private String U_OpenId;
	private Date U_RegisterTime;
	private Date U_LoginTime;
	private int U_Status;
}
