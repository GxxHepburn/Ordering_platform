package com.gxx.ordering_platform.xpyunSDK.vo;

import lombok.Data;

/**
 * 打印机打印小票请求参数
 *
 * @author Gxx
 * @date 2021年3月19日
 */

@Data
public class PrintRequest extends RestRequest {

	/**
     * 打印机编号
     */
    private String sn;

    /**
     * 打印内容,不能超过5000字节
     */
    private String content;

    /**
     * 打印份数，默认为1
     */
    private int copies = 1;

    /**
     * 打印模式，默认为0
     */
    private int mode = 0;

    /**
     * 支付方式41~55：支付宝 微信 ...
     */
    private Integer payType;
    /**
     * 支付与否59~61：退款 到账 消费
     */
    private Integer payMode;
    /**
     * 支付金额
     */
    private Double money;
    /**
     * 声音播放模式，0 为取消订单模式，1 为静音模式，2 为来单播放模式，默认为 2 来单播放模式
     */
    private Integer voice;
}
