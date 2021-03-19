package com.gxx.ordering_platform.xpyunSDK.vo;

import lombok.Data;

/**
 * 返回公共参数
 * @param <T>
 *
 * @author Gxx
 * @date 2021年3月19日
 */

@Data
public class ObjectRestResponse<T> {

	public static final String REST_RESPONSE_OK = "ok";

    /**
     * 返回码，正确返回0，【注意：结果正确与否的判断请用此返回参数】，错误返回非零
     */
    private int code;
    /**
     * 结果提示信息，正确返回”ok”，如果有错误，返回错误信息
     */
    private String msg;
    /**
     * 数据类型和内容详看私有返回参数data，如果有错误，返回null
     */
    private T data;
    /**
     * 服务器程序执行时间，单位：毫秒
     */
    private long serverExecutedTime;

    public ObjectRestResponse() {
        this.setCode(0);
        this.setMsg(REST_RESPONSE_OK);
    }

    public ObjectRestResponse code(int code) {
        this.setCode(code);
        return this;
    }

    public ObjectRestResponse data(T data) {
        this.setData(data);
        return this;
    }

    public ObjectRestResponse msg(String msg) {
        this.setMsg(msg);
        return this;
    }

    public ObjectRestResponse setResult(int code, T data) {
        this.setCode(code);
        this.setData(data);
        return this;
    }

    public ObjectRestResponse setResult(int code, T data, String msg) {
        this.setCode(code);
        this.setData(data);
        this.setMsg(msg);
        return this;
    }

}
