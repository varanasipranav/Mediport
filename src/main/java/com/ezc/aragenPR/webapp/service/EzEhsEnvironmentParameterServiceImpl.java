package com.ezc.aragenPR.webapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezc.aragenPR.webapp.model.EzEhsEnvironmentParameter;
import com.ezc.aragenPR.webapp.persistance.dao.EzEhsEnvironmentParameterRepository;

@Service
public class EzEhsEnvironmentParameterServiceImpl implements EzEhsEnvironmentParameterService {

	private final EzEhsEnvironmentParameterRepository parameterRepository;

    @Autowired
    public EzEhsEnvironmentParameterServiceImpl(EzEhsEnvironmentParameterRepository parameterRepository) {
        this.parameterRepository = parameterRepository;
    }

    @Override
    public List<EzEhsEnvironmentParameter> findByDocId(int docId) {
        return parameterRepository.findByHeaderDocId(docId);
    }

    @Override
    public void saveAll(List<EzEhsEnvironmentParameter> parameters) {
        parameterRepository.saveAll(parameters);
    }

}
