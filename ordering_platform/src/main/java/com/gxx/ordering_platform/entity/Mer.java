package com.gxx.ordering_platform.entity;

import java.sql.Time;
import java.util.Date;

import lombok.Data;

@Data
public class Mer {

	private int M_ID;
	private String M_Name;
	private String M_Address;
	private String M_Phone;
	private Time M_BeginTime;
	private Time M_EndTime;
	private Date M_RegisterTime;
	private String M_img;
}
