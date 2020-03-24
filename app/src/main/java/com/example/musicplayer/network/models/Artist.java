package com.example.musicplayer.network.models;

import com.squareup.moshi.Json;

public class Artist{

	@Json(name = "picture_xl")
	private String pictureXl;

	@Json(name = "picture_big")
	private String pictureBig;

	@Json(name = "name")
	private String name;

	@Json(name = "picture_small")
	private String pictureSmall;

	@Json(name = "id")
	private int id;

	@Json(name = "picture")
	private String picture;

	@Json(name = "picture_medium")
	private String pictureMedium;

	public void setPictureXl(String pictureXl){
		this.pictureXl = pictureXl;
	}

	public String getPictureXl(){
		return pictureXl;
	}

	public void setPictureBig(String pictureBig){
		this.pictureBig = pictureBig;
	}

	public String getPictureBig(){
		return pictureBig;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setPictureSmall(String pictureSmall){
		this.pictureSmall = pictureSmall;
	}

	public String getPictureSmall(){
		return pictureSmall;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setPicture(String picture){
		this.picture = picture;
	}

	public String getPicture(){
		return picture;
	}

	public void setPictureMedium(String pictureMedium){
		this.pictureMedium = pictureMedium;
	}

	public String getPictureMedium(){
		return pictureMedium;
	}

	@Override
 	public String toString(){
		return 
			"Artist{" + 
			"picture_xl = '" + pictureXl + '\'' +
			",picture_big = '" + pictureBig + '\'' + 
			",name = '" + name + '\'' +
			",picture_small = '" + pictureSmall + '\'' + 
			",id = '" + id + '\'' +
			",picture = '" + picture + '\'' + 
			",picture_medium = '" + pictureMedium + '\'' + 
			"}";
		}
}