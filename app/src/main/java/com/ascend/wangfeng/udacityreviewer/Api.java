package com.ascend.wangfeng.udacityreviewer;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by fengye on 2017/12/23.
 * email 1040441325@qq.com
 */

public interface Api {
    /**
     *
     * 获取 审阅数
     * @return
     */
    @GET("api/v1/me/certifications.json")
    Observable<String> getReviews();

    /**
     *
     * @param id 项目id;
     * @param lang  request payload 数据,需改变请求头Content-type,同时自己组装json数据的请求体
     * @return
     */
    @Headers({"Content-Type:application/json;charset=UTF-8",
    "Accept-Language:zh-CN,zh;q=0.9"})
    @POST("api/v1/projects/{id}/submissions/assign.json")
    Observable<String> applyReview(@Path("id") String id, @Body RequestBody lang);
}
