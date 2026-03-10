package com.ezc.aragenPR.webapp.dto;

import java.util.ArrayList;
import java.util.List;

import com.ezc.aragenPR.webapp.model.Users;

public class WFRow {

	private int level;
	private List<Users> userList;
	private List<String> selUserList=new ArrayList<String>();
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public List<Users> getUserList() {
		return userList;
	}
	public void setUserList(List<Users> userList) {
		this.userList = userList;
	}
	public List<String> getSelUserList() {
		return selUserList;
	}
	public void setSelUserList(List<String> selUserList) {
		this.selUserList = selUserList;
	}
	
	
}
