/*
package com.ezc.aragenPR.webapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import com.ezc.aragenPR.webapp.dto.EzEhsSustainabilityMaterialDTO;
import com.ezc.aragenPR.webapp.model.EzEhsSustainabilityMaterial;
import com.ezc.aragenPR.webapp.persistance.dao.EzEhsSustainabilityMaterialRepository;

@Service
public class EzEhsSustainabilityMaterialServiceImpl implements EzEhsSustainabilityMaterialService {

    @Autowired
    private EzEhsSustainabilityMaterialRepository repository;

    @Override
    public List<EzEhsSustainabilityMaterial> findAll() {
        return repository.findAll();
    }

    @Override
    public EzEhsSustainabilityMaterial findByKey(String key) {
        return repository.findByKey(key);
    }
    

    @Override
    public List<EzEhsSustainabilityMaterialDTO> getActiveMaterials() {
        List<EzEhsSustainabilityMaterial> materials = repository
                .findByStatus(EzEhsSustainabilityMaterial.Status.ACTIVE);

        // Convert to DTO
        return materials.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    private EzEhsSustainabilityMaterialDTO convertToDTO(EzEhsSustainabilityMaterial entity) {
        EzEhsSustainabilityMaterialDTO dto = new EzEhsSustainabilityMaterialDTO();
        dto.setKey(entity.getKey());
        dto.setDefaultValue(entity.getDefaultValue());
        dto.setDerivedValue(entity.getDerivedValue());
        dto.setDescription(entity.getDescription());
        dto.setEditable(entity.getEditable());
        dto.setFormIndex(entity.getFormIndex());
        dto.setHighRange(entity.getHighRange());
        dto.setLowRange(entity.getLowRange());
        dto.setRequired(entity.getRequired());
        dto.setStatus(entity.getStatus().name());
        dto.setType(entity.getType());
        dto.setIsHeader(entity.getIsHeader());
        dto.setUom(entity.getUom());
        return dto;
    }
}

*/
