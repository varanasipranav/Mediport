package com.ezc.aragenPR.webapp.persistance.dao;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ezc.aragenPR.webapp.model.Users;

public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByEmail(String email);
    
    Users findByUserId(String userId);
    
    Users findByUserIdOrEmail(String userId, String email);
    @Query(value = "select a.* from ezc_users a where id in (select user_id from users_roles where role_id in (select id from ezc_roles where name=:role))",nativeQuery = true)
    List<Users> findUsersByRole(String role);
    
    @Query(value = "select a.* from ezc_users a where id in ( select eud_user_id from ezc_user_defaults where EUD_VALUE LIKE :plant AND  EUD_USER_ID in (select user_id from users_roles where role_id in (select id from ezc_roles where name=:role)))",nativeQuery = true)
    List<Users> findUsersByRoleAndPlant(String plant,String role);


    
    
     
    
//    https://www.baeldung.com/spring-data-derived-queries

    @Override 
    void delete(Users user);
    
    
}