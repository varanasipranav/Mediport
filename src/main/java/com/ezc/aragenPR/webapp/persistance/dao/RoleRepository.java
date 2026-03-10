package com.ezc.aragenPR.webapp.persistance.dao;


import org.springframework.data.jpa.repository.JpaRepository;

import com.ezc.aragenPR.webapp.model.Roles;

public interface RoleRepository extends JpaRepository<Roles, Long> {

    Roles findByName(String name);

    @Override
    void delete(Roles role);

}
