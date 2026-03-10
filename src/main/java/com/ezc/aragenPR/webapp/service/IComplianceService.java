package com.ezc.aragenPR.webapp.service;

import java.util.Date;
import java.util.List;

//import com.ezc.aragenPR.webapp.dto.EzcEhsFormFieldMasterDto;
//import com.ezc.aragenPR.webapp.dto.EzcEhsVendorFormConfigDto;
import com.ezc.aragenPR.webapp.model.EzComments;
import com.ezc.aragenPR.webapp.model.EzEnvironmentReport;

public interface IComplianceService {
  //  List<EzcEhsFormFieldMasterDto> getFormFieldsByVendorId(String vendorId);
    
    EzEnvironmentReport saveFieldEntry(Long docId, Long vendorId, Long fieldId, String actual,
            String remarks, String status, String reportMonth, String submissionStatus, String FileName);
    
    List<EzEnvironmentReport> getSavedReports(Long docId, String vendorId, String reportMonth);
    
      //  List<EzcEhsVendorFormConfigDto> getAllVendorConfigs();
        
        //List<EzcEhsVendorFormConfigDto> getVendorConfigsByVendorId(String vendorId);

        void saveComments(String comments, Integer docId, String userId, String tab);

}
