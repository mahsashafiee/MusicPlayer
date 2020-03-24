package com.example.musicplayer.repository;

import android.content.Context;
import android.util.Log;

import com.example.musicplayer.network.APIProvider;
import com.example.musicplayer.network.ServiceGenerator;
import com.example.musicplayer.network.models.NetworkData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkRepository {

    private static final String TAG = "NetworkRepository";

    private static NetworkRepository sInstance;
    private Context mContext;

    public static NetworkRepository getInstance(Context context) {
        if (sInstance == null)
            sInstance = new NetworkRepository(context);
        return sInstance;
    }

    private NetworkRepository(Context context) {
        mContext = context;
    }

    public void getArtistPics(String artistName) {
        Call<NetworkData> call = ServiceGenerator.createService(APIProvider.class, mContext).getArtistURL(artistName);
        call.enqueue(new Callback<NetworkData>() {
            @Override
            public void onResponse(Call<NetworkData> call, Response<NetworkData> response) {
                if (response.isSuccessful())
                    PlayList.getArtistURLs().getValue().add(response.body().getData().get(0).getArtist().getPictureMedium());
            }

            @Override
            public void onFailure(Call<NetworkData> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }
}
