package com.ascend.wangfeng.udacityreviewer;

import retrofit2.http.GET;
import retrofit2.http.Query;
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
     * @return
     */
    @GET("api/v1/users/208891/submissions.json")
    Observable<String> applyReview(@Query("project_id") String id);
}
