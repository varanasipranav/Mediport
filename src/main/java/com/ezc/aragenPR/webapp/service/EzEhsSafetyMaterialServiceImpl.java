/*
package com.ezc.aragenPR.webapp.service;

import com.ezc.aragenPR.webapp.dto.EzEhsSafetyMaterialDTO;
import com.ezc.aragenPR.webapp.model.EzEhsSafetyMaterial;
import com.ezc.aragenPR.webapp.persistance.dao.EzEhsSafetyMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EzEhsSafetyMaterialServiceImpl implements EzEhsSafetyMaterialService {

    @Autowired
    private EzEhsSafetyMaterialRepository safetyMaterialRepository;

    @Override
    public List<EzEhsSafetyMaterialDTO> getAllSafetyMaterials() {
        List<EzEhsSafetyMaterial> materials = safetyMaterialRepository.findAll();

        return materials.stream().map(material -> {
            EzEhsSafetyMaterialDTO dto = new EzEhsSafetyMaterialDTO();
            dto.setKey(material.getKey());
            dto.setDescription(material.getDescription());
           // dto.setType(material.get);
            dto.setLowRange(material.getLowRange());
            dto.setHighRange(material.getHighRange());
            dto.setDefaultValue(material.getDefaultValue());
            dto.setRequired(material.getRequired());
            dto.setDerivedValue(material.getDerivedValue());
         //   dto.setStatus(material.getStatus().toString());
            dto.setEditable(material.getEditable());
            dto.setFormIndex(material.getFormIndex());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<EzEhsSafetyMaterialDTO> getAllActiveMaterials() {
       

    	List<EzEhsSafetyMaterial> activeMaterials = safetyMaterialRepository.findByStatus(EzEhsSafetyMaterial.Status.ACTIVE);
  
       
        return activeMaterials.stream().map(material -> {
            EzEhsSafetyMaterialDTO dto = new EzEhsSafetyMaterialDTO();
            dto.setKey(material.getKey());
            dto.setDescription(material.getDescription());
          //  dto.setType(material.getType());
            dto.setLowRange(material.getLowRange());
            dto.setHighRange(material.getHighRange());
            dto.setDefaultValue(material.getDefaultValue());
            dto.setRequired(material.getRequired());
            dto.setDerivedValue(material.getDerivedValue());
         //   dto.setStatus(material.getStatus());
            dto.setEditable(material.getEditable());
            dto.setFormIndex(material.getFormIndex());
            return dto;
        }).collect(Collectors.toList());
   }
}

*/
