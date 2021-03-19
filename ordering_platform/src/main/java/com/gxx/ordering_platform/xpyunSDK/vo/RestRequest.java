package com.gxx.ordering_platform.xpyunSDK.vo;

import lombok.Data;

/**
 * 请求公共参数
 *
 * @author Gxx
 * @date 2021年3月19日
 */

@Data
public class RestRequest {

	/**
     * 芯烨云后台注册用户名
     */
    private String user;
    /**
     * 当前UNIX时间戳，10位，精确到秒
     */
    private String timestamp;
    /**
     * 对参数 user + UKEY + timestamp 拼接后（+号表示连接符）进行SHA1加密得到签名，值为40位小写字符串
     */
    private String sign;
    /**
     * debug=1返回非json格式的数据。仅测试时候使用
     */
    private String debug;
}
