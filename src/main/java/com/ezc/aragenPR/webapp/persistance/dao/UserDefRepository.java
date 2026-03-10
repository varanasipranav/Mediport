package com.ezc.aragenPR.webapp.persistance.dao;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ezc.aragenPR.webapp.model.UserDefaults;
import com.ezc.aragenPR.webapp.model.UserDefaultsKey;



public interface UserDefRepository extends JpaRepository<UserDefaults, UserDefaultsKey> {
     
	@Modifying
	@Query("DELETE FROM UserDefaults u WHERE u.userId = ?1")	
	void deleteUserDefaults(Long userId);

	@Query(nativeQuery = true,value="select a.USER_ID,b.EUD_value,a.FIRST_NAME from ezc_users a,ezc_user_defaults b where a.id=b.EUD_USER_ID and eud_key='STORE'")
	List<Object[]> getStoreUsers();

	UserDefaults findByuserIdAndKey(Long userDocId, String location);
}