package com.ezc.aragenPR.webapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezc.aragenPR.webapp.model.EzEhsSustainabilityParameter;
import com.ezc.aragenPR.webapp.persistance.dao.EzEhsSustainabilityParameterRepository;

@Service
public class EzEhsSustainabilityParameterServiceImpl implements EzEhsSustainabilityParameterService {

    @Autowired
    private EzEhsSustainabilityParameterRepository repository;

    @Override
    public List<EzEhsSustainabilityParameter> findByDocId(int docId) {
        return repository.findByHeaderDocId(docId);
    }

    @Override
    public void saveAll(List<EzEhsSustainabilityParameter> parameters) {
        repository.saveAll(parameters);
    }

	
}

