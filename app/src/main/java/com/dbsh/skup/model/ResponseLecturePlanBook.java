package com.dbsh.skup.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResponseLecturePlanBook {
	@SerializedName("RTN_STATUS")
	@Expose
	private String rtnStatus;

	@SerializedName("LIST")
	@Expose
	private ArrayList<ResponseLecturePlanBookList> responseLecturePlanBookLists;

	public String getRtnStatus() {
		return rtnStatus;
	}

	public ArrayList<ResponseLecturePlanBookList> getResponseLecturePlanBookLists() {
		return responseLecturePlanBookLists;
	}
}
