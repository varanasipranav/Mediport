package com.ezc.aragenPR.webapp.service;

import java.util.List;

import com.ezc.aragenPR.webapp.model.EzEhsEnvironmentParameter;

public interface EzEhsEnvironmentParameterService {
	 List<EzEhsEnvironmentParameter> findByDocId(int docId);
	    void saveAll(List<EzEhsEnvironmentParameter> parameters);
}
