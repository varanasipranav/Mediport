package com.ezc.aragenPR.webapp.service;

import java.util.List;

import com.ezc.aragenPR.webapp.dto.ListSelector;
import com.ezc.aragenPR.webapp.model.EzPurchaseOrder;


public interface IPurchaseOrder {
	public EzPurchaseOrder getPoNo(String id);
	public void synchPO(List<EzPurchaseOrder> poList);
//	List<EzPurchaseOrder> getAllPurchaseOrders();
	List<EzPurchaseOrder> getAllPurchaseOrdersForVendor(String loggedUser);
	List<EzPurchaseOrder> getAllPurchaseOrders(ListSelector listSelector);
}

// Created interface here check implementation in PurchaseOrderImpl.jaa file
