/*
package com.ezc.aragenPR.webapp.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ezc.aragenPR.webapp.service.GRNItemsSapService;
import com.ezc.aragenPR.webapp.service.RestTemplateService;
import com.ezc.aragenPR.webapp.wsdl.grn.items.gen.BAPI2017_GM_HEAD_02;
import com.ezc.aragenPR.webapp.wsdl.grn.items.gen.BAPI2017_GM_ITEM_SHOW;
import com.ezc.aragenPR.webapp.wsdl.grn.items.gen.BAPI2017_GM_MOVE_TYPE_RA;
import com.ezc.aragenPR.webapp.wsdl.grn.items.gen.BAPI2017_GM_PLANT_RA;
import com.ezc.aragenPR.webapp.wsdl.grn.items.gen.BAPI2017_GM_PSTNG_DATE_RA;
import com.ezc.aragenPR.webapp.wsdl.grn.items.gen.BAPI2017_GM_TR_EV_TYPE_RA;
import com.ezc.aragenPR.webapp.wsdl.grn.items.gen.BAPI_GOODSMVT_GETITEMS;
import com.ezc.aragenPR.webapp.wsdl.grn.items.gen.BAPI_GOODSMVT_GETITEMSResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class UploadInvoiceController {

    @Autowired
    private RestTemplateService restTemplateService;
    
    @Autowired
    private GRNItemsSapService gRNItemsSapService;

    @PostMapping(value = "/rest-template-service")
    public ResponseEntity<String> handleFileUploadMap(@RequestBody HashMap<String, String> input) {
    	System.out.println("Enter into handleFileUpload method: before try:");
        try {
            // Convert file to Base64
           String base64File = input.get("file");

            // Call the API and get the response
            return restTemplateService.callApi(base64File);

            // Add response to the model to display on the page
            //model.addAttribute("response", apiResponse);

            //return "purchaseOrder/uploadInvoice"; // Redirect to a success page or re-render the current page
        } catch (Exception e) {
            log.error(e.toString());
            
           // return "purchaseOrder/list"; // Redirect to an error page or re-render with error
            return null;
        }
    }
    
    @GetMapping(value = "/get-submitted-qty")
    public ResponseEntity<Map<String, BigDecimal>> getSubmittedQty(
    		@RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("plantNo") String plantNo
    		) {

        Map<String, BigDecimal> materialMap = new HashMap<String, BigDecimal>();
		try{
			BAPI_GOODSMVT_GETITEMS mainParams = new BAPI_GOODSMVT_GETITEMS();

			BAPI_GOODSMVT_GETITEMS.RETURN returnObj = new BAPI_GOODSMVT_GETITEMS.RETURN();
			BAPI_GOODSMVT_GETITEMS.GOODSMVT_HEADER hdr = new BAPI_GOODSMVT_GETITEMS.GOODSMVT_HEADER();
			BAPI_GOODSMVT_GETITEMS.GOODSMVT_ITEMS items = new BAPI_GOODSMVT_GETITEMS.GOODSMVT_ITEMS();
			mainParams.setGOODSMVT_HEADER(hdr);
			mainParams.setGOODSMVT_ITEMS(items);
			mainParams.setRETURN(returnObj);
			log.debug("STORAGE LOC => " + mainParams.getSTGE_LOC_RA());

			BAPI_GOODSMVT_GETITEMS.PSTNG_DATE_RA lv_PostingDt = new BAPI_GOODSMVT_GETITEMS.PSTNG_DATE_RA();
			List <BAPI2017_GM_PSTNG_DATE_RA> li_PostingDt = lv_PostingDt.getItem();
			BAPI2017_GM_PSTNG_DATE_RA r_PostingDt = new BAPI2017_GM_PSTNG_DATE_RA();
			r_PostingDt.setSIGN("I");
			r_PostingDt.setOPTION("BT");
			r_PostingDt.setLOW(startDate);
			r_PostingDt.setHIGH(endDate);
			li_PostingDt.add(r_PostingDt);

			mainParams.setPSTNG_DATE_RA(lv_PostingDt);
			
			
			BAPI_GOODSMVT_GETITEMS.MOVE_TYPE_RA lv_trnsType = new BAPI_GOODSMVT_GETITEMS.MOVE_TYPE_RA();
			List<BAPI2017_GM_MOVE_TYPE_RA> li_trnsType = lv_trnsType.getItem();
			BAPI2017_GM_MOVE_TYPE_RA r_trnsType = new BAPI2017_GM_MOVE_TYPE_RA();
			r_trnsType.setSIGN("I");
			r_trnsType.setOPTION("BT");
			if("TR00".equals(plantNo) || "AR00".equals(plantNo))
			{
				r_trnsType.setLOW("101");
				r_trnsType.setHIGH("102");
			}
			else
			{
				r_trnsType.setLOW("641");
				r_trnsType.setHIGH("642");
			}
			li_trnsType.add(r_trnsType);
			
			mainParams.setMOVE_TYPE_RA(lv_trnsType);
			
			
			BAPI_GOODSMVT_GETITEMS.PLANT_RA lv_plantType = new BAPI_GOODSMVT_GETITEMS.PLANT_RA();
			List<BAPI2017_GM_PLANT_RA> li_plantType = lv_plantType.getItem();
			BAPI2017_GM_PLANT_RA r_plantType = new BAPI2017_GM_PLANT_RA();
			r_plantType.setSIGN("I");
			r_plantType.setOPTION("EQ");
			log.debug("plantNo => " + plantNo);
			r_plantType.setLOW(plantNo);
			r_plantType.setHIGH("");
			li_plantType.add(r_plantType);
			
			mainParams.setPLANT_RA(lv_plantType);


			BAPI_GOODSMVT_GETITEMSResponse resp = (BAPI_GOODSMVT_GETITEMSResponse) gRNItemsSapService.callService(mainParams);
			
				
			List<BAPI2017_GM_ITEM_SHOW> repItems =  null;// resp.getOUTPUT().getItem();
			int repItemsRows = 0;

			if(resp.getGOODSMVT_ITEMS()!=null)
			{
				repItems = resp.getGOODSMVT_ITEMS().getItem();
				repItemsRows = repItems.size();
			}
//			System.out.println(repItems.get(0));
			if(repItemsRows>0)
			{
				for(int r=0;r<repItemsRows;r++)
				{	
					BAPI2017_GM_ITEM_SHOW outputRows = repItems.get(r);
					
					
		            // Log each item's details
					BigDecimal zero = new BigDecimal("0");
		            // Aggregate quantities by material
					if(outputRows.getMATERIAL() == null ||  "null".equals(outputRows.getMATERIAL())) {
						continue;
					}
					if(outputRows.getSTGE_LOC() == null || "null".equals(outputRows.getSTGE_LOC())) {
						continue;
					}
		            String material = outputRows.getMATERIAL().replaceFirst("^0+", "");
		            BigDecimal quantity = outputRows.getENTRY_QNT();
		            
		            
//		            materialMap.put(material, materialMap.getOrDefault(material, zero).add(quantity));
		            
		            String moveType = outputRows.getMOVE_TYPE();
		            log.debug("outputRows.getSTGE_LOC() => " + outputRows.getSTGE_LOC());
		            log.debug("move type => " + moveType);
		            materialMap.putIfAbsent(material, zero);
		            
		            
		            if ("641".equals(moveType) || "101".equals(moveType)) {
		                materialMap.put(material, materialMap.get(material).add(quantity));
		            } else if ("642".equals(moveType) || "102".equals(moveType)) {
		                materialMap.put(material, materialMap.get(material).subtract(quantity));
		            }
					
//					docItems.setFieldValue("MAT_DOC",outputRows.getMAT_DOC());
//					docItems.setFieldValue("DOC_YEAR",outputRows.getDOC_YEAR());
//					docItems.setFieldValue("MATDOC_ITM",outputRows.getMATDOC_ITM());
//					docItems.setFieldValue("MATERIAL",outputRows.getMATERIAL()); // need this
//					docItems.setFieldValue("PLANT",outputRows.getPLANT());
//					docItems.setFieldValue("STGE_LOC",outputRows.getSTGE_LOC());
//					docItems.setFieldValue("BATCH",outputRows.getBATCH());
//					docItems.setFieldValue("MOVE_TYPE",outputRows.getMOVE_TYPE());
//					docItems.setFieldValue("STCK_TYPE",outputRows.getSTCK_TYPE());
//					docItems.setFieldValue("VENDOR",outputRows.getVENDOR());
//					docItems.setFieldValue("VAL_TYPE",outputRows.getVAL_TYPE());
//					docItems.setFieldValue("ENTRY_QNT",outputRows.getENTRY_QNT()); // need this (submitted qty)
//					docItems.setFieldValue("ENTRY_UOM",outputRows.getENTRY_UOM());
//					docItems.setFieldValue("ENTRY_UOM_ISO",outputRows.getENTRY_UOM_ISO());
//					docItems.setFieldValue("PO_PR_QNT",outputRows.getPO_PR_QNT());
//					docItems.setFieldValue("ORDERPR_UN",outputRows.getORDERPR_UN());
//					docItems.setFieldValue("ORDERPR_UN_ISO",outputRows.getORDERPR_UN_ISO());
//					docItems.setFieldValue("PO_NUMBER",outputRows.getPO_NUMBER());
//					docItems.setFieldValue("PO_ITEM",outputRows.getPO_ITEM());
//					docItems.setFieldValue("ITEM_TEXT",outputRows.getITEM_TEXT());
//					docItems.setFieldValue("MVT_IND",outputRows.getMVT_IND());
//					docItems.setFieldValue("PROFIT_CTR",outputRows.getPROFIT_CTR());
//					docItems.setFieldValue("WBS_ELEM",outputRows.getWBS_ELEM());
//					docItems.setFieldValue("AMOUNT_LC",outputRows.getAMOUNT_LC());
//					docItems.setFieldValue("AMOUNT_SV",outputRows.getAMOUNT_SV());
//					docItems.setFieldValue("CURRENCY",outputRows.getCURRENCY());
//					docItems.setFieldValue("CURRENCY_ISO",outputRows.getCURRENCY_ISO());
//					docItems.setFieldValue("REF_DOC_YR",outputRows.getREF_DOC_YR());
//					docItems.setFieldValue("REF_DOC",outputRows.getREF_DOC());
//					docItems.setFieldValue("REF_DOC_IT",outputRows.getREF_DOC_IT());
//					docItems.setFieldValue("IBD_NO","");
//					docItems.setFieldValue("IBD_ITEM","");
//					docItems.addRow();
				}
			}	

		}catch(Exception e){
			 log.error("Error while fetching submitted quantity", e);
		        return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(null);
		}
		return ResponseEntity.ok(materialMap);
	
    }
}
*/
