package com.gxx.ordering_platform.xpyunSDK.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.gxx.ordering_platform.xpyunSDK.vo.ObjectRestResponse;
import com.gxx.ordering_platform.xpyunSDK.vo.PrintRequest;
import com.gxx.ordering_platform.xpyunSDK.util.HttpClientUtil;

/**
 * 云打印相关接口封装类
 *
 * @author Gxx
 * @date 2021年3月19日
 */
public class PrintService {

private static String BASE_URL = "https://open.xpyun.net/api/openapi";

    /**
     * 3.打印小票订单
     * @param restRequest
     * @return
     */
    public ObjectRestResponse<String> print(PrintRequest restRequest) {
        String url = BASE_URL + "/xprinter/print";
        String jsonRequest = JSON.toJSONString(restRequest);
        String resp = HttpClientUtil.doPostJSON(url, jsonRequest);
        ObjectRestResponse<String> result = JSON.parseObject(resp, new TypeReference<ObjectRestResponse<String>>(){});
        return result;
    }
}
