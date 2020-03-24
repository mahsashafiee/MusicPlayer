package com.example.musicplayer.network;

import android.content.Context;

import com.example.musicplayer.utils.Constants;
import com.example.musicplayer.utils.Utils;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class ServiceGenerator {
    private static Retrofit sRetrofit = null;
    private static long sCacheSize = 10 * 1024 * 1024;
    private static OkHttpClient sOkHttpClient;


    private static OkHttpClient getClient(Context context) {
        if (sOkHttpClient == null)
            sOkHttpClient = new OkHttpClient.Builder()
                    .cache(new Cache(context.getCacheDir(), sCacheSize))
                    .addInterceptor(chain -> {
                        Request request = chain.request();
                        request = request.newBuilder().addHeader("X-RapidAPI-Host", Constants.API_HOST)
                                .addHeader("X-RapidAPI-Key", Constants.API_KEY).build();
                        if (Utils.isNetworkAvailableAndConected(context))
                            request = request.newBuilder().addHeader("Cache-Control", "public, max-age=" + 5).build();
                        else
                            request = request.newBuilder().addHeader("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build();

                        return chain.proceed(request);
                    })
                    .build();

        return sOkHttpClient;

    }

    public static <T> T createService(Class<T> serviceClass, Context context) {

        if (sRetrofit == null) {
            sRetrofit = new Retrofit.Builder()
                    .client(getClient(context))
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build();
        }
        return sRetrofit.create(serviceClass);
    }

}
