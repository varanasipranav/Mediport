/*
package com.ezc.aragenPR.webapp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.catalina.User;
import org.apache.xmlbeans.impl.piccolo.util.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.ezc.aragenPR.webapp.persistance.dao.EzPoItemsRepo;
import com.ezc.aragenPR.webapp.dto.EmailFormat;
//import com.ezc.aragenPR.webapp.dto.EzAccountStatementDto;
import com.ezc.aragenPR.webapp.dto.EzInvoiceReportDto;
import com.ezc.aragenPR.webapp.dto.ListSelector;
import com.ezc.aragenPR.webapp.dto.SAPResponse;
import com.ezc.aragenPR.webapp.model.EzEHSIncident;
import com.ezc.aragenPR.webapp.model.EzInvoiceHeader;
import com.ezc.aragenPR.webapp.model.EzInvoiceInputDto;
import com.ezc.aragenPR.webapp.model.EzInvoiceItems;
import com.ezc.aragenPR.webapp.model.EzInvoiceItemsCompKey;
import com.ezc.aragenPR.webapp.model.EzInvoiceItemsDto;
import com.ezc.aragenPR.webapp.model.EzInvoiceReport;
import com.ezc.aragenPR.webapp.model.EzPoItems;
import com.ezc.aragenPR.webapp.model.EzPoItemsKey;
import com.ezc.aragenPR.webapp.model.EzValueMapping;
import com.ezc.aragenPR.webapp.model.Users;
import com.ezc.aragenPR.webapp.persistance.dao.EzPoItemsRepo;
import com.ezc.aragenPR.webapp.persistance.dao.InvoiceHeaderRepository;
import com.ezc.aragenPR.webapp.persistance.dao.InvoiceItemsRepository;
import com.ezc.aragenPR.webapp.persistance.dao.UserRepository;
import com.ezc.aragenPR.webapp.wsdl.invoice.create.gen.BAPIRET2;
import com.ezc.aragenPR.webapp.wsdl.invoice.create.gen.BAPI_INCINV_CREATE_ADDRESSDATA;
import com.ezc.aragenPR.webapp.wsdl.invoice.create.gen.BAPI_INCINV_CREATE_GL_ACCOUNT;
import com.ezc.aragenPR.webapp.wsdl.invoice.create.gen.BAPI_INCINV_CREATE_HEADER;
import com.ezc.aragenPR.webapp.wsdl.invoice.create.gen.BAPI_INCINV_CREATE_ITEM;
import com.ezc.aragenPR.webapp.wsdl.invoice.create.gen.BAPI_INCOMINGINVOICE_PARK;
import com.ezc.aragenPR.webapp.wsdl.invoice.create.gen.BAPI_INCOMINGINVOICE_PARKResponse;
import com.ezc.aragenPR.webapp.wsdl.invoice.report.gen.BAPIRETURN;
import com.ezc.aragenPR.webapp.wsdl.invoice.report.gen.ZBAPI3008_2;
import com.ezc.aragenPR.webapp.wsdl.invoice.report.gen.Z_EZ_GET_VENDOR_INVOICE_LIST;
import com.ezc.aragenPR.webapp.wsdl.invoice.report.gen.Z_EZ_GET_VENDOR_INVOICE_LISTResponse;
import com.ezc.aragenPR.webapp.service.RFCReadSAPService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Transactional
public class InvoiceServiceImpl implements IInvoiceService {
	@Autowired
	private InvoiceHeaderRepository invoiceHeaderRepository;

	@Autowired
	private InvoiceItemsRepository invoiceItemsRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MIROSAPCreateService miroCreateService;

	@Autowired
	private InvoiceReportSAPService invoiceReportSAPService;

	@Autowired
	EzPoItemsRepo ezPoItemsRepo;
	
	@Autowired
	IMasterService masterService;
	
    @Autowired 
    IUserService userService;
    
    @Autowired
	RFCReadSAPService rfcReadSAPService;
    
    @Autowired
	 private IEmailService iEmailService;
    
	@Autowired
	IStorageService storageService;
    
	@Override
	public List<EzInvoiceHeader> getInvoiceHeader(ListSelector listSelector) {
//		return invoiceHeaderRepository.findAll();
		
    	if((listSelector == null || listSelector.getFromDate() == null))
    	{
    		Date todayDate = new Date();
    		Calendar c = Calendar.getInstance(); 
    		c.setTime(todayDate); 
    		c.add(Calendar.MONTH, -3);
    		listSelector = new ListSelector();
    		listSelector.setFromDate(c.getTime());
    		listSelector.setToDate(todayDate);	
    	}
		
		if (listSelector.getVendor() == null)
			return invoiceHeaderRepository.findByInvoiceDateGreaterThanEqualAndInvoiceDateLessThanEqual(
					listSelector.getFromDate(), listSelector.getToDate());
		else
			return invoiceHeaderRepository.findByUserIdAndInvoiceDateGreaterThanEqualAndInvoiceDateLessThanEqual(
					listSelector.getUser().get(0), listSelector.getFromDate(), listSelector.getToDate());
	}

	@Override
	public void setInvoiceItems(List<EzInvoiceItems> itemsList) {
		// TODO Auto-generated method stub
		invoiceItemsRepository.saveAll(itemsList);
	}

	@Override
	public void setInvoiceHeaders(EzInvoiceHeader headerList) {
		invoiceHeaderRepository.save(headerList);
	}

	@Override
	@Transactional
	public void saveInvoiceData(EzInvoiceInputDto ezInvoiceInputDto) {
	    EzInvoiceHeader ezInvoiceHeader = new EzInvoiceHeader();

	    // ✅ Set invoice header details
	    ezInvoiceHeader.setInvoiceValue(ezInvoiceInputDto.getInvoiceValue());
	    ezInvoiceHeader.setInvoiceNo(ezInvoiceInputDto.getInvoiceNumber());
	    ezInvoiceHeader.setInvoiceDate(ezInvoiceInputDto.getInvoiceDate());
	    ezInvoiceHeader.setStartDate(ezInvoiceInputDto.getStartDate());
	    ezInvoiceHeader.setEndDate(ezInvoiceInputDto.getEndDate());
	    ezInvoiceHeader.setUserId(ezInvoiceInputDto.getVendor());
	    ezInvoiceHeader.setPlantNo(ezInvoiceInputDto.getInvoiceItems().get(0).getPlantNo());
	    ezInvoiceHeader.setUploadedInvoice(ezInvoiceInputDto.getUuid());
	    log.debug("ezInvoiceInputDto.getVendor() => " + ezInvoiceInputDto.getVendor());
	    ezInvoiceHeader.setPoNo(ezInvoiceInputDto.getPoNo());
		ezInvoiceHeader.setCreatedOn(new Date());
	    ezInvoiceHeader.setStatus("SUBMITTED");

	    // ✅ Save the EzInvoiceHeader FIRST to get a generated DOC_ID
	    ezInvoiceHeader = invoiceHeaderRepository.save(ezInvoiceHeader);

	    List<EzInvoiceItems> itemsList = new ArrayList<>();
	    Map<EzInvoiceItemsCompKey, EzInvoiceItems> uniqueItems = new HashMap<>(); // ✅ Track unique items

	    // 🔄 Start processing each invoice item
	    for (EzInvoiceItemsDto item : ezInvoiceInputDto.getInvoiceItems()) {
	        List<EzInvoiceItemsDto> allocatedItems = allocateQuantityFIFO(item.getQuantity(), item.getMaterial());

	        int lineNo = 1;  // 🔄 Reset lineNo for each material

	        for (EzInvoiceItemsDto allocatedItem : allocatedItems) {
	        	
	        	log.debug("Allocated Item -> Material: " + allocatedItem.getMaterial() + 
	                       ", Quantity: " + allocatedItem.getQuantity() + 
	                       ", SubmittedQty: " + item.getSubmittedQty());
	        	
	            EzInvoiceItemsCompKey key = new EzInvoiceItemsCompKey(
	                allocatedItem.getPoNo(), // ✅ Use PO number from allocation
	                allocatedItem.getPoItemNumber()
	            );

	            if (uniqueItems.containsKey(key)) {
	                throw new IllegalArgumentException("Duplicate invoice item detected for key: " + key);
	            }

	            EzInvoiceItems tempInvoiceItems = new EzInvoiceItems();
	            tempInvoiceItems.setId(key);
	            tempInvoiceItems.setEzInvoiceHeader(ezInvoiceHeader);  // ✅ Link to header
	            tempInvoiceItems.setMaterial(allocatedItem.getMaterial());
	            tempInvoiceItems.setMatDesc(item.getMatDesc());
	            tempInvoiceItems.setUom(item.getUom());
	            tempInvoiceItems.setTotalQty(allocatedItem.getTotalQty());
	            tempInvoiceItems.setQuantity(allocatedItem.getQuantity());
	            tempInvoiceItems.setPoItemNumber(allocatedItem.getPoItemNumber());
	            tempInvoiceItems.setSubmittedQty(item.getSubmittedQty());
	            tempInvoiceItems.setInvoiceQty(item.getInvoiceQty());
	            tempInvoiceItems.setInvoiceAmount(item.getInvoiceAmount());
	            tempInvoiceItems.setPricePerKg(item.getPricePerKg());
	            tempInvoiceItems.setNetWeight(item.getNetWeight());

	            // ✅ Calculate price, tax, and total value
	            BigDecimal unitPrice = item.getPrice();
	            BigDecimal quantity = allocatedItem.getQuantity();
	            BigDecimal newAmount = unitPrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
	            BigDecimal tax = newAmount.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
	            BigDecimal totalValue = newAmount.add(tax).setScale(2, RoundingMode.HALF_UP);

	            tempInvoiceItems.setValue(newAmount);
	            tempInvoiceItems.setTaxes(tax);
	            tempInvoiceItems.setTotalValue(totalValue);
	            tempInvoiceItems.setPrice(item.getPrice());

				*/
/*
				 * // ✅ Fetch the PO item and update `openQty` EzPoItems poItem =
				 * ezPoItemsRepo.findByPoNoAndMaterial( allocatedItem.getPoNo(),
				 * allocatedItem.getMaterial(), String.valueOf(allocatedItem.getPoItemNumber())
				 * );
				 * 
				 * if (poItem != null) { BigDecimal currentOpenQty = (poItem.getOpenQty() !=
				 * null) ? poItem.getOpenQty() : poItem.getTotalQty(); BigDecimal allocatedQty =
				 * allocatedItem.getQuantity();
				 * 
				 * // ✅ Subtract allocatedQty from openQty and ensure it doesn't go below 0
				 * BigDecimal newOpenQty =
				 * currentOpenQty.subtract(allocatedQty).max(BigDecimal.ZERO);
				 * poItem.setOpenQty(newOpenQty); poItem.setQuantity(quantity);
				 * poItem.setSubmittedQty(item.getSubmittedQty()); ezPoItemsRepo.save(poItem);
				 * // ✅ Save updated openQty }
				 *//*


	            // ✅ Track the item to prevent duplicates
	            uniqueItems.put(key, tempInvoiceItems);
	            itemsList.add(tempInvoiceItems);
	            lineNo++;  // ✅ Increment line number for each new allocation
	        }
	    }
	    

	    ezInvoiceHeader.setEzInvoiceItems(itemsList);
	    
	    invoiceHeaderRepository.save(ezInvoiceHeader); // ✅ Save header again to cascade items
	    
		storageService.saveFile(ezInvoiceInputDto.getUuid(), (int) ezInvoiceHeader.getDocNo(), ezInvoiceInputDto.getVendor(), "Invoice");
	}


	*/
/**
	 * Allocates quantity FIFO (First In, First Out).
	 *//*

	public List<EzInvoiceItemsDto> allocateQuantityFIFO(BigDecimal enteredQty, String materialCode) {
	    List<EzInvoiceItemsDto> allocatedItems = new ArrayList<>();

	    // ✅ Fetch all POs for the material, sorted FIFO
	    List<EzPoItems> poItems = ezPoItemsRepo.findByMaterialOrderByPoNoAsc(materialCode);

	    BigDecimal remainingQty = enteredQty;

	    for (EzPoItems poItem : poItems) {
	        if (remainingQty.compareTo(BigDecimal.ZERO) <= 0) break;

	        BigDecimal availableQty = (poItem.getOpenQty() != null) ? poItem.getOpenQty() : poItem.getTotalQty();
	        log.debug("availableQty => " +  availableQty);
	        if (availableQty.compareTo(BigDecimal.ZERO) > 0) {
	            BigDecimal allocatedQty = availableQty.min(remainingQty);
	            log.debug("allocatedQty => " +  allocatedQty);
	            EzInvoiceItemsDto dto = new EzInvoiceItemsDto();
	            dto.setPoNo(poItem.getId().getPoNo());
	            dto.setMaterial(poItem.getMaterial());
	            dto.setPoItemNumber(poItem.getId().getPoItemNumber());
	            dto.setQuantity(allocatedQty);
	            dto.setTotalQty(poItem.getTotalQty());

	            allocatedItems.add(dto);
	            remainingQty = remainingQty.subtract(allocatedQty);
	            
	            log.debug("remainingQty => " +  remainingQty);

	            // ✅ Update openQty in database immediately
	            BigDecimal newOpenQty = availableQty.subtract(allocatedQty).max(BigDecimal.ZERO);
	            log.debug("newOpenQty => " +  newOpenQty);
	            poItem.setOpenQty(newOpenQty);
	            ezPoItemsRepo.save(poItem); // ✅ Save updated openQty
	        }
	    }

	    if (remainingQty.compareTo(BigDecimal.ZERO) > 0) {
	        throw new IllegalArgumentException("Entered quantity exceeds available stock!");
	    }

	    return allocatedItems;
	}
	
	@Override
	@Transactional
	public void reverseFifoOnRejection(Long docId) {
	    // 1. Fetch the invoice header by DOC_ID
	    EzInvoiceHeader header = invoiceHeaderRepository.findById(docId)
	        .orElseThrow(() -> new IllegalArgumentException("Invoice not found with DOC_ID: " + docId));

	    // 2. Iterate over all invoice items
	    for (EzInvoiceItems item : header.getEzInvoiceItems()) {
	        String poNo = item.getId().getPoNo();
	        String material = item.getMaterial();
	        Integer poItemNumber = item.getPoItemNumber();
	        BigDecimal allocatedQty = item.getQuantity();

	        // 3. Find the corresponding PO Item
	        EzPoItems poItem = ezPoItemsRepo.findByPoNoAndMaterial(poNo, material, String.valueOf(poItemNumber));
	        log.debug("poItem =>" + poItem.getQuantity() + "-" + poItem.getOpenQty());
	        if (poItem != null) {
	            BigDecimal currentOpenQty = (poItem.getOpenQty() != null) ? poItem.getOpenQty() : BigDecimal.ZERO;
	            log.debug("currentOpenQty =>" + currentOpenQty);
	            BigDecimal newOpenQty = currentOpenQty.add(allocatedQty);
	            log.debug("newOpenQty =>" + newOpenQty);
	            poItem.setOpenQty(newOpenQty);
	            ezPoItemsRepo.save(poItem);  // ✅ Save updated openQty
	        }
	    }
	    // 4. Mark invoice as REJECTED
	    //header.setStatus("REJECTED");
	    invoiceHeaderRepository.save(header);
	}

	@Override
	public EzInvoiceHeader getInvoiceHeaderById(Long docNo) {
		return invoiceHeaderRepository.findById(docNo).get();
	}

	@Override
	public SAPResponse postMIRO(Long docNo) {
		log.debug("inside miro call");
		LocalDate today = LocalDate.now();
		boolean isError = false;
		String dispMessage = "";
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String formattedDate = today.format(formatter);
	
		EzInvoiceHeader invoiceHeader = invoiceHeaderRepository.findById(docNo).get();
		
		LocalDateTime dateTime = invoiceHeader.getInvoiceDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

	    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    String formattedInvoiceDate = dateTime.format(outputFormatter);
		

		String tempSupplier = invoiceHeader.getUserId();
		tempSupplier = "0000000000000" + tempSupplier;
		tempSupplier = tempSupplier.substring(tempSupplier.length() - 10);
		log.debug("tempSupplier => " + tempSupplier);
		
		String startDate = invoiceHeader.getStartDate()+"";
		String endDate = invoiceHeader.getEndDate()+"";
		
		String poNo = invoiceHeader.getPoNo();
		
		String firstPoNo = poNo.split("/")[0];
		
        DateTimeFormatter inputFormatterTwo = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        LocalDateTime startDateTime = LocalDateTime.parse(startDate, inputFormatterTwo);
        LocalDateTime endDateTime = LocalDateTime.parse(endDate, inputFormatterTwo);

        // Extract date, month (text), and year separately
        int startDay = startDateTime.getDayOfMonth();
        
        int endDay = endDateTime.getDayOfMonth();
        String endMonth = endDateTime.getMonth().toString().substring(0, 3).toUpperCase(); // NOV
        int endYear = endDateTime.get(ChronoField.YEAR) % 100; // 
		
		
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	//Users userObj = (Users)authentication.getPrincipal();
    	//String firstName = userObj.getFirstName();
    	//String lastName = userObj.getLastName();
    	log.debug("invoiceHeader.getUserId() => " + invoiceHeader.getUserId());
    	Users userObj = userService.findUserByUserId(invoiceHeader.getUserId().substring(invoiceHeader.getUserId().length() - 7));
    	
    	tempSupplier.substring(tempSupplier.length() - 10);
        String firstName = userObj.getFirstName();
        String lastName = userObj.getLastName();
        
		log.debug( firstName +  " " + lastName + "BILL,FOR" + startDay + "-" + endDay + "-" + endMonth + endYear + "," + poNo);;
		
		BigDecimal invoiceGrossVal = new BigDecimal(0);

//		BigDecimal discountPercentage = new BigDecimal("0.18"); // 18% as a decimal
//		BigDecimal discountAmount = invoiceHeader.getInvoiceValue().multiply(discountPercentage);
//		invoiceGrossVal = invoiceHeader.getInvoiceValue().subtract(discountAmount);
		
		ArrayList<String> arrTypes=new ArrayList<String>();
    	arrTypes.add("BUS_PLACE");
    	arrTypes.add("TAX_CODE");
    	arrTypes.add("MIRVENTXT");

    	Map<String, List<EzValueMapping>> masterMap = masterService.getValueMapList(arrTypes);
		
    	
    	
    	List<EzValueMapping> mappings = masterMap.get("BUS_PLACE");
    	
    	List<EzValueMapping> miroVendTxt = masterMap.get("MIRVENTXT");
    	String busPlace = "MH01";
    	String vendNameTxt = "";
    	String loggedInUser = invoiceHeader.getUserId().replaceFirst("^0+(?!$)", "");;
    	if (mappings != null) {
    	    

    	    busPlace = mappings.stream().filter(m -> loggedInUser.equals(m.getValue1())).map(EzValueMapping::getValue2).findFirst().orElse(null);    	
    	    log.debug(busPlace);
    	} else {
    	    log.debug("No mappings found for BUS_PLACE");
    	}
    	
    	if (miroVendTxt != null) {

    		vendNameTxt = miroVendTxt.stream().filter(m -> loggedInUser.equals(m.getValue1())).map(EzValueMapping::getValue2).findFirst().orElse(null);    	
    	    log.debug(busPlace);
    	} else {
    	    log.debug("No mappings found for BUS_PLACE");
    	}
    	log.debug("PO NOU => " + poNo);
    	String textString = vendNameTxt + ",BILL,FOR" + startDay + "-" + endDay + "-" + endMonth + endYear + "," + firstPoNo;
    	log.debug("textstring => " + textString);
    	List<EzValueMapping> mappingsForTaxCode = masterMap.get("TAX_CODE");
    	
    	log.debug("TEST TAX CODE => " + masterMap.get("TAX_CODE"));
    	
    	String taxCode = "IJ";
    	String taxPercentage = "0.18";
    	if (mappingsForTaxCode != null) {
    	    //String loggedInUser = invoiceHeader.getUserId().replaceFirst("^0+(?!$)", "");
    	    log.debug("USER => " + loggedInUser);
    	    //taxCode = mappingsForTaxCode.stream().filter(m -> loggedInUser.equals(m.getValue1())).map(EzValueMapping::getValue2).findFirst().orElse(null);   
    	    
    	    taxCode = mappingsForTaxCode.stream()
    	            .filter(m -> loggedInUser.equals(m.getValue1()))
    	            .map(EzValueMapping::getValue2)
    	            .map(value -> value.contains("#") ? value.split("#")[0] : value) // Split by '#' and take first part
    	            .findFirst()
    	            .orElse(null);
    	    
    	    taxPercentage = mappingsForTaxCode.stream()
    	            .filter(m -> loggedInUser.equals(m.getValue1()))
    	            .map(EzValueMapping::getValue2)
    	            .map(value -> value.contains("#") ? value.split("#")[1] : value) // Split by '#' and take second part
    	            .findFirst()
    	            .orElse(null);
    	} else {
    	    log.debug("No mappings found for TAX_CODE");
    	}    
    	
		//invoiceGrossVal = invoiceHeader.getInvoiceValue().divide(BigDecimal.ONE.add(new BigDecimal(taxPercentage)));
    	
    	
		BigDecimal taxRate = new BigDecimal(taxPercentage).divide(new BigDecimal("100")); // Convert to 0.18
		log.debug("taxPercentage => ::" + taxRate);
		log.debug("taxCode => ::" + taxCode);
    	
    	 invoiceGrossVal = invoiceHeader.getInvoiceValue().divide(
    	      BigDecimal.ONE.add(taxRate), 2, RoundingMode.HALF_UP // Scale 10 with rounding
    	 );
    	 
    	 
    	 log.debug("invoiceGrossVal => " + invoiceGrossVal);		
		

		
    	
		BAPI_INCOMINGINVOICE_PARK request = new BAPI_INCOMINGINVOICE_PARK();

		
		BAPI_INCINV_CREATE_ADDRESSDATA addrStruct = new BAPI_INCINV_CREATE_ADDRESSDATA();

		BAPI_INCINV_CREATE_HEADER headerStruct = new BAPI_INCINV_CREATE_HEADER();
		headerStruct.setINVOICE_IND("X");
		headerStruct.setREF_DOC_NO(invoiceHeader.getInvoiceNo());// invoice No
		log.debug("formattedInvoiceDate => ::" + formattedInvoiceDate);
		headerStruct.setDOC_DATE(formattedInvoiceDate);
		headerStruct.setPSTNG_DATE(formattedDate);// today Date 16.01.2025
		headerStruct.setCOMP_CODE("1000");
		headerStruct.setDIFF_INV(tempSupplier);
		headerStruct.setGROSS_AMOUNT(invoiceHeader.getInvoiceValue());// invoice value
		headerStruct.setCURRENCY("INR");
		headerStruct.setCALC_TAX_IND("X");
		headerStruct.setBUSINESS_PLACE(busPlace);
		headerStruct.setITEM_TEXT(textString);
		
		
		log.debug("taxCODES => " + taxCode + "busplace " + busPlace);

		BAPI_INCOMINGINVOICE_PARK.ITEMDATA invItems = new BAPI_INCOMINGINVOICE_PARK.ITEMDATA();

		BAPI_INCOMINGINVOICE_PARK.RETURN returnIn = new BAPI_INCOMINGINVOICE_PARK.RETURN();

		BAPI_INCOMINGINVOICE_PARK.GLACCOUNTDATA glItems = new BAPI_INCOMINGINVOICE_PARK.GLACCOUNTDATA();

		BAPI_INCINV_CREATE_GL_ACCOUNT glItem = new BAPI_INCINV_CREATE_GL_ACCOUNT();
		glItem.setINVOICE_DOC_ITEM("000010");
		glItem.setGL_ACCOUNT("0031000022");
		glItem.setITEM_AMOUNT(invoiceGrossVal);// invoice value
		glItem.setCOSTCENTER("3031022900");
		glItem.setDB_CR_IND("S");
		glItem.setCOMP_CODE("1000");
		glItem.setTAX_CODE(taxCode);
		glItems.getItem().add(glItem);

		request.setADDRESSDATA(addrStruct);
		request.setHEADERDATA(headerStruct);
		request.setGLACCOUNTDATA(glItems);
		request.setITEMDATA(invItems);
		request.setRETURN(returnIn);

		BAPI_INCOMINGINVOICE_PARKResponse resp = null;

		try {
			resp = (BAPI_INCOMINGINVOICE_PARKResponse) miroCreateService.callService(request);
			log.debug("resp => " + resp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.debug("Error in MIRO creation" + e);
		}
		List<BAPIRET2> repOutput = null;// resp.getOUT_PO_TAB().getItem();
		int repOutputRows = 0;

		// out.println("<Br>:::::::PO NUMBER:::::::"+mainParams.getEXPPURCHASEORDER());
		log.debug(resp.getINVOICEDOCNUMBER());
		if (resp.getRETURN() != null) {
			repOutput = resp.getRETURN().getItem();
			repOutputRows = repOutput.size();
		}
		if (repOutputRows > 0) {
			for (int r = 0; r < repOutputRows; r++) {
				BAPIRET2 outputRows = (BAPIRET2) repOutput.get(r);

				String lv_Type = (String) outputRows.getTYPE();
				String lv_Msg = (String) outputRows.getMESSAGE();

				log.debug(lv_Type + "::::::::::::::" + lv_Msg);

				if ("S".equals(lv_Type)) {
					dispMessage += lv_Msg + "<Br>";

				} else if ("E".equals(lv_Type)) {
					isError = true;
					dispMessage += lv_Msg + "<Br>";

				}

			}

		}

		BAPIRETURN requestNew = new BAPIRETURN();
		String vendorNew = requestNew.getLOG_MSG_NO();
		log.debug("VENDOR = > " + vendorNew);

		System.out.println("dispMessage => " + dispMessage);
		SAPResponse response = new SAPResponse();
		response.setError(isError);
		response.setMessage(dispMessage);
		if (!isError) {
			// set status to approved and update invoice no
			invoiceHeader.setMiroNumber(resp.getINVOICEDOCNUMBER());
			invoiceHeader.setStatus("APPROVED");
			response.setMessage("MIRO call successful, MIRO number: " + resp.getINVOICEDOCNUMBER());
		}
		return response;
	}

	public List<EzInvoiceReportDto> getInvoiceReports(String vendor) {

		LocalDate today = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String formattedDate = today.format(formatter);

		Z_EZ_GET_VENDOR_INVOICE_LIST request = new Z_EZ_GET_VENDOR_INVOICE_LIST();
		request.setCOMPANYCODE("1000");
		request.setVENDOR(vendor);
		request.setKEYDATE(formattedDate);
		request.setINVFLAG("O");

		Z_EZ_GET_VENDOR_INVOICE_LISTResponse response = null;

		try {
			response = (Z_EZ_GET_VENDOR_INVOICE_LISTResponse) invoiceReportSAPService.callService(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.debug("Error in Invoice Report" + e);
		}

		List<ZBAPI3008_2> respOutput = null;
		int rowsCount = 0;

		if (response.getLINEITEMS().getItem() != null) {
			respOutput = response.getLINEITEMS().getItem();
			rowsCount = respOutput.size();
		}

		List<EzInvoiceReportDto> ezInvoiceReportDto = new ArrayList<EzInvoiceReportDto>();

		if (rowsCount > 0) {
			for (ZBAPI3008_2 tempObj : response.getLINEITEMS().getItem()) {
				EzInvoiceReportDto item = new EzInvoiceReportDto();
				item.setVendor(tempObj.getVENDOR());
				item.setSapInvNo(tempObj.getDOC_NO());
				item.setVendorInvNo(tempObj.getREF_DOC_NO());
				item.setAmount(tempObj.getAMOUNT());
				item.setCurrency(tempObj.getCURRENCY());
				item.setInvoiceDate(tempObj.getDOC_DATE());
				item.setPaymentDueDate(tempObj.getBLINE_DATE());
				item.setPoNumber(tempObj.getALLOC_NMBR());
				ezInvoiceReportDto.add(item);
			}
		}

		return ezInvoiceReportDto;
	}

*/
/*
	public List<EzAccountStatementDto> getAccountStatements(ListSelector listSelector) {
		String vendor = listSelector.getUser().get(0);

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String fromDate = formatter.format(listSelector.getFromDate());
		String toDate = formatter.format(listSelector.getToDate());

		Z_EZ_GET_VENDOR_INVOICE_LIST request = new Z_EZ_GET_VENDOR_INVOICE_LIST();
		request.setCOMPANYCODE("1000");
		request.setVENDOR(vendor);
		request.setDATE_FROM(fromDate);
		request.setDATE_TO(toDate);
		request.setINVFLAG("B");

		Z_EZ_GET_VENDOR_INVOICE_LISTResponse response = null;
		try {
			response = (Z_EZ_GET_VENDOR_INVOICE_LISTResponse) invoiceReportSAPService.callService(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.debug("Error in Invoice Report" + e);
		}

		List<ZBAPI3008_2> respOutput = null;
		int rowsCount = 0;

		if (response.getLINEITEMS().getItem() != null) {
			respOutput = response.getLINEITEMS().getItem();
			rowsCount = respOutput.size();
		}
		BigDecimal balanceAmt = new BigDecimal(0);
		List<EzAccountStatementDto> ezAccountStatementDto = new ArrayList<EzAccountStatementDto>();
		if (rowsCount > 0) {
			for (ZBAPI3008_2 tempObj : response.getLINEITEMS().getItem()) {
				String drCRInd = tempObj.getDB_CR_IND();
				String docType = tempObj.getDOC_TYPE();
				EzAccountStatementDto item = new EzAccountStatementDto();

				String particulars = null;
				switch (docType) {
				case "KZ":
					particulars = "Payment";
					break;
				case "RE":
					particulars = "Invoice";
					break;
				case "AB":
					particulars = "A/C Adjustments";
					break;
				case "OV":
					particulars = "Carry Forward";
					break;
				default:
					particulars = "N/A";
				}

				item.setPostingDate(tempObj.getPSTNG_DATE());
				item.setParticulars(particulars);
				item.setBillReference(tempObj.getREF_DOC_NO());

				if (drCRInd.equals("H")) {
					item.setCreditAmount(tempObj.getLC_AMOUNT());
					item.setDebitAmount(new BigDecimal(0));
					balanceAmt = balanceAmt.add(tempObj.getLC_AMOUNT());
				} else {
					item.setDebitAmount(tempObj.getLC_AMOUNT());
					item.setCreditAmount(new BigDecimal(0));
					balanceAmt = balanceAmt.add(tempObj.getLC_AMOUNT().negate());
				}
				item.setBalance(balanceAmt);

				ezAccountStatementDto.add(item);
			}
		}
		return ezAccountStatementDto;
	}
*//*


	public HashMap<String, String> getMaterialNetWt(ArrayList<String> itemsList, String plant, Date date) {
		String [] outputFields =new String[] {"MATNR","FWEIGHT"};
		
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = outputFormat.format(date);
		
		String [] queryValues =new String[itemsList.size()+2];
		//queryValues[0] = "BEGDA >= '"+formattedDate+"' AND ENDDA <= '"+formattedDate+"'";
		//queryValues[0] = "BEGDA >= '"+formattedDate+"' AND ENDDA <= '"+formattedDate+"'";
		queryValues[0] = "WERKS EQ '"+plant+"' AND ";
		int tempMatCnt=1;
        //log.debug("formattedDate => " + formattedDate + "PlantNo => " + plant);
		for(String tempMat : itemsList)
		{
			if(tempMat.matches("\\d*"))
			{
				tempMat="00000000000000000000000000000000" + tempMat;
				tempMat=tempMat.substring(tempMat.length()-18);
			}
			if(tempMatCnt == 1)
				queryValues[tempMatCnt]= "MATNR IN ('"+tempMat+"'";
			else
				queryValues[tempMatCnt]= ",'"+tempMat+"'";
		
			tempMatCnt++;
		}
		queryValues[tempMatCnt-1] += ")";
		queryValues[tempMatCnt] = "AND BEGDA <= '"+formattedDate+"' AND ENDDA >= '"+formattedDate+"'";
		
		for(String temp:queryValues)
		{
			log.debug("::::"+temp);	
		}
		
		HashMap<String, String> htMatWt = new HashMap<String, String>();
		
		List<String[]> matList= rfcReadSAPService.readRFCTable("ZPP007", outputFields, queryValues);
		for(String[] tempArrObj : matList)
		{
			String tempVal = tempArrObj[1].trim();
			String tempMat = tempArrObj[0].trim();
			tempMat = tempMat.replaceFirst("^0+(?!$)", "");
			htMatWt.put(tempMat, tempVal);
		}
		log.debug(htMatWt+"");
		return htMatWt;
	}


	public List<String> getAllInvoiceNos() {
		return invoiceHeaderRepository.findAllInvoiceNos();
	}

	@Autowired
    private JavaMailSender emailSender;

    @Value("${site.url}")
    private String siteUrl="";

    @Value("${mail.fromaddress}")
    private String fromEmail="";

    @Value("${mail.ccmail}")
    private String ccmail="";
    
    public void sendInvoiceMail(String subject, String fromEmail,  String[] toEmails, String recipientName, Long docNo, String messageBody, String actionLabel) {
//        String vendorUserId = invoiceHeader.getUserId();
    

        String link = "http://10.62.10.138/invoice/details/" + docNo;
        String mailSub = (subject == null || subject.isEmpty()) 
            ? actionLabel + " - Invoice ID: " + docNo 
            : subject;

        String mailTxt = EmailFormat.formatEmail(
            actionLabel,
            recipientName,
            messageBody,
            link,
            "Regards, OneSource Team",
            "View Invoice"
        );

        MimeMessage mimeMessage = emailSender.createMimeMessage();

        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            message.setFrom(fromEmail);
            message.setTo(toEmails);
//            message.setBcc("pratik.sonawane@answerthink.com, sagar.dhagale@ceat.com");
            
            message.setBcc(new String[] {
        	    "pratik.sonawane@answerthink.com",
        	    "sagar.dhagale@ceat.com"
        	});

            if (ccmail != null && !"null".equals(ccmail) && !"".equals(ccmail)) {
                message.setCc(ccmail);
            }

            message.setSubject(mailSub);
            message.setText(mailTxt, true);
        } catch (MessagingException e) {
            log.error("Error setting up email message: " + e.getMessage());
        }

        try {
            emailSender.send(mimeMessage);
            log.info("Email sent to vendor: " + toEmails);
        } catch (Exception e) {
            log.error("Error in sending email: " + e.getMessage());
        }
    }


}
*/
