package com.ezc.aragenPR.webapp.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import com.ezc.aragenPR.webapp.dto.ListSelector;
import com.ezc.aragenPR.webapp.dto.WeeklyPlanAmmendItem;
import com.ezc.aragenPR.webapp.dto.WeeklyPlanFormDetInput;
import com.ezc.aragenPR.webapp.dto.WeeklyPlanItem;
import com.ezc.aragenPR.webapp.model.EzcPoPlanHeader;
import com.ezc.aragenPR.webapp.model.EzcPoWeekPlan;
import com.ezc.aragenPR.webapp.model.PrdPlanFormDetInput;

public interface IPrdPlanService {

	
	public EzcPoPlanHeader createPlan(PrdPlanFormDetInput prdPlanFormDetInput);
	public List<EzcPoPlanHeader> getPlanList(ListSelector listSelector);
	public List<EzcPoPlanHeader> getFilteredPlanList(ListSelector listSelector);
	public PrdPlanFormDetInput getPlanById(String docId);
	public String createPO(String planId);
	public String submitRequest(PrdPlanFormDetInput prdPlanFormDetInput);
	public String approveRequest(PrdPlanFormDetInput prdPlanFormDetInput);
	public List<WeeklyPlanItem> getWeeklyPlanList(ListSelector listSelector);
	public WeeklyPlanFormDetInput getWeekPlanById(String planId,int weekId);
	public HashMap<String,String> getMaterialNetWt(ArrayList<String> matList,String plant,Date date);
	PrdPlanFormDetInput getWekklyPlanAllById(String docId, int weekId);
	EzcPoWeekPlan getWeekPlanHdrById(String planId, int weekId);
	String updateWeeklyPlanAll(PrdPlanFormDetInput prdPlanFormDetInput, String btnType);
	List<Object[]> getOtherPlantPlans(String planId);
	List<EzcPoPlanHeader> getSubmittedPlansByRoleAndDateRange(String role, Date fromDate, Date toDate);
	List<EzcPoPlanHeader> getSubmittedPlansByRoleAndDateRangeAndUserId(
			String role, String userId, Date fromDate, Date toDate
	);


}
