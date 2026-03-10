package com.ezc.aragenPR.webapp.dto;

public class SAPResponse {

 boolean isError=false;
 String message="";
public boolean isError() {
	return isError;
}
public void setError(boolean isError) {
	this.isError = isError;
}
public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}
	
}
