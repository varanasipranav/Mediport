package com.ezc.aragenPR.webapp.job;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EzProductSynchJob {

	@Autowired
//	 private com.ezc.outsrc.webapp.sap.IMasterService iSAPMasterService;
	
	private static final Logger log = LoggerFactory.getLogger(EzProductSynchJob.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Scheduled(cron = "${product.cron.expression}")
	public void reportCurrentTime() {
		log.info("Product synch job start::", dateFormat.format(new Date()));
		//iSAPMasterService.synchMaterials();
		log.info("Product synch job end::", dateFormat.format(new Date()));
	}
}
