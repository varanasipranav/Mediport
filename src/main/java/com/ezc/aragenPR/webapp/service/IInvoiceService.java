package com.ezc.aragenPR.webapp.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

//import com.ezc.aragenPR.webapp.dto.EzAccountStatementDto;
import com.ezc.aragenPR.webapp.dto.EzInvoiceReportDto;
import com.ezc.aragenPR.webapp.dto.ListSelector;
import com.ezc.aragenPR.webapp.dto.SAPResponse;
import com.ezc.aragenPR.webapp.model.EzInvoiceHeader;
import com.ezc.aragenPR.webapp.model.EzInvoiceInputDto;
import com.ezc.aragenPR.webapp.model.EzInvoiceItems;
import com.ezc.aragenPR.webapp.model.EzInvoiceItemsDto;

public interface IInvoiceService {
	List<EzInvoiceHeader> getInvoiceHeader(ListSelector listSelector);
	public void setInvoiceItems(List<EzInvoiceItems> itemsList);
	public void setInvoiceHeaders(EzInvoiceHeader tempInvoiceHeader);
	public void saveInvoiceData(EzInvoiceInputDto ezInvoiceInputDto);
	EzInvoiceHeader getInvoiceHeaderById(Long docNo);
	public SAPResponse postMIRO(Long docNo);
	public List<String> getAllInvoiceNos();
	public List<EzInvoiceReportDto> getInvoiceReports(String vendor);
//	public List<EzAccountStatementDto> getAccountStatements(ListSelector listSelector);
	public HashMap<String, String> getMaterialNetWt(ArrayList<String> itemsList, String plant, Date date);
	public void reverseFifoOnRejection(Long docId);
//	public void sendInvoiceMail(String subject, String toEmail, EzInvoiceHeader invoiceHeader);
    public void sendInvoiceMail(String subject, String fromMail,  String[] toEmails, String recipientName, Long docNo, String messageBody, String actionLabel);

}