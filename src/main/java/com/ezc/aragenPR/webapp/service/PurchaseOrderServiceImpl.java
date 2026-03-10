package com.ezc.aragenPR.webapp.service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.ezc.aragenPR.webapp.model.EzInvoiceItemsDto;
import com.ezc.aragenPR.webapp.model.EzPoItems;
import com.ezc.aragenPR.webapp.model.EzPoItemsKey;
import com.ezc.aragenPR.webapp.persistance.dao.EzPoItemsRepo;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PurchaseOrderServiceImpl implements IPurchaseOrderService {

    private final EzPoItemsRepo ezPoItemsRepo;

    public PurchaseOrderServiceImpl(EzPoItemsRepo ezPoItemsRepo) {
        this.ezPoItemsRepo = ezPoItemsRepo;
    }

    @Override
    public void saveInvoiceItems(List<EzInvoiceItemsDto> allInvoiceItems) {
        List<EzPoItems> purchaseOrders = allInvoiceItems.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
        
        // Save all items to the database
        ezPoItemsRepo.saveAll(purchaseOrders);
    }

    private EzPoItems convertToEntity(EzInvoiceItemsDto dto) {
        log.debug("dto.getPlantNo() =>" + dto.getPlantNo());
        EzPoItems order = new EzPoItems();
        // ID will be auto-generated
        
        EzPoItemsKey key = new EzPoItemsKey(dto.getPoNo(), dto.getPoItemNumber());
        order.setId(key);

        
//        order.setPoNo(dto.getPoNo());
        order.setLineNo(dto.getPoItemNumber());
        order.setMaterial(dto.getMaterial());
        order.setPlantNo(dto.getPlantNo());

        order.setMatDesc(dto.getMatDesc());
        order.setUom(dto.getUom());
        order.setTotalQty(dto.getTotalQty());
        order.setQuantity(dto.getQuantity());
        order.setPrice(dto.getPrice());
//        order.setPoItemNumber(dto.getPoItemNumber());
        order.setNetWeight(dto.getNetWeight());
        order.setPricePerKg(dto.getPricePerKg());
        return order;
    }

}
