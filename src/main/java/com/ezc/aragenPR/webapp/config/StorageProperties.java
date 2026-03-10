package com.ezc.aragenPR.webapp.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({ "classpath:storage.properties" })
public class StorageProperties {
	
	
	/**
	 * Folder location for storing files
	 */
	private String location = "upload-dir";
	

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
