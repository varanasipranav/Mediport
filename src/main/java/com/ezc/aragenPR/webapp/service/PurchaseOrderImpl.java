package com.ezc.aragenPR.webapp.service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezc.aragenPR.webapp.dto.ListSelector;
import com.ezc.aragenPR.webapp.model.EzPurchaseOrder;
import com.ezc.aragenPR.webapp.persistance.dao.PurchaseOrderRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class PurchaseOrderImpl implements IPurchaseOrder {

	@Autowired
	private PurchaseOrderRepository purchaseOrderRepository;
	
	@Override
	public List<EzPurchaseOrder> getAllPurchaseOrdersForVendor(String loggedUser) {
		if(loggedUser == "CPTTEAM") {
		return purchaseOrderRepository.findAll();
		}
		return purchaseOrderRepository.findByUserId(loggedUser);
	}
	
	@Override
	public List<EzPurchaseOrder> getAllPurchaseOrders(ListSelector listSelector) {
		log.debug("listSelector.getUser() => " + listSelector.getUser());
		if(listSelector.getUser() == null)
			return purchaseOrderRepository.findByDocDateGreaterThanEqualAndDocDateLessThanEqual(listSelector.getFromDate(), listSelector.getToDate());
		else
			return purchaseOrderRepository.findByUserIdAndDocDateGreaterThanEqualAndDocDateLessThanEqual(listSelector.getUser().get(0),listSelector.getFromDate(), listSelector.getToDate());
	}


	@Override
	public EzPurchaseOrder getPoNo(String id) {
		// TODO Auto-generated method stub
		System.out.println("id => " + id);
		Optional<EzPurchaseOrder> optional = purchaseOrderRepository.findById(id);
		EzPurchaseOrder ezPurchaseOrder = null;
		if(optional.isPresent()) {
			ezPurchaseOrder = optional.get();
		}
		else {
			throw new RuntimeException("PO not found with id:" + id);
		}
		return ezPurchaseOrder;
	}


	@Override
	public void synchPO(List<EzPurchaseOrder> poList) {
		purchaseOrderRepository.saveAll(poList);
		
	}


}

// Implementation of service logic will come here.
