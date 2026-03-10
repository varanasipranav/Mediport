    /*
    package com.ezc.aragenPR.webapp.persistance.dao;

    import java.util.List;

    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Modifying;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;

    //import com.ezc.aragenPR.webapp.model.EzWorkFlowMapping;
    //import com.ezc.aragenPR.webapp.model.EzWorkFlowMappingKey;

    public interface WorkflowMapRepository extends JpaRepository<EzWorkFlowMapping,EzWorkFlowMappingKey> {

        @Modifying
        @Query(value="delete from ezc_workflow_mapping where EWM_WF_KEY=:wfKey",nativeQuery = true)
        void deleteWFMapping(@Param("wfKey") Integer wfKey);

        @Query(value = "select USER_ID,FIRST_NAME,LAST_NAME,EMAIL from ezc_users where user_id in (select ewm_user_id from ezc_workflow_mapping where EWM_WF_KEY=:wfkey and EWM_LEVEL=:wflevel)",nativeQuery = true)
        List<Object[]> getWFUserByKey(@Param("wfkey") Integer wfkey,@Param("wflevel")Integer wflevel);

        @Query(value = "SELECT A.EWM_WF_KEY,A.EWM_LEVEL,B.USER_ID,B.FIRST_NAME,B.LAST_NAME FROM EZC_WORKFLOW_MAPPING A LEFT JOIN EZC_USERS B ON A.EWM_USER_ID=B.USER_ID",nativeQuery = true)
        List<Object[]> getWFUserList();

    }

    */
