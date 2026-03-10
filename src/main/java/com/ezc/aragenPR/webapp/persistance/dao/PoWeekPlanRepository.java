package com.ezc.aragenPR.webapp.persistance.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ezc.aragenPR.webapp.model.EzcPoWeekPlan;
import com.ezc.aragenPR.webapp.model.EzcPoWeekPlanId;

public interface PoWeekPlanRepository extends JpaRepository<EzcPoWeekPlan, EzcPoWeekPlanId> {


}
