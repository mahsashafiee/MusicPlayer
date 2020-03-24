package com.example.musicplayer.network.models;

import com.squareup.moshi.Json;

public class DataItem{

	@Json(name = "artist")
	private Artist artist;

	@Json(name = "title")
	private String title;

	@Json(name = "id")
	private int id;

	public void setArtist(Artist artist){
		this.artist = artist;
	}

	public Artist getArtist(){
		return artist;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	@Override
 	public String toString(){
		return 
			"DataItem{" +
			",artist = '" + artist + '\'' +
			",title = '" + title + '\'' +
			",id = '" + id + '\'' +
			"}";
		}
}