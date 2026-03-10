package com.ezc.aragenPR.webapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "EZC_USER_PRIVILEGES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrivilege {

    @Id
    @Column(name="prv_id")
    private String id;

    @Column(name = "prv_desc",unique = true)
    private String desc;

    @ManyToMany(mappedBy = "privileges")
    private Set<Users> users = new HashSet<>();

    public UserPrivilege(String id, String desc) {
        this.id = id;
        this.desc = desc;
    }
}
