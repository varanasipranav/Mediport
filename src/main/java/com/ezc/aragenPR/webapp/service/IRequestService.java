package com.ezc.aragenPR.webapp.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ezc.aragenPR.webapp.dto.ListSelector;
import com.ezc.aragenPR.webapp.dto.PriceFormDetInput;
import com.ezc.aragenPR.webapp.dto.PriceFormViewDet;
import com.ezc.aragenPR.webapp.model.EzComments;
import com.ezc.aragenPR.webapp.model.EzMaterialLimits;
import com.ezc.aragenPR.webapp.model.EzPriceRequestHeader;

public interface IRequestService {

	public List<EzPriceRequestHeader> createRequest(PriceFormDetInput priceFormDetInput);
	public List<EzPriceRequestHeader> getRequestList(ListSelector listSelector);
	public EzPriceRequestHeader getRequestById(String docId);
	public List<EzComments> getRequestComments(String docId);
	//public EzPriceRequestHeader submitRequest(PriceFormViewDet priceFormViewDet, String status);
	//public List<EzMaterialLimits> findReqMatLimitsByMaterial(List<String> matList);
	//public void autoApproveCheck(String docId);
	public List<Object[]> getRequestReportByUser(ArrayList<String> userIdList,Date fromDate,Date toDate);
	public List<Object[]> getRequestReport(Date fromDate, Date toDate);
	public List<Object[]> getRequestReportByWFUser(ArrayList<String> userList, Date fromDate, Date toDate);
	
}
