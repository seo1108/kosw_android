package kr.co.photointerior.kosw.rest;

import android.content.Context;
import android.util.Log;

import kr.co.photointerior.kosw.conf.ConditionHolder;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * HttpClient class for RestApi.
 * It using the retrofit3.
 * Created by kugie
 */
public class DefaultRestClient<T> {
    private T service;
    private Context context;
    public DefaultRestClient(Context context){
        this.context = context;
    }
    public T getClient(Class<? extends T> type) {
//        if (service == null) {
            OkHttpClient okHttpClient = HttpClientHolder.instance().getHttpClient(context);
            Retrofit client = new Retrofit.Builder()
                    .baseUrl(ConditionHolder.instance().getRestBaseUrl())
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = client.create(type);
//        }
        return service;
    }
}
