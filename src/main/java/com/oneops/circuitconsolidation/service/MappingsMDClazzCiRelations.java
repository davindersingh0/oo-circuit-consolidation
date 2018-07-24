package com.oneops.circuitconsolidation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.exceptions.UnSupportedOperation;
import com.oneops.circuitconsolidation.mappings.dal.OOConsolidationMapper;
import com.oneops.circuitconsolidation.model.CmsCIRelationAndRelationAttributesActionMappingsModel;
import com.oneops.circuitconsolidation.model.CmsCIRelationModel;
import com.oneops.circuitconsolidation.util.CircuitconsolidationUtil;
import com.oneops.circuitconsolidation.util.IConstants;
import com.oneops.circuitconsolidation.util.PackDefinitionUtil;
import com.oneops.cms.md.domain.CmsRelation;
import com.oneops.cms.md.domain.CmsRelationAttribute;
import com.oneops.cms.md.service.CmsMdProcessor;

public class MappingsMDClazzCiRelations {

  private final Logger log = LoggerFactory.getLogger(MappingsMDClazzCiRelations.class);

  @Autowired
  PackDefinitionUtil packDefinitionUtil;

  @Autowired
  private OOConsolidationMapper ooConsolidationMapper;

  @Autowired
  private CmsMdProcessor mdProcessor;

  @Autowired
  private Gson gson;

  private Map<String, CmsCIRelationModel> cmsCIRelationTypesInpackDefinitionMap;

  public void setPackDefinitionUtil(PackDefinitionUtil packDefinitionUtil) {
    this.packDefinitionUtil = packDefinitionUtil;
  }

  public void setOoConsolidationMapper(OOConsolidationMapper ooConsolidationMapper) {
    this.ooConsolidationMapper = ooConsolidationMapper;
  }

  public void setMdProcessor(CmsMdProcessor mdProcessor) {
    this.mdProcessor = mdProcessor;
  }

  public void setGson(Gson gson) {
    this.gson = gson;
  }

  public List<CmsCIRelationAndRelationAttributesActionMappingsModel> createCiRelationsMappings(
      String packSource, String packName, String ooPhase, String deplomentType) {

    this.cmsCIRelationTypesInpackDefinitionMap = packDefinitionUtil
        .getCiRelationsFromPackDefinition(packSource, packName, ooPhase, deplomentType);

    String envName = null;
    String targetPackCatlogName = packSource + "-" + packName;

    // Begin: hard coded for apache cassandra pack
    String sourcePackCatlogName = "walmartlabs" + "-" + packName;
    String ns_sourcePack = "/TestOrg2/ms-wmtlabs-a-cass";
    String platformName_sourcePack = "cass";
    String ns_targetPack = "/TestOrg2/ms-oneops-a-cass";
    String platformName_targetPack = "oneops-apache-cassandra";
    // End: hard coded for apache cassandra pack

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


    List<CmsCIRelationAndRelationAttributesActionMappingsModel> cmsCIRelationAndRelationAttributesActionMappingsList =
        createCmsCIRelationMappings(ns_sourcePack, platformName_sourcePack, ns_targetPack,
            platformName_targetPack, ooPhase, envName, sourcePackCatlogName, targetPackCatlogName);

    log.info("cmsCIRelationAndRelationAttributesActionMappingsList: "+gson.toJson(cmsCIRelationAndRelationAttributesActionMappingsList));
    return cmsCIRelationAndRelationAttributesActionMappingsList;
  }

  private List<CmsCIRelationAndRelationAttributesActionMappingsModel> createCmsCIRelationMappings(
      String ns_sourcePack, String platformName_sourcePack, String ns_targetPack,
      String platformName_targetPack, String ooPhase, String envName, String sourcePackCatlogName,
      String targetPackCatlogName) {

    Map<String, CmsCIRelationModel> cmsCIRelationTypes_sourcePack =
        getCmsCIRelationTypesForAllCIRelationsInPack(ns_sourcePack, platformName_sourcePack,
            ooPhase, envName);

    Map<String, CmsCIRelationModel> cmsCIRelationTypes_targetPack =
        getCmsCIRelationTypesForAllCIRelationsInPack(ns_targetPack, platformName_targetPack,
            ooPhase, envName);


    List<CmsCIRelationAndRelationAttributesActionMappingsModel> cmsCIRelationAndRelationAttributesActionMappingsList =
        createCmsCIRelationAndRelationAttributesActionMappings(sourcePackCatlogName,
            cmsCIRelationTypes_sourcePack, targetPackCatlogName, cmsCIRelationTypes_targetPack);

    return cmsCIRelationAndRelationAttributesActionMappingsList;
  }


  private Map<String, CmsCIRelationModel> getCmsCIRelationTypesForAllCIRelationsInPack(String ns,
      String platformName, String ooPhase, String envName) {


    String nsForPlatformCiComponents =
        CircuitconsolidationUtil.getnsForPlatformCiComponents(ns, platformName, ooPhase, envName);

    List<CmsCIRelationModel> cmsCIRelationsPlatformComponentsNsPath = ooConsolidationMapper
        .getCIRelations(nsForPlatformCiComponents, null, null, null, null, null, null);

    Map<String, CmsCIRelationModel> cmsCIRelationTypes =
        createCmsCIRelationTypesForAllCIRelationsInPack(cmsCIRelationsPlatformComponentsNsPath);

    return cmsCIRelationTypes;

  }

  private List<CmsCIRelationAndRelationAttributesActionMappingsModel> createCmsCIRelationAndRelationAttributesActionMappings(
      String sourcePack, Map<String, CmsCIRelationModel> cmsCIRelationTypes_sourcePack,
      String targetPack, Map<String, CmsCIRelationModel> cmsCIRelationTypes_targetPack) {

    List<CmsCIRelationAndRelationAttributesActionMappingsModel> cmsCIRelationAndRelationAttributesActionMappingsList =
        new ArrayList<CmsCIRelationAndRelationAttributesActionMappingsModel>();

    for (String cmsCIRelationTypeKey_source : cmsCIRelationTypes_sourcePack.keySet()) {
      CmsCIRelationAndRelationAttributesActionMappingsModel cmsCIRelationAndRelationAttributesActionMapping =
          new CmsCIRelationAndRelationAttributesActionMappingsModel();

      CmsCIRelationModel sourceCmsCIRelation =
          cmsCIRelationTypes_sourcePack.get(cmsCIRelationTypeKey_source);
      CmsCIRelationModel targetCmsCIRelation =
          cmsCIRelationTypes_targetPack.get(cmsCIRelationTypeKey_source);

      if (targetCmsCIRelation != null) {
        cmsCIRelationAndRelationAttributesActionMapping.setSourcePack(sourcePack);
        cmsCIRelationAndRelationAttributesActionMapping
            .setSourceCmsCiRelationKey(cmsCIRelationTypeKey_source);

        cmsCIRelationAndRelationAttributesActionMapping
            .setSourceCmsCIRelationModel(sourceCmsCIRelation);


        cmsCIRelationAndRelationAttributesActionMapping.setTargetPack(targetPack);
        cmsCIRelationAndRelationAttributesActionMapping
            .setTargetCmsCIRelationModel(targetCmsCIRelation);

        cmsCIRelationAndRelationAttributesActionMapping.setAction("NO_ACTION");
        cmsCIRelationAndRelationAttributesActionMapping.setEntityType("CMSCI_RELATION");
        /*
         * cmsCIRelationAndRelationAttributesActionMappingsList
         * .add(cmsCIRelationAndRelationAttributesActionMapping);
         */ } else {

        cmsCIRelationAndRelationAttributesActionMapping.setSourcePack(sourcePack);
        cmsCIRelationAndRelationAttributesActionMapping
            .setSourceCmsCiRelationKey(cmsCIRelationTypeKey_source);
        cmsCIRelationAndRelationAttributesActionMapping
            .setSourceCmsCIRelationModel(sourceCmsCIRelation);



        cmsCIRelationAndRelationAttributesActionMapping.setTargetPack(targetPack);
        cmsCIRelationAndRelationAttributesActionMapping.setEntityType("CMSCI_RELATION");
        cmsCIRelationAndRelationAttributesActionMapping.setAction("DELETE_RELATION");
        cmsCIRelationAndRelationAttributesActionMappingsList
            .add(cmsCIRelationAndRelationAttributesActionMapping);
      }


    }


    for (String cmsCIRelationTypeKey_target : cmsCIRelationTypes_targetPack.keySet()) {
      CmsCIRelationAndRelationAttributesActionMappingsModel cmsCIRelationAndRelationAttributesActionMapping =
          new CmsCIRelationAndRelationAttributesActionMappingsModel();

      CmsCIRelationModel sourceCmsCIRelation =
          cmsCIRelationTypes_sourcePack.get(cmsCIRelationTypeKey_target);
      CmsCIRelationModel targetCmsCIRelation =
          cmsCIRelationTypes_targetPack.get(cmsCIRelationTypeKey_target);
      if (sourceCmsCIRelation == null) {

        // createRelation
        cmsCIRelationAndRelationAttributesActionMapping.setSourcePack(sourcePack);
        cmsCIRelationAndRelationAttributesActionMapping.setTargetPack(targetPack);

        cmsCIRelationAndRelationAttributesActionMapping
            .setTargetCmsCIRelationModel(targetCmsCIRelation);

        cmsCIRelationAndRelationAttributesActionMapping.setAction("CREATE_RELATION");
        cmsCIRelationAndRelationAttributesActionMapping.setEntityType("CMSCI_RELATION");

        cmsCIRelationAndRelationAttributesActionMappingsList
            .add(cmsCIRelationAndRelationAttributesActionMapping);


        populateCmsCIRelationAttributesMappings(
            cmsCIRelationAndRelationAttributesActionMappingsList, cmsCIRelationTypeKey_target,
            sourcePack, targetPack, targetCmsCIRelation);
        log.info("cmsCIRelationTypeKey_target: " + cmsCIRelationTypeKey_target);


        continue;
      }

    }


    return cmsCIRelationAndRelationAttributesActionMappingsList;
  }

  private void populateCmsCIRelationAttributesMappings(
      List<CmsCIRelationAndRelationAttributesActionMappingsModel> cmsCIRelationAndRelationAttributesActionMappingsList,
      String cmsCIRelationTypeKey_target, String sourcePack, String targetPack,
      CmsCIRelationModel targetCmsCIRelation) {


    String relationName = targetCmsCIRelation.getRelationName();

    CmsRelation relationClazz = mdProcessor.getRelation(relationName);
    log.info("relationClazz: :" + gson.toJson(relationClazz));

    for (CmsRelationAttribute relationAttr : relationClazz.getMdAttributes()) {

      CmsCIRelationAndRelationAttributesActionMappingsModel cmsCIRelationAndRelationAttributesActionMapping =
          new CmsCIRelationAndRelationAttributesActionMappingsModel();

      cmsCIRelationAndRelationAttributesActionMapping.setSourcePack(sourcePack);
      cmsCIRelationAndRelationAttributesActionMapping.setTargetPack(targetPack);

      cmsCIRelationAndRelationAttributesActionMapping.setAction("ADD_RELATION_ATTRIBUTE");
      cmsCIRelationAndRelationAttributesActionMapping.setEntityType("CMSCI_RELATION_ATTRIBUTE");

      cmsCIRelationAndRelationAttributesActionMapping
          .setTargetCmsCIRelationModel(targetCmsCIRelation);

      cmsCIRelationAndRelationAttributesActionMapping.setRelationId(relationAttr.getRelationId());
      cmsCIRelationAndRelationAttributesActionMapping.setAttributeId(relationAttr.getAttributeId());
      cmsCIRelationAndRelationAttributesActionMapping
          .setAttributeName(relationAttr.getAttributeName());
      // cmsCIRelationAttribute.setComments("updated for circuit consolidation");

      // get values from pack definition
      log.info("cmsCIRelationTypeKey_target: " + cmsCIRelationTypeKey_target);

      CmsCIRelationModel CmsCIRelationFromPackDefinition =
          this.cmsCIRelationTypesInpackDefinitionMap.get(cmsCIRelationTypeKey_target);
      log.info("CmsCIRelationFromPackDefinition: " + gson.toJson(CmsCIRelationFromPackDefinition));

      String dfValueFromPackDefinition = CmsCIRelationFromPackDefinition.getAttributes()
          .get(relationAttr.getAttributeName()).getDfValue();
      String djValueFromPackDefinition = CmsCIRelationFromPackDefinition.getAttributes()
          .get(relationAttr.getAttributeName()).getDjValue();
      log.info("dfValueFromPackDefinition: " + dfValueFromPackDefinition);
      log.info("djValueFromPackDefinition: " + djValueFromPackDefinition);

      if (!relationAttr.isMandatory() && StringUtils.isEmpty(dfValueFromPackDefinition)
          && StringUtils.isEmpty(djValueFromPackDefinition)) {
        continue;
      }

      cmsCIRelationAndRelationAttributesActionMapping.setDfValue(dfValueFromPackDefinition);
      cmsCIRelationAndRelationAttributesActionMapping.setDjValue(djValueFromPackDefinition);

      cmsCIRelationAndRelationAttributesActionMappingsList
          .add(cmsCIRelationAndRelationAttributesActionMapping);

    }

  }

  private Map<String, CmsCIRelationModel> createCmsCIRelationTypesForAllCIRelationsInPack(
      List<CmsCIRelationModel> cmsCIRelationList) {
    Map<String, CmsCIRelationModel> cmsCIRelationTypes = new TreeMap<String, CmsCIRelationModel>();

    for (CmsCIRelationModel cmsCIRelation : cmsCIRelationList) {

      String key =
          cmsCIRelation.getRelationName() + "|" + getMappingKey(cmsCIRelation.getFromCiClazz())
              + "|" + getMappingKey(cmsCIRelation.getToCiClazz());
      cmsCIRelationTypes.put(key, cmsCIRelation);

    }
    return cmsCIRelationTypes;
  }

  
  public void publishCmsCIRelationAndRelationAttributesActionMappings(String ooPhase, 
      List<CmsCIRelationAndRelationAttributesActionMappingsModel> cmsCIRelationAndRelationAttributesActionMappingsList) {
    log.info("populating mappings into CMS database");
    for (CmsCIRelationAndRelationAttributesActionMappingsModel cmsCIRelationAndRelationAttributesActionMappings : cmsCIRelationAndRelationAttributesActionMappingsList) {
     
      cmsCIRelationAndRelationAttributesActionMappings.setOoPhase(ooPhase);
      ooConsolidationMapper.populateCmsCIRelationAndRelationAttributesActionMappings(
          cmsCIRelationAndRelationAttributesActionMappings);
    }

    log.info("populated mappings into CMS database");

  }
  
  private String getMappingKey(String str) {

    String[] strArr = str.split("\\.");

    String prefix = strArr[0];
    String suffix = strArr[strArr.length - 1];

    String[] suffixArr = suffix.split("_");
    String refinedSuffix = suffixArr[suffixArr.length - 1];

    String key = prefix + refinedSuffix;
    return key.toLowerCase();

  }
}
