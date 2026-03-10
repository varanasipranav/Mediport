package com.ezc.aragenPR.webapp.model;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserForm extends Auditable<String> {
	
	private Long id;
	private String userId;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String stateCode;
	private List<String> location;
    private List<String> docType;
	private String busPlace;
	private String role;
	private String zone;
	private List<String> group;
	private List<String> privilegeIds;
	private String vendorCode;
	
	
	


	public String getBusPlace() {
		return busPlace;
	}

	public void setBusPlace(String busPlace) {
		this.busPlace = busPlace;
	}

	public List<String> getGroup() {
		return group;
	}

	public void setGroup(List<String> group) {
		this.group = group;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public UserForm() {
    }

	@NotNull
	@Size(min=2, max=30)
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}

	@NotNull
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@NotNull
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@NotNull
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	@NotNull
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<String> getLocation() {
		return location;
	}

	public void setLocation(List<String> location) {
		this.location = location;
	}

    public List<String> getDocType() {
        return docType;
    }

    public void setDocType(List<String> docType) {
        this.docType = docType;
    }

	public List<String> getPrivilegeIds() {
		return privilegeIds;
	}
	public void setPrivilegeIds(List<String> privilegeIds){ this.privilegeIds = privilegeIds; }

	public String getVendorCode() {
		return vendorCode;
	}
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}

}
