package com.gxx.ordering_platform.entity;

import lombok.Data;

@Data
public class Food {

	private int F_ID;
	private int F_FTID;
	private int F_MID;
	private String F_Name;
	private String F_ImageUrl;
	private float F_Price;
	private int F_Status;
	private String F_Unit;
	private int F_Stock;
	private int F_SalesVolume;
	private String F_tag;
	private int Sort;
}
