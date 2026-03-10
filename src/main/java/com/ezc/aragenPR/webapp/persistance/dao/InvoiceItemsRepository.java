package com.ezc.aragenPR.webapp.persistance.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ezc.aragenPR.webapp.model.EzInvoiceItems;

public interface InvoiceItemsRepository extends JpaRepository<EzInvoiceItems, String> {

}
