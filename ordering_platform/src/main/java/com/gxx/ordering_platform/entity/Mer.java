package com.gxx.ordering_platform.entity;

import java.util.Date;

import lombok.Data;

@Data
public class Mer {

	private int M_ID;
	private String M_Name;
	private String M_Address;
	private String M_Phone;
	private String M_BeginTime;
	private String M_EndTime;
	private Date M_RegisterTime;
	private String M_img;
	private String M_Sub_Mch_ID;
	private int M_IsInOpenTime;
	private int M_IsBan;
}
