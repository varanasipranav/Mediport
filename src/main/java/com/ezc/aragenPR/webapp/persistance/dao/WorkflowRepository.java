/*
package com.ezc.aragenPR.webapp.persistance.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

//import com.ezc.aragenPR.webapp.model.EzWorkFlowKey;

public interface WorkflowRepository extends JpaRepository<EzWorkFlowKey,Integer> {
	
	
	
	@Query(value = "select a.EWK_COND_TYPE,a.EWK_SALES_ORG from ezc_workflow_key a, ezc_workflow_mapping b where a.EWK_KEY=b.EWM_WF_KEY and b.EWM_USER_ID=:username",nativeQuery = true)
    List<Object[]> getUserWFDefaults(@Param("username") String username);
    @Query("select a from EzWorkFlowKey a where a.salesOrg=:salesOrg and a.distChnl=:distChnl and a.mainChnl=:mainChnl and a.subChnl=:subChnl and a.condType=:condType and a.division=:division")
    List<EzWorkFlowKey> searchWFKey(String salesOrg,String distChnl,String mainChnl,String subChnl,String condType,String division);
    @Query("select a from EzWorkFlowKey a where a.wfKey in (select b.ezWorkFlowKey.wfKey from EzWorkFlowMapping b where b.userId=:creator)")
    List<EzWorkFlowKey> searchWFKeyByCreator(String creator);
    @Query(value = "select a.EWK_KEY,a.EWK_SALES_ORG,a.EWK_DIST_CHNL,a.EWK_MAIN_CHNL,a.EWK_SUB_CHNL,a.EWK_DIVISION,a.EWK_COND_TYPE,a.EWK_BRAND,b.EWM_LEVEL,b.EWM_USER_ID,c.FIRST_NAME from ezc_workflow_key a, ezc_workflow_mapping b,ezc_users c where a.EWK_KEY=b.EWM_WF_KEY and b.EWM_USER_ID=c.USER_ID",nativeQuery = true)
    List<Object[]> getWorkFlowDetails();
	
}
*/
