/*
package com.ezc.aragenPR.webapp.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezc.aragenPR.webapp.dto.EzcEhsTrainingRecordDto;
import com.ezc.aragenPR.webapp.model.EzUploadDocument;
import com.ezc.aragenPR.webapp.model.EzcEhsHeader;
import com.ezc.aragenPR.webapp.model.EzcEhsTrainingRecord;
import com.ezc.aragenPR.webapp.persistance.dao.EzUploadDocumentRepo;
import com.ezc.aragenPR.webapp.persistance.dao.EzcEhsHeaderRepository;
import com.ezc.aragenPR.webapp.persistance.dao.EzcEhsTrainingRecordRepository;

import jakarta.transaction.Transactional;


@Service
@Transactional
public class EzcEhsTrainingRecordServiceImpl implements EzcEhsTrainingRecordService {
	
	 @Autowired
	    private EzcEhsTrainingRecordRepository trainingRecordRepository;

	    @Autowired
	    private EzcEhsHeaderRepository headerRepository;

	    @Autowired
	    private EzUploadDocumentRepo ezUploadDocumentRepo;


	@Override
	public void saveTrainingRecords(List<EzcEhsTrainingRecordDto> trainingRecordDtos) {
		  List<EzcEhsTrainingRecord> records = trainingRecordDtos.stream()
		            .filter(dto -> dto.getTrainingName() != null && !dto.getTrainingName().trim().isEmpty())
		            .map(dto -> {
		                EzcEhsTrainingRecord record = new EzcEhsTrainingRecord();
		                record.setTrainingName(dto.getTrainingName());
		                record.setTrainer(dto.getTrainer());
		                record.setNoOfParticipants(dto.getNoOfParticipants());
		                record.setNoOfHours(dto.getNoOfHours());
		                record.setTotalHours(dto.getTotalHours());
		                record.setTrainingDate(dto.getTrainingDate());

		                EzcEhsHeader header = headerRepository.findById(dto.getDocId())
		                    .orElseThrow(() -> new RuntimeException("Header not found with docId: " + dto.getDocId()));
		                record.setHeader(header);

		                return record;
		            })
		            .collect(Collectors.toList());

		        if (!records.isEmpty()) {
		            trainingRecordRepository.saveAll(records);
		        }
		
	}

	
	@Transactional
	public void saveOrUpdateTrainingRecords(List<EzcEhsTrainingRecordDto> trainingRecordDtos) {
	    for (EzcEhsTrainingRecordDto dto : trainingRecordDtos) {
	        EzcEhsTrainingRecord record;

	        if (dto.getId() != null) {
	            Optional<EzcEhsTrainingRecord> existingOpt = trainingRecordRepository.findById(dto.getId());

	            if (existingOpt.isPresent()) {
	                record = existingOpt.get();

	                boolean isChanged =
	                        !Objects.equals(record.getTrainingName(), dto.getTrainingName()) ||
	                        !Objects.equals(record.getTrainer(), dto.getTrainer()) ||
	                        !Objects.equals(record.getNoOfParticipants(), dto.getNoOfParticipants()) ||
	                        !Objects.equals(record.getNoOfHours(), dto.getNoOfHours()) ||
	                        !Objects.equals(record.getTotalHours(), dto.getTotalHours())||
                    		!Objects.equals(record.getTrainingDate(), dto.getTrainingDate());

	                if (isChanged) {
	                    record.setTrainingName(dto.getTrainingName());
	                    record.setTrainer(dto.getTrainer());
	                    record.setNoOfParticipants(dto.getNoOfParticipants());
	                    record.setNoOfHours(dto.getNoOfHours());
	                    record.setTotalHours(dto.getTotalHours());
	                    record.setTrainingDate(dto.getTrainingDate());
	                    trainingRecordRepository.save(record);
	                }

	                continue; // Skip re-saving unchanged row
	            }
	        }

	        // New record
	        record = new EzcEhsTrainingRecord();
	        record.setTrainingName(dto.getTrainingName());
	        record.setTrainer(dto.getTrainer());
	        record.setNoOfParticipants(dto.getNoOfParticipants());
	        record.setNoOfHours(dto.getNoOfHours());
	        record.setTotalHours(dto.getTotalHours());
	        record.setTrainingDate(dto.getTrainingDate());

	        EzcEhsHeader header = headerRepository.findById(dto.getDocId())
	            .orElseThrow(() -> new RuntimeException("Header not found with docId: " + dto.getDocId()));
	        record.setHeader(header);

	        trainingRecordRepository.save(record);
	    }
	}

	@Override
	public void deleteById(Integer id) {
		  trainingRecordRepository.deleteById(id);
		
	}

	@Override
	public List<EzUploadDocument> findFileByDocType(Integer docId, String docType) {
		 return ezUploadDocumentRepo.findFileByDocType(docId, docType);
	}


}
*/
