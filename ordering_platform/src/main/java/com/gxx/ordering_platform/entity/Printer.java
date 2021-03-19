package com.gxx.ordering_platform.entity;

import java.util.Date;

import lombok.Data;

@Data
public class Printer {

	private int P_ID;
	private int P_MID;
	private String P_Serial_No;
	private String P_No;
	private String P_OpenNo;
	private String P_Name;
	private Date P_Register_Time;
	// 是否在线
	private int P_Status;
	private int P_Cutter;
	private String P_Firmware_Version;
	private String P_SIM;
	private String P_SIM_Term_Of_Validity;
	private String P_Logo_Url;
	private String P_Voice_Type;
}
