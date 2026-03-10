package com.ezc.aragenPR.webapp.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name="EZC_USERS")
public class Users extends Auditable<String> {


	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	
	@Column(name="USER_ID")
	private String userId;
	
	@Column(name="FIRST_NAME")
	private String firstName;
	
	@Column(name="LAST_NAME")
	private String lastName;
	
	@Column(name="EMAIL")
	private String email;
	
	
	@Column(name="PASSWORD")
	private String password;
	
	@Column(name="IS_ENABLED")
	private boolean enabled;

	
	 @ManyToMany(fetch = FetchType.EAGER)
	 @JoinTable(name = "users_roles", joinColumns =
	 			@JoinColumn(name = "user_id", referencedColumnName = "id"), 
	 			inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id	")
	 )
	 private Collection<Roles> roles;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_privileges",
			joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "prv_id"))
	private Set<UserPrivilege> privileges = new HashSet<>();

	public Set<UserPrivilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(Set<UserPrivilege> privileges) {
		this.privileges = privileges;
	}

	 @OneToMany(mappedBy="users",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	    private Set<UserDefaults> userDefaults;



		public Set<UserDefaults> getUserDefaults() {
		return userDefaults;
	}

	public void setUserDefaults(Set<UserDefaults> userDefaults) {
		this.userDefaults = userDefaults;
	}

		public Users() {
	    }

	public Collection<Roles> getRoles() {
		return roles;
	}

	public void setRoles(Collection<Roles> roles) {
		this.roles = roles;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

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

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	private String vendorCode;
	public String getVendorCode() {
		return vendorCode;
	}
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}
	@Override
	public String toString() {
		return "Users [id=" + id + ", userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", email=" + email + ", password=" + password + ", enabled=" + enabled + "]";
	}

	public Users(String userId, String firstName, String lastName, String email, String password, boolean enabled) {
		super();
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.enabled = enabled;
	}
}
