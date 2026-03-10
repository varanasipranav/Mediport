package com.ezc.aragenPR.webapp.service;

import java.util.List;

import com.ezc.aragenPR.webapp.dto.WeeklyPlanAmmendItem;
import com.ezc.aragenPR.webapp.dto.WeeklyPlanFormDetInput;
import com.ezc.aragenPR.webapp.model.EzcPoWeekPlan;
import com.ezc.aragenPR.webapp.model.PrdPlanFormDetInput;

public interface IPrdPlanAmmendService {

	public String ammendWeeklyPlan(String planId,int weekId,List<WeeklyPlanAmmendItem> itemList,String btnType);

	public String updateVendorWeeklyPlan(WeeklyPlanFormDetInput weeklyPlanFormDetInput);

	public String updateInSAP(String planId, int weekId, List<WeeklyPlanAmmendItem> itemList);
	
	public String ammendAndUpdatePO(String planId,int weekId,List<WeeklyPlanAmmendItem> itemList);
	public String updatePOInSAP(EzcPoWeekPlan poWeekPlan, String plant, String monthYr);

	public String updateVendorWeeklyPlanAll(PrdPlanFormDetInput prdPlanFormDetInput);
}
