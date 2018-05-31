package com.oneops.circuitconsolidation.mappings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.mappings.dal.OOConsolidationMapper;
import com.oneops.circuitconsolidation.service.TransformCiAttributesService;
import com.oneops.cms.md.domain.CmsClazz;
import com.oneops.cms.md.domain.CmsClazzAttribute;
import com.oneops.cms.md.service.CmsMdProcessor;

public class MappingBuilder {

  @Autowired
  TransformCiAttributesService transformCiAttributesService;

  @Autowired
  Gson gson;

  @Autowired
  private CmsMdProcessor mdProcessor;

  @Autowired
  OOConsolidationMapper ooConsolidationMapper;
  
  private final Logger log = LoggerFactory.getLogger(MappingBuilder.class);


  public void setTransformCiAttributesService(
      TransformCiAttributesService transformCiAttributesService) {
    this.transformCiAttributesService = transformCiAttributesService;
  }

  public void setGson(Gson gson) {
    this.gson = gson;
  }

  public void setMdProcessor(CmsMdProcessor mdProcessor) {
    this.mdProcessor = mdProcessor;
  }

  public void setOoConsolidationMapper(OOConsolidationMapper ooConsolidationMapper) {
    this.ooConsolidationMapper = ooConsolidationMapper;
  }

  public void createCmCiAttributesMappings(String sourcePack, String targetPack) {


    Map<String, String> CiClazzesTransformationsMap =
        transformCiAttributesService.getTransformationSupportedCiClazzesConfigsMap();
    List<CmsCiAndCmsCiAttributesActionMappingsModel> cmCiAttributesActionMappingsList = new ArrayList<CmsCiAndCmsCiAttributesActionMappingsModel>();
    
    for (String sourceCircuitCmsClazzName : CiClazzesTransformationsMap.keySet()) {
      String targetCircuitCmsClazzName = CiClazzesTransformationsMap.get(sourceCircuitCmsClazzName);
      log.info(
          "Staring processing for <sourceCircuitCmsClazzName> {} , <targetCircuitCmsClazzName> {}",
          sourceCircuitCmsClazzName, targetCircuitCmsClazzName);

      CmsClazz sourceCmsClazz = mdProcessor.getClazz(sourceCircuitCmsClazzName);
      CmsClazz targetCmsClazz = mdProcessor.getClazz(targetCircuitCmsClazzName);


      Map<String, CmsClazzAttribute> sourceCmsClazzAttributesMap =
          getMdAttributesMap(sourceCmsClazz);
      Map<String, CmsClazzAttribute> targetCmsClazzAttributesMap =
          getMdAttributesMap(targetCmsClazz);
      
     //create mapping for CMCI Table
      CmsCiAndCmsCiAttributesActionMappingsModel cmCiClazzIDClazzNameAndGoidActionMappings =
          generateCmCiClazzIDClazzNameAndGoidActionMappings(sourcePack, sourceCmsClazz, targetPack, targetCmsClazz);
      
      cmCiAttributesActionMappingsList.add(cmCiClazzIDClazzNameAndGoidActionMappings);
      
    //create mapping for CMCI_Attributes Table
      List<CmsCiAndCmsCiAttributesActionMappingsModel> cmCiClazzAttributesActionMappings =
          generateCmCiAttributesActionMappings(sourcePack, sourceCmsClazz, sourceCmsClazzAttributesMap,
              targetPack, targetCmsClazz, targetCmsClazzAttributesMap);
      
      cmCiAttributesActionMappingsList.addAll(cmCiClazzAttributesActionMappings);
      
      log.info("list : " + gson.toJson(cmCiClazzAttributesActionMappings));
    }

    log.info("Mapping process for CmCiAttributesMappings complete: ");
    log.info("Number of Mappings created: "+cmCiAttributesActionMappingsList.size());
    log.info("<cmCiAttributesActionMappingsList>: {}",gson.toJson(cmCiAttributesActionMappingsList)); 
    


  }

  private CmsCiAndCmsCiAttributesActionMappingsModel generateCmCiClazzIDClazzNameAndGoidActionMappings(
      String sourcePack, CmsClazz sourceCmsClazz, String targetPack, CmsClazz targetCmsClazz) {
    
    CmsCiAndCmsCiAttributesActionMappingsModel cmCiAttributesActionMappings =
        new CmsCiAndCmsCiAttributesActionMappingsModel();
    cmCiAttributesActionMappings.setEntityType("CMCI");
    cmCiAttributesActionMappings.setAction("UPDATE_CMCI_CLAZZID_CLAZZNAME_GOID");
    
    cmCiAttributesActionMappings.setSourcePack(sourcePack);
    cmCiAttributesActionMappings.setSourceClassId(sourceCmsClazz.getClassId());
    cmCiAttributesActionMappings.setSourceClassname(sourceCmsClazz.getClassName());
    
    cmCiAttributesActionMappings.setTargetPack(targetPack);
    cmCiAttributesActionMappings.setTargetClassId(targetCmsClazz.getClassId());
    cmCiAttributesActionMappings.setTargetClassname(targetCmsClazz.getClassName());
    
    
    return cmCiAttributesActionMappings;
  }

  private Map<String, CmsClazzAttribute> getMdAttributesMap(CmsClazz cmsClazz) {

    List<CmsClazzAttribute> cmsClazzAttributesList = cmsClazz.getMdAttributes();
    Map<String, CmsClazzAttribute> cmsClazzAttributesMap = new HashMap<String, CmsClazzAttribute>();

    for (CmsClazzAttribute cmsClazzAttribute : cmsClazzAttributesList) {
      cmsClazzAttributesMap.put(cmsClazzAttribute.getAttributeName(), cmsClazzAttribute);

    }

    return cmsClazzAttributesMap;

  }

  private List<CmsCiAndCmsCiAttributesActionMappingsModel> generateCmCiAttributesActionMappings(String sourcePack,
      CmsClazz sourceCmsClazz, Map<String, CmsClazzAttribute> sourceCmsClazzAttributesMap,
      String targetPack, CmsClazz targetCmsClazz,
      Map<String, CmsClazzAttribute> targetCmsClazzAttributesMap) {

    List<CmsCiAndCmsCiAttributesActionMappingsModel> cmCiAttributesActionMappingsList =
        new ArrayList<CmsCiAndCmsCiAttributesActionMappingsModel>();

    for (String sourceCircuitAttributeName : sourceCmsClazzAttributesMap.keySet()) {
      CmsCiAndCmsCiAttributesActionMappingsModel cmCiAttributesActionMappings =
          new CmsCiAndCmsCiAttributesActionMappingsModel();

      CmsClazzAttribute sourceCmsClazzAttribute =
          sourceCmsClazzAttributesMap.get(sourceCircuitAttributeName);
      if (targetCmsClazzAttributesMap.containsKey(sourceCircuitAttributeName)) {

        cmCiAttributesActionMappings.setEntityType("CMCI_ATTRIBUTE");
        cmCiAttributesActionMappings.setAction("UPDATE_SOURCE_ATTRIBUTE_ID");

        cmCiAttributesActionMappings.setSourcePack(sourcePack);
        cmCiAttributesActionMappings.setSourceClassname(sourceCmsClazz.getClassName());
        cmCiAttributesActionMappings.setSourceClassId(sourceCmsClazz.getClassId());
        cmCiAttributesActionMappings.setSourceCmsClazzAttributeMappings(sourceCmsClazzAttribute);

        cmCiAttributesActionMappings.setTargetPack(targetPack);
        cmCiAttributesActionMappings.setTargetClassname(targetCmsClazz.getClassName());
        cmCiAttributesActionMappings.setTargetClassId(targetCmsClazz.getClassId());
        cmCiAttributesActionMappings.setTargetCmsClazzAttributeMappings(
            targetCmsClazzAttributesMap.get(sourceCircuitAttributeName));

        targetCmsClazzAttributesMap.remove(sourceCircuitAttributeName);
        cmCiAttributesActionMappingsList.add(cmCiAttributesActionMappings);

      } else {
        // deleteAttribute or UnlinkFromCmSCi by deleting CIiD or setting CiId to some unused value
        cmCiAttributesActionMappings.setEntityType("CMCI_ATTRIBUTE");
        cmCiAttributesActionMappings.setAction("DELETE_SOURCE_ATTRIBUTE_ID");
        cmCiAttributesActionMappings.setSourcePack(sourcePack);
        cmCiAttributesActionMappings.setSourceClassname(sourceCmsClazz.getClassName());
        cmCiAttributesActionMappings.setSourceClassId(sourceCmsClazz.getClassId());
        

        cmCiAttributesActionMappings.setSourceCmsClazzAttributeMappings(sourceCmsClazzAttribute);
        cmCiAttributesActionMappings.setTargetPack(targetPack);
        cmCiAttributesActionMappings.setTargetClassId(targetCmsClazz.getClassId());
        cmCiAttributesActionMappings.setTargetClassname(targetCmsClazz.getClassName());

        cmCiAttributesActionMappingsList.add(cmCiAttributesActionMappings);

      }
    }

    if (targetCmsClazzAttributesMap.size() > 0) {
      // setDefaults
      for (CmsClazzAttribute targetCmsClazzAttribute : targetCmsClazzAttributesMap.values()) {
        CmsCiAndCmsCiAttributesActionMappingsModel cmCiAttributesActionMappings =
            new CmsCiAndCmsCiAttributesActionMappingsModel();
        cmCiAttributesActionMappings.setEntityType("CMCI_ATTRIBUTE");
        cmCiAttributesActionMappings.setAction("SET_DEFAULT_ATTRIBUTE_VALUE");
        cmCiAttributesActionMappings.setSourcePack(sourcePack);
        cmCiAttributesActionMappings.setSourceClassname(sourceCmsClazz.getClassName());
        cmCiAttributesActionMappings.setSourceClassId(sourceCmsClazz.getClassId());

        cmCiAttributesActionMappings.setTargetPack(targetPack);
        cmCiAttributesActionMappings.setTargetClassId(targetCmsClazz.getClassId());
        cmCiAttributesActionMappings.setTargetClassname(targetCmsClazz.getClassName());
        
        cmCiAttributesActionMappings.setTargetCmsClazzAttributeMappings(targetCmsClazzAttribute);
        cmCiAttributesActionMappingsList.add(cmCiAttributesActionMappings);
      }


    }
    log.info("Number of mappings created from <sourceCmsClazz> {} to <targetCmsClazz> {} is {}",sourceCmsClazz.getClassName(), targetCmsClazz.getClassName(), cmCiAttributesActionMappingsList.size());
    log.info(
        "jsonified cmCiAttributesActionMappings: {} " , gson.toJson(cmCiAttributesActionMappingsList));
    
    return cmCiAttributesActionMappingsList;


  }
  
  
  
  

}
