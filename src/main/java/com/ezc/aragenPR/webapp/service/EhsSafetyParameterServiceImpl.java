/*
package com.ezc.aragenPR.webapp.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezc.aragenPR.webapp.dto.EhsSafetyParameterDTO;
import com.ezc.aragenPR.webapp.dto.EzEhsSafetyMaterialDTO;
import com.ezc.aragenPR.webapp.model.EzEhsSafetyMaterial;
import com.ezc.aragenPR.webapp.model.EzEhsSafetyParameter;
import com.ezc.aragenPR.webapp.persistance.dao.EzEhsSafetyParameterRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

@Service
@Slf4j
public class EhsSafetyParameterServiceImpl implements EhsSafetyParameterService {

    @Autowired
    private EzEhsSafetyParameterRepository safetyParameterRepository;

    @Override
    @Transactional
    public void saveAll(List<EzEhsSafetyParameter> safetyParameters) {
        safetyParameterRepository.saveAll(safetyParameters);
        log.debug("Saving safety parameters for EEH_DOC_ID: ");

    }

//    @Override
//    public List<EhsSafetyParameterDTO> getParametersByDocId(Integer docId) {
//        List<EzEhsSafetyParameter> parameters = safetyParameterRepository.findAll();
//        
//        return parameters.stream().map(param -> {
//            EhsSafetyParameterDTO dto = new EhsSafetyParameterDTO();
//            dto.setEESP_DOC_ID(param.getDocId());
//            dto.setEESP_VENDOR(param.getVendor());
//            dto.setEESP_MONTH_YR(param.getMonthYear());
//            dto.setEESP_KEY(param.getKey());
//            dto.setEESP_DESCRIPTION(param.getDescription());
//            dto.setEESP_VALUE(param.getValue());
//            return dto;
//        }).collect(Collectors.toList());
//    }
    
    
      
    @Override
    public List<EhsSafetyParameterDTO> getParametersByDocId(Integer docId) {
    	List<EzEhsSafetyParameter> parameters = safetyParameterRepository.findByHeaderDocId(docId);

    	 return parameters.stream().map(param -> {
    		 EhsSafetyParameterDTO dto = new EhsSafetyParameterDTO();
    		 //dto.setId(param.getId());  // Correct way to set the safety parameter's primary key
    		 dto.setDocId(param.getHeader().getDocId());
    		 dto.setKey(param.getDescription());  // Ensure it's a String
    		 dto.setValue(param.getValue());

    		 if (param.getSafetyMaterial() != null) {
    		     EzEhsSafetyMaterialDTO materialDto = new EzEhsSafetyMaterialDTO();
    		     materialDto.setKey(param.getSafetyMaterial().getKey());  // Assuming it's an Integer
    		     dto.setMaterial(materialDto);  
    		 }


    	        return dto;
    	    }).collect(Collectors.toList());
    }

	@Override
	public List<EzEhsSafetyParameter> findByDocId(Integer docId) {
		 return safetyParameterRepository.findByHeaderDocId(docId);
	}
}
*/
