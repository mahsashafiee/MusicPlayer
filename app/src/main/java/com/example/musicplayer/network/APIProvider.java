package com.example.musicplayer.network;

import androidx.annotation.NonNull;

import com.example.musicplayer.network.models.NetworkData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIProvider {
    @GET("search")
    Call<NetworkData> getArtistURL(@NonNull @Query("q") String name);
}
