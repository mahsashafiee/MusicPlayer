package com.example.musicplayer.network.models;

import java.util.List;
import com.squareup.moshi.Json;

public class NetworkData {

	@Json(name = "data")
	private List<DataItem> data;

	public void setData(List<DataItem> data){
		this.data = data;
	}

	public List<DataItem> getData(){
		return data;
	}

	@Override
 	public String toString(){
		return 
			"Response{" +
			",data = '" + data + '\'' + 
			"}";
		}
}