package com.ezc.aragenPR.webapp.dto;

import java.util.List;

import com.ezc.aragenPR.webapp.model.EzValueMapping;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SeqCondMapObj {

	//private List<EzPriceSeqMaster> priceSeqList = new ArrayList<EzPriceSeqMaster>();
	private boolean isSelected;
	private String condType;
	private String condDesc;
	private List<SequenceDto> seqList;
	private List<String> checkedItems;
	
		
	
	
}
