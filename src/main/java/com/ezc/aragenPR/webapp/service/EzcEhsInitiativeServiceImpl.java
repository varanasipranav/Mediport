/*
package com.ezc.aragenPR.webapp.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezc.aragenPR.webapp.dto.EzcEhsInitiativeDTO;
import com.ezc.aragenPR.webapp.model.EzUploadDocument;
import com.ezc.aragenPR.webapp.model.EzcEhsHeader;
import com.ezc.aragenPR.webapp.model.EzcEhsInitiative;
import com.ezc.aragenPR.webapp.persistance.dao.EzUploadDocumentRepo;
import com.ezc.aragenPR.webapp.persistance.dao.EzcEhsHeaderRepository;
import com.ezc.aragenPR.webapp.persistance.dao.EzcEhsInitiativeRepository;

import jakarta.transaction.Transactional;

@Service  
@Transactional
public class EzcEhsInitiativeServiceImpl implements EzcEhsInitiativeService {
	
	   @Autowired
	    private EzcEhsInitiativeRepository initiativeRepository;

	    @Autowired
	    private EzcEhsHeaderRepository headerRepository;
	    
	    @Autowired
	    private EzUploadDocumentRepo ezUploadDocumentRepo;

	@Override
	public void saveInitiatives(List<EzcEhsInitiativeDTO> initiativeDTOList) {
           
		List<EzcEhsInitiative> initiatives = initiativeDTOList.stream()
		        .filter(dto -> dto.getInitiative() != null && !dto.getInitiative().trim().isEmpty())
		        .map(dto -> {
		            EzcEhsInitiative initiative = new EzcEhsInitiative();
		            initiative.setInitiative(dto.getInitiative());

		            EzcEhsHeader header = headerRepository.findById(dto.getDocId())
		                .orElseThrow(() -> new RuntimeException("Header not found with docId: " + dto.getDocId()));
		            initiative.setHeader(header);

		            return initiative;
		        })
		        .collect(Collectors.toList());

		    if (!initiatives.isEmpty()) {
		        initiativeRepository.saveAll(initiatives);
		    }
	  }
	
	@Transactional
	public void saveOrUpdateInitiatives(List<EzcEhsInitiativeDTO> initiativeDTOs) {
	    for (EzcEhsInitiativeDTO dto : initiativeDTOs) {
	        EzcEhsInitiative initiative;

	        if (dto.getId() != null) {
	            // Check if already exists
	            Optional<EzcEhsInitiative> existingOpt = initiativeRepository.findById(dto.getId());

	            if (existingOpt.isPresent()) {
	                initiative = existingOpt.get();
	                // Only update if changed
	                if (!initiative.getInitiative().equals(dto.getInitiative())) {
	                    initiative.setInitiative(dto.getInitiative());
	                    initiativeRepository.save(initiative);
	                }
	                continue; // skip saving unchanged rows
	            }
	        }

	        // New record
	        initiative = new EzcEhsInitiative();
	        initiative.setInitiative(dto.getInitiative());

	        EzcEhsHeader header = headerRepository.findById(dto.getDocId())
	            .orElseThrow(() -> new RuntimeException("Header not found with docId: " + dto.getDocId()));
	        initiative.setHeader(header);

	        initiativeRepository.save(initiative);
	    }
	}

	@Override
	public void deleteById(Integer id) {
		initiativeRepository.deleteById(id);
	}

	@Override
	public List<EzUploadDocument> findFileByDocType(Integer docNo, String docType) {
		// TODO Auto-generated method stub
		List<EzUploadDocument> ezUploadDocumentList= ezUploadDocumentRepo.findFileByDocType(docNo,docType);
		return ezUploadDocumentList;
	}


}

*/
