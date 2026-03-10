package com.ezc.aragenPR.webapp.persistance.dao;


import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.ezc.aragenPR.webapp.dto.ListSelector;
import com.ezc.aragenPR.webapp.model.EzPriceRequestHeader;
//import com.ezc.aragenPR.webapp.model.EzWorkFlowMapping;

import lombok.extern.slf4j.Slf4j;
 
@Repository
@Slf4j
public class RequestCustomDto {

	@PersistenceContext
	EntityManager em;
	//findByErhReqTypeAndErhStatusAndErhRequestedOnLessThanEqualAndErhRequestedOnGreaterThanEqual
	    public List<EzPriceRequestHeader> findRequestList(ListSelector listSelector) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EzPriceRequestHeader> cq = cb.createQuery(EzPriceRequestHeader.class);
        Root<EzPriceRequestHeader> header = cq.from(EzPriceRequestHeader.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        if(listSelector.getNextPart() != null && !"null".equals(listSelector.getNextPart()) && !"".equals(listSelector.getNextPart()))
        	predicates.add(cb.equal(header.get("nextPart"), listSelector.getType()));
        if(listSelector.getFromDate() != null && listSelector.getToDate() != null)
        	predicates.add(cb.between(header.get("createdOn"), listSelector.getFromDate(), listSelector.getToDate()));
        if(listSelector.getStatus() != null && !"null".equals(listSelector.getStatus()) && !"".equals(listSelector.getStatus()))
        {
        	predicates.add(cb.equal(header.get("status"), listSelector.getStatus()));
        }
        
       /* if("ROLE_APR".equals(listSelector.getRole()))
    	{
    		Root<EzWorkFlowMapping> wfMapping = cq.from(EzWorkFlowMapping.class);        		
    		predicates.add(cb.equal(wfMapping.get("ezWorkFlowKey"), header.get("wfKey")));
    		predicates.add(cb.equal(wfMapping.get("level"), header.get("wfLevel")));
    		if("S".equals(listSelector.getStatus()))
    		{
    			predicates.add(cb.equal(wfMapping.get("userId"),listSelector.getWfUser()));
    		}
    	}*/
        
        if(listSelector.getUser() != null && listSelector.getUser().size() > 0)
        {
        	Expression<String> exp = header.get("createdBy");
        	predicates.add(exp.in(listSelector.getUser()));
        }
        cq.select(header).where(predicates.toArray(new Predicate[0]));
        TypedQuery<EzPriceRequestHeader> query = em.createQuery(cq);
        return query.getResultList();
    }
	    
	    	    
}
