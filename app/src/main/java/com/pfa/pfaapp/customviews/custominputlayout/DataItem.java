package com.pfa.pfaapp.customviews.custominputlayout;

import com.google.gson.annotations.SerializedName;

public class DataItem{

	@SerializedName("name")
	private String name;

	@SerializedName("value")
	private String value;

	@SerializedName("key")
	private String key;

	public String getName(){
		return name;
	}

	public String getValue(){
		return value;
	}

	public String getKey(){
		return key;
	}
}