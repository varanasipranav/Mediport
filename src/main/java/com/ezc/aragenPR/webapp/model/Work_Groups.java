package com.ezc.aragenPR.webapp.model;

import java.util.Collection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;



@Entity
@Table(name="EZC_WORK_GROUPS")
public class Work_Groups {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   
    @Column(name="EWG_GROUP")
	private String name;
	
	@Column(name="EWG_GROUP_DESC")
	private String desc;
	
	@Column(name="EWG_ROLE")
	private String role;

    protected Work_Groups() {

    }

    public Work_Groups(final String name,final String desc,final String role) {
        super();
        this.name = name;
        this.desc = desc;
        this.role = role;
    }

	public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Work_Groups groups = (Work_Groups) obj;
        if (!name.equals(groups.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("WorkGroups [name=").append(name).append("]").append("[desc=").append(desc).append("[role=").append(role).append("]");
        return builder.toString();
    }

}
