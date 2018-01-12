package com.ascend.wangfeng.udacityreviewer;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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
     * @param lang  language: zh-cn;
     * @return
     */
    @Headers({"Content-Type:application/json;charset=UTF-8"})
    @FormUrlEncoded
    @POST("api/v1/projects/{id}/submissions/assign.json")
    Observable<String> applyReview(@Path("id") String id, @Field("lang") String lang);
}
