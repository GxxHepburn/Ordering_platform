package com.gxx.ordering_platform.entity;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
public class WxRefundNotifyV0 implements Serializable {

	private String appid;
	private String mch_id;
	private String nonce_str;
	private String sub_appid;
	private String sub_mch_id;
	private String return_code;
	private String req_info;
}
