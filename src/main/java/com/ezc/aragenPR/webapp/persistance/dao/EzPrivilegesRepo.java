package com.ezc.aragenPR.webapp.persistance.dao;

import com.ezc.aragenPR.webapp.model.UserPrivilege;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EzPrivilegesRepo extends JpaRepository<UserPrivilege, String> {

}
