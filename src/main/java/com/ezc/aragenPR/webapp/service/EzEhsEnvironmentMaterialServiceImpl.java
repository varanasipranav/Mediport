/*
package com.ezc.aragenPR.webapp.service;

import java.util.List;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ezc.aragenPR.webapp.dto.EzEhsEnvironmentMaterialDTO;
import com.ezc.aragenPR.webapp.model.EzEhsEnvironmentMaterial;
import com.ezc.aragenPR.webapp.persistance.dao.EzEhsEnvironmentMaterialRepository;

@Service
public class EzEhsEnvironmentMaterialServiceImpl implements EzEhsEnvironmentMaterialService{

	private final EzEhsEnvironmentMaterialRepository environmentMaterialRepository;

    public EzEhsEnvironmentMaterialServiceImpl(EzEhsEnvironmentMaterialRepository environmentMaterialRepository) {
        this.environmentMaterialRepository = environmentMaterialRepository;
    }

    @Override
    public List<EzEhsEnvironmentMaterialDTO> getAllMaterials() {
        return environmentMaterialRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EzEhsEnvironmentMaterialDTO> getActiveMaterials() {
        return environmentMaterialRepository.findByStatus(EzEhsEnvironmentMaterial.Status.ACTIVE).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private EzEhsEnvironmentMaterialDTO toDTO(EzEhsEnvironmentMaterial material) {
        EzEhsEnvironmentMaterialDTO dto = new EzEhsEnvironmentMaterialDTO();
        dto.setKey(material.getKey());
        dto.setDescription(material.getDescription());
        dto.setDefaultValue(material.getDefaultValue());
        dto.setDerivedValue(material.getDerivedValue());
        dto.setEditable(material.getEditable());
        dto.setFormIndex(material.getFormIndex());
        dto.setLowRange(material.getLowRange());
        dto.setHighRange(material.getHighRange());
        dto.setRequired(material.getRequired());
        dto.setUom(material.getUom());
        dto.setIsHeader(material.getIsHeader());
        return dto;
    }

}
*/
