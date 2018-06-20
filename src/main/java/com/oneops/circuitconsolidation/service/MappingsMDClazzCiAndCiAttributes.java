package com.oneops.circuitconsolidation.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.exceptions.UnSupportedOperation;
import com.oneops.circuitconsolidation.mappings.CmsCiAndCmsCiAttributesActionMappingsModel;
import com.oneops.circuitconsolidation.mappings.dal.OOConsolidationMapper;
import com.oneops.circuitconsolidation.model.CmsCiTransformationModel;
import com.oneops.circuitconsolidation.util.CiMDClazzMappingsUtil;
import com.oneops.circuitconsolidation.util.CircuitconsolidationUtil;
import com.oneops.circuitconsolidation.util.IConstants;
import com.oneops.circuitconsolidation.util.PackDefinitionUtil;
import com.oneops.cms.cm.dal.CIMapper;
import com.oneops.cms.md.domain.CmsClazz;
import com.oneops.cms.md.domain.CmsClazzAttribute;
import com.oneops.cms.md.service.CmsMdProcessor;

public class MappingsMDClazzCiAndCiAttributes {

  private final Logger log = LoggerFactory.getLogger(MappingsMDClazzCiRelations.class);

  @Autowired
  PackDefinitionUtil packDefinitionUtil;

  @Autowired
  private Gson gson;

  @Autowired
  private CIMapper ciMapper;

  @Autowired
  CiMDClazzMappingsUtil ciMDClazzMappingsUtil;

  @Autowired
  private CmsMdProcessor mdProcessor;

  @Autowired
  OOConsolidationMapper ooConsolidationMapper;

  public void setPackDefinitionUtil(PackDefinitionUtil packDefinitionUtil) {
    this.packDefinitionUtil = packDefinitionUtil;
  }

  public void setGson(Gson gson) {
    this.gson = gson;
  }

  public void setCiMapper(CIMapper ciMapper) {
    this.ciMapper = ciMapper;
  }

  public void setCiMDClazzMappingsUtil(CiMDClazzMappingsUtil ciMDClazzMappingsUtil) {
    this.ciMDClazzMappingsUtil = ciMDClazzMappingsUtil;
  }

  public void setMdProcessor(CmsMdProcessor mdProcessor) {
    this.mdProcessor = mdProcessor;
  }

  public void setOoConsolidationMapper(OOConsolidationMapper ooConsolidationMapper) {
    this.ooConsolidationMapper = ooConsolidationMapper;
  }

  public void createCiAndAttributesMappings(String ooPhase) {

    String envName = null;

    switch (ooPhase) {
      case IConstants.DESIGN_PHASE:
        envName = null;
        break;
      case IConstants.TRANSITION_PHASE:
        envName = "dev";
        break;
      case IConstants.OPERATE_PHASE:

        log.error("ooPhase {} not supported", ooPhase);
        throw new UnSupportedOperation(ooPhase + " not supported");

      default:
        log.error("ooPhase {} not supported", ooPhase);
        throw new UnSupportedOperation(ooPhase + " not supported");

    }

    // Begin: hard coded for apache cassandra pack
    String ns_sourcePack = "/TestOrg2/ms-wmtlabs-a-cass";
    String platformName_sourcePack = "cass";
    String nsForPlatformCiComponents_sourcePack = CircuitconsolidationUtil
        .getnsForPlatformCiComponents(ns_sourcePack, platformName_sourcePack, ooPhase, envName);


    String ns_targetPack = "/TestOrg2/ms-oneops-a-cass";
    String platformName_targetPack = "oneops-apache-cassandra";
    String nsForPlatformCiComponents_targetPack = CircuitconsolidationUtil
        .getnsForPlatformCiComponents(ns_targetPack, platformName_targetPack, ooPhase, envName);

    // End: hard coded for apache cassandra pack
    List<CmsCiTransformationModel> mapppings =
        ciMDClazzMappingsUtil.getMDClazzMappingsForSourceAndTartgetPacks(
            nsForPlatformCiComponents_sourcePack, nsForPlatformCiComponents_targetPack);

    log.info("mapppings for {} phase : {}", ooPhase, gson.toJson(mapppings));

    Map<String, String> ciClazzesTransformationsMap = new HashMap<String, String>();
    List<String> deleteCiClazzesList = new ArrayList<String>();
    Map<String, String> createCiClazzesMap = new HashMap<String, String>();

    
    for (CmsCiTransformationModel cmsCiTransformationModel : mapppings) {

      String mappingAction = cmsCiTransformationModel.getCiAction();

      switch (mappingAction) {
        case IConstants.TRANSFORM_CMSCI:
          ciClazzesTransformationsMap.put(cmsCiTransformationModel.getSourceCiClazzName(),
              cmsCiTransformationModel.getTargetCiClazzName());
          break;
        case IConstants.DELETE_CMSCI:
          deleteCiClazzesList.add(cmsCiTransformationModel.getSourceCiClazzName());
          break;
        case IConstants.CREATE_CMSCI:
          String createCiClazz = cmsCiTransformationModel.getTargetCiClazzName().toLowerCase();
          if (createCiClazz.contains(".oneops.1.os") && ooPhase.equals(IConstants.DESIGN_PHASE)) {

            createCiClazzesMap.put("catalog.oneops.1.Compute",
                cmsCiTransformationModel.getTargetCiClazzName());
          }
          if (createCiClazz.contains(".oneops.1.os")
              && ooPhase.equals(IConstants.TRANSITION_PHASE)) {

            createCiClazzesMap.put("manifest.oneops.1.Compute",
                cmsCiTransformationModel.getTargetCiClazzName());
          }

          break;

        default:
          throw new UnSupportedOperation("mappingAction: " + mappingAction + " not supported");

      }

    }

    log.info("ciClazzesTransformationsMap: "+gson.toJson(ciClazzesTransformationsMap));
    log.info("deleteCiClazzesList: "+gson.toJson(deleteCiClazzesList));
    log.info("createCiClazzesMap: "+gson.toJson(createCiClazzesMap));
    
  //TODO: add feature to create & delete CI  
    List<CmsCiAndCmsCiAttributesActionMappingsModel> cmsCiAndCmsCiAttributesActionMappingsList =
        createCmCiAttributesMappings("walmartlabs-apache_cassandra", "oneops-apache_cassandra", ciClazzesTransformationsMap);

    log.info("cmsCiAndCmsCiAttributesActionMappingsList: "
        + gson.toJson(cmsCiAndCmsCiAttributesActionMappingsList));

  }

  public List<CmsCiAndCmsCiAttributesActionMappingsModel> createCmCiAttributesMappings2(
      String sourcePack, String targetPack, Map<String, String> ciMDClazzTransformationMap) {

    return null;

  }

  public List<CmsCiAndCmsCiAttributesActionMappingsModel> createCmCiAttributesMappings(
      String sourcePack, String targetPack, Map<String, String> ciClazzesTransformationsMap) {


    List<CmsCiAndCmsCiAttributesActionMappingsModel> cmCiAttributesActionMappingsList =
        new ArrayList<CmsCiAndCmsCiAttributesActionMappingsModel>();

    for (String sourceCircuitCmsClazzName : ciClazzesTransformationsMap.keySet()) {
      String targetCircuitCmsClazzName = ciClazzesTransformationsMap.get(sourceCircuitCmsClazzName);
      log.info(
          "Staring processing for <sourceCircuitCmsClazzName> {} , <targetCircuitCmsClazzName> {}",
          sourceCircuitCmsClazzName, targetCircuitCmsClazzName);

      CmsClazz sourceCmsClazz = mdProcessor.getClazz(sourceCircuitCmsClazzName);
      CmsClazz targetCmsClazz = mdProcessor.getClazz(targetCircuitCmsClazzName);


      Map<String, CmsClazzAttribute> sourceCmsClazzAttributesMap =
          getMdAttributesMap(sourceCmsClazz);
      Map<String, CmsClazzAttribute> targetCmsClazzAttributesMap =
          getMdAttributesMap(targetCmsClazz);

      // create mapping for CMCI Table
      CmsCiAndCmsCiAttributesActionMappingsModel cmCiClazzIDClazzNameAndGoidActionMappings =
          generateCmCiClazzIDClazzNameAndGoidActionMappings(sourcePack, sourceCmsClazz, targetPack,
              targetCmsClazz);

      cmCiAttributesActionMappingsList.add(cmCiClazzIDClazzNameAndGoidActionMappings);

      // create mapping for CMCI_Attributes Table
      List<CmsCiAndCmsCiAttributesActionMappingsModel> cmCiClazzAttributesActionMappings =
          generateCmCiAttributesActionMappings(sourcePack, sourceCmsClazz,
              sourceCmsClazzAttributesMap, targetPack, targetCmsClazz, targetCmsClazzAttributesMap);

      cmCiAttributesActionMappingsList.addAll(cmCiClazzAttributesActionMappings);

      log.info("list : " + gson.toJson(cmCiClazzAttributesActionMappings));
    }

    log.info("Mapping process for CmCiAttributesMappings complete: ");
    log.info("Number of Mappings created: " + cmCiAttributesActionMappingsList.size());
    log.info("<cmCiAttributesActionMappingsList>: {}",
        gson.toJson(cmCiAttributesActionMappingsList));
    return cmCiAttributesActionMappingsList;



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

  private List<CmsCiAndCmsCiAttributesActionMappingsModel> generateCmCiAttributesActionMappings(
      String sourcePack, CmsClazz sourceCmsClazz,
      Map<String, CmsClazzAttribute> sourceCmsClazzAttributesMap, String targetPack,
      CmsClazz targetCmsClazz, Map<String, CmsClazzAttribute> targetCmsClazzAttributesMap) {

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
    log.info("Number of mappings created from <sourceCmsClazz> {} to <targetCmsClazz> {} is {}",
        sourceCmsClazz.getClassName(), targetCmsClazz.getClassName(),
        cmCiAttributesActionMappingsList.size());
    log.info("jsonified cmCiAttributesActionMappings: {} ",
        gson.toJson(cmCiAttributesActionMappingsList));

    return cmCiAttributesActionMappingsList;


  }
}
