package com.ascend.wangfeng.udacityreviewer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by fengye on 2017/12/23.
 * email 1040441325@qq.com
 */

public class Client {
    public static final String BASE_URL = "https://review-api.udacity.com/";
    public static Api getInstance(){
        return  Instance.API;
    }
    public static class Instance{
        public static final String AUTHOR ="Authorization";
        public static HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
        public static final String AUTHOR_CONTENT= MyApplication.getSp().getString("api","api");
        // public static final String AUTHOR_CONTENT="Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1MTY2MDM0NTIsImlhdCI6MTUxNDAxMTQ1Miwia2lkIjoibW1yZDQ5ZjJ5IiwidWlkIjoiMTA3NTY1MTgyNjIiLCJyb2xlcyI6WyJ1c2VyIl19.cLZN82Co6IZmt-YqKXZRkukM26URCMKyHnD3yxudibjhD9uf7PL5vAzy2UBaVTFJhIFeXKw1VMasBwbryJmqtSuPPyuNv7z4iFrMGYij4pvF6RyX7L3xTGC3P3_Y6JMga-SFgvvs85Q08HemE7QyCIXY_oUwwYONk_wuJ1WNTb3Ha99nSdTJ7oN5k_Mer3YKz3-AYY45VgwzptelxnIjmPURdWj_fJWjYnUAeoMoDyWrC_WljTtPadbOHNNr1mb8r_7AAXkV63bLGa-_AcONB7kjwW0t2AXFCXnW_sSByfcOEmLJ8mUQ_MNWnsnfTngKYNBoCsN-BB3__PF-8IlDCQ";
        private static OkHttpClient.Builder mBuilder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader(AUTHOR, AUTHOR_CONTENT)
                                .build();
                        return chain.proceed(request);
                    }
                })/*.addInterceptor(logInterceptor) */;

       private static Retrofit mRetrofit = new Retrofit.Builder()
                .client(mBuilder.build())
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        private static final Api API =mRetrofit.create(Api.class);


    }
}
