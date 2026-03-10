/*
package com.ezc.aragenPR.webapp.service;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezc.aragenPR.webapp.model.EzConditionSeqMap;
import com.ezc.aragenPR.webapp.model.EzPriceSeqMaster;
import com.ezc.aragenPR.webapp.persistance.dao.ConditionSeqMapRepo;
import com.ezc.aragenPR.webapp.persistance.dao.PriceSeqMasterRepository;
import com.ezc.aragenPR.webapp.persistance.dao.WorkflowRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class PriceReqService implements IPriceReqService {

	
	@Autowired
	PriceSeqMasterRepository priceSeqMasterRepo;

	@Autowired
	WorkflowRepository wfRepo;
	
	@Autowired
	ConditionSeqMapRepo condSeqRepo;
	
	@Override
	public List<EzPriceSeqMaster> getPriceSeqMaster() {
		return priceSeqMasterRepo.findAll();
	}

	@Override
	public EzPriceSeqMaster getPriceSeqMasterById(int id) {
		
		Optional<EzPriceSeqMaster> ezPriceSeqMasterOpt= priceSeqMasterRepo.findById(id);
		if(ezPriceSeqMasterOpt.isPresent())
			return ezPriceSeqMasterOpt.get();
		else
			return null;
	}

	@Override
	public List<Object[]> getUserWFDefaults(String username) {
		return wfRepo.getUserWFDefaults(username);
	}

	@Override
	public List<EzPriceSeqMaster> getPriceSeqByCondType(String condType,String userId) {
		return priceSeqMasterRepo.getByCondType(condType,userId);
	}

	@Override
	public List<EzConditionSeqMap> getUserCondTypes(String userId) {
		return condSeqRepo.findByUserId(userId);
	}

		
		
	

}
*/
