package com.ezc.aragenPR.webapp.service;

import java.util.List;

import com.ezc.aragenPR.webapp.model.EzInvoiceItemsDto;

public interface IPurchaseOrderService {
    void saveInvoiceItems(List<EzInvoiceItemsDto> allInvoiceItems);
}
