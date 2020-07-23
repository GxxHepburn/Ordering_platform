package com.gxx.ordering_platform.entity;


import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
public class WxPayNotifyV0 implements Serializable {

	private String appid;
	private String attach;
	private String bank_type;
	private String fee_type;
	private String is_subscribe;
	private String mch_id;
	private String nonce_str;
	private String openid;
	private String out_trade_no;//根据这个定位订单，对订单数据做最后的修改
	private String result_code;
	private String return_code;
	private String sign;
	private String time_end;
	private String total_fee;
	private String coupon_fee;
	private String coupon_count;
	private String coupon_type;
	private String coupon_id;
	private String trade_type;
	private String transaction_id;
}
