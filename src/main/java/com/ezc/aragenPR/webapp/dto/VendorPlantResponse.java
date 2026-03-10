package com.ezc.aragenPR.webapp.dto;

import java.util.List;

public class VendorPlantResponse {
    private List<String> vendors;
    private List<String> plantNames;

    // Constructors
    public VendorPlantResponse() {}

    public VendorPlantResponse(List<String> vendors, List<String> plantNames) {
        this.vendors = vendors;
        this.plantNames = plantNames;
    }

    // Getters and Setters
    public List<String> getVendors() {
        return vendors;
    }

    public void setVendors(List<String> vendors) {
        this.vendors = vendors;
    }

    public List<String> getPlantNames() {
        return plantNames;
    }

    public void setPlantNames(List<String> plantNames) {
        this.plantNames = plantNames;
    }
}
