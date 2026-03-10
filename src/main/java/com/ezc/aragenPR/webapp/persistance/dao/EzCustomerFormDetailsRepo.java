package com.ezc.aragenPR.webapp.persistance.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ezc.aragenPR.webapp.model.EzCustomerFormDetails;
import com.ezc.aragenPR.webapp.model.EzCustomerFormDetailsKey;


public interface EzCustomerFormDetailsRepo extends JpaRepository<EzCustomerFormDetails, EzCustomerFormDetailsKey> {
	
	@Query(nativeQuery=true,value="select * from ezc_customer_form_details where ECFD_DOC_ID=:docId and ECFD_FIELD=:type ")
	List<EzCustomerFormDetails> findAllByIdAndType(Integer docId,String type);

	
	@Query(nativeQuery=true,value="select email from ezc_users where user_id in (select ECFD_VALUE from ezc_customer_form_details where ECFD_DOC_ID=:docId and ECFD_FIELD=:type and ECFD_VALUE not in ('OS_CQA','OS_HOS'))")
	List<String> findPrevApprMail(Integer docId,String type); 
}
