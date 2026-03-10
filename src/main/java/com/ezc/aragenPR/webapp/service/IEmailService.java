package com.ezc.aragenPR.webapp.service;



import java.util.List;

import com.ezc.aragenPR.webapp.model.EzCustomerFormHeader;
import com.ezc.aragenPR.webapp.model.EzLinkController;
import com.ezc.aragenPR.webapp.model.EzPriceRequestHeader;

public interface IEmailService {
	public void sendSimpleMessage(String to, String subject, String text);
	public void sendRequestMailToNextPart(EzCustomerFormHeader custFormHeader,List<String> emailList);
	public EzLinkController saveLink(EzLinkController linkController);
	public EzLinkController getDetailsById(Integer id);
}
