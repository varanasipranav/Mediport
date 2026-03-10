/*
package com.ezc.aragenPR.webapp.persistance.dao;


import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.ezc.aragenPR.webapp.dto.WFInput;
import com.ezc.aragenPR.webapp.model.EzWorkFlowKey;

import lombok.extern.slf4j.Slf4j;
 
@Repository
@Slf4j
public class WorkflowKeyCustomDto {

	@PersistenceContext
	EntityManager em;
	//findByErhReqTypeAndErhStatusAndErhRequestedOnLessThanEqualAndErhRequestedOnGreaterThanEqual
	    public List<EzWorkFlowKey> searchWFKey(WFInput wfInput) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EzWorkFlowKey> cq = cb.createQuery(EzWorkFlowKey.class);
        Root<EzWorkFlowKey> wfkey = cq.from(EzWorkFlowKey.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
       
        if(wfInput.getSelSalesOrg() != null && !"null".equals(wfInput.getSelSalesOrg()) && !"".equals(wfInput.getSelSalesOrg()))
        	predicates.add(cb.equal(wfkey.get("salesOrg"), wfInput.getSelSalesOrg()));
        else
        	predicates.add(cb.equal(wfkey.get("salesOrg"), ""));
        if(wfInput.getSelDistChnl() != null && !"null".equals(wfInput.getSelDistChnl()) && !"".equals(wfInput.getSelDistChnl()))
        	predicates.add(cb.equal(wfkey.get("distChnl"), wfInput.getSelDistChnl()));
        else	
        	predicates.add(cb.equal(wfkey.get("distChnl"), ""));
        if(wfInput.getSelDivision() != null && !"null".equals(wfInput.getSelDivision()) && !"".equals(wfInput.getSelDivision()))
        	predicates.add(cb.equal(wfkey.get("division"), wfInput.getSelDivision()));
        else
        	predicates.add(cb.equal(wfkey.get("division"), ""));
        if(wfInput.getSelMainChnl() != null && !"null".equals(wfInput.getSelMainChnl()) && !"".equals(wfInput.getSelMainChnl()))
        	predicates.add(cb.equal(wfkey.get("mainChnl"), wfInput.getSelMainChnl()));
        else
        	predicates.add(cb.equal(wfkey.get("mainChnl"), ""));
        if(wfInput.getSelSubChnl() != null && !"null".equals(wfInput.getSelSubChnl()) && !"".equals(wfInput.getSelSubChnl()))
        	predicates.add(cb.equal(wfkey.get("subChnl"), wfInput.getSelSubChnl()));
        else
        	predicates.add(cb.equal(wfkey.get("subChnl"), ""));
        if(wfInput.getSelCondTypes() != null && !"null".equals(wfInput.getSelCondTypes()) && !"".equals(wfInput.getSelCondTypes()))
        	predicates.add(cb.equal(wfkey.get("condType"), wfInput.getSelCondTypes()));
        else
        	predicates.add(cb.equal(wfkey.get("condType"), ""));
        if(wfInput.getSelBrand() != null && !"null".equals(wfInput.getSelBrand()) && !"".equals(wfInput.getSelBrand()))
        	predicates.add(cb.equal(wfkey.get("brand"), wfInput.getSelBrand()));
        else
        	predicates.add(cb.equal(wfkey.get("brand"), ""));
        
//        if(wfInput.getUser() != null && wfInput.getUser().size() > 0)
//        {
//        	Expression<String> exp = wfkey.get("createdBy");
//        	predicates.add(exp.in(wfInput.getUser()));
//        }
        cq.where(predicates.toArray(new Predicate[0]));
        TypedQuery<EzWorkFlowKey> query = em.createQuery(cq);
        return query.getResultList();
    }
	    
	    	    
}
*/
