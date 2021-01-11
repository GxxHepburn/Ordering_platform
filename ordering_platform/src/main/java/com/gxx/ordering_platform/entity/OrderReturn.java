package com.gxx.ordering_platform.entity;

import java.util.Date;

import lombok.Data;

@Data
public class OrderReturn {

	private int OR_ID;
	private int OR_OID;
	private int OR_Sort;
	private int OR_MID;
	private int OR_UID;
	private int OR_TID;
	private float OR_TotlePrice;
	private Date OR_ReturnTime;
	private Date OR_PayReturnTime;
	private String OR_ReturnRemarks;
	private int OR_TotleReturnNum;
}
