package com.ezc.aragenPR.webapp.service;

import java.util.List;

import com.ezc.aragenPR.webapp.model.EzEhsSustainabilityParameter;

public interface EzEhsSustainabilityParameterService {
	   List<EzEhsSustainabilityParameter> findByDocId(int docId);
	    void saveAll(List<EzEhsSustainabilityParameter> parameters);
}
