package com.gxx.ordering_platform.entity;

import java.util.Date;

public class WechatUser {

	public int u_ID;
	public String U_OpenId;
	public Date U_RegisterTime;
	public Date U_LoginTime;
	public int getU_ID() {
		return u_ID;
	}
	public void setU_ID(int u_ID) {
		this.u_ID = u_ID;
	}
	public String getU_OpenId() {
		return U_OpenId;
	}
	public void setU_OpenId(String u_OpenId) {
		U_OpenId = u_OpenId;
	}
	public Date getU_RegisterTime() {
		return U_RegisterTime;
	}
	public void setU_RegisterTime(Date u_RegisterTime) {
		U_RegisterTime = u_RegisterTime;
	}
	public Date getU_LoginTime() {
		return U_LoginTime;
	}
	public void setU_LoginTime(Date u_LoginTime) {
		U_LoginTime = u_LoginTime;
	}
}
