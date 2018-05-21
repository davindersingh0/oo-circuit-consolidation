package com.oneops.circuitconsolidation.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.exceptions.InvalidCacheLoadException;
import com.oneops.circuitconsolidation.exceptions.UnSupportedOperation;
import com.oneops.circuitconsolidation.model.CiRelationDefaultAttributeModel;
import com.oneops.circuitconsolidation.model.CiRelationModel;
import com.oneops.circuitconsolidation.util.CircuitconsolidationUtil;
import com.oneops.circuitconsolidation.util.IConstants;
import com.oneops.circuitconsolidation.util.ValidateComponentRelations;
import com.oneops.cms.cm.dal.CIMapper;
import com.oneops.cms.cm.domain.CmsCI;
import com.oneops.cms.cm.domain.CmsCIRelation;
import com.oneops.cms.cm.domain.CmsCIRelationAttribute;
import com.oneops.cms.cm.service.CmsCmProcessor;
import com.oneops.cms.md.domain.CmsRelation;
import com.oneops.cms.md.domain.CmsRelationAttribute;
import com.oneops.cms.md.service.CmsMdProcessor;
import com.oneops.cms.util.CmsUtil;

public class CIRelationsService {

  @Autowired
  CmsCmProcessor cmProcessor;
  @Autowired
  private CmsMdProcessor mdProcessor;

  @Autowired
  private Gson gson;

  @Autowired
  CIMapper ciMapper;

  @Autowired
  ValidateComponentRelations validateComponentRelations;

  private final Logger log = LoggerFactory.getLogger(getClass());

  Map<String, CiRelationModel[]> ciRelationsConfigurationMap;

  CIRelationsService() {
    setCiRelationsConfigurationMap(loadCiRelationsConfigs());

  }


  public Map<String, CiRelationModel[]> getCiRelationsConfigurationMap() {
    return ciRelationsConfigurationMap;
  }

  public void setCiRelationsConfigurationMap(
      Map<String, CiRelationModel[]> ciRelationsConfigurationMap) {
    this.ciRelationsConfigurationMap = ciRelationsConfigurationMap;
  }

  public void setCmProcessor(CmsCmProcessor cmProcessor) {
    this.cmProcessor = cmProcessor;
  }

  public void setMdProcessor(CmsMdProcessor mdProcessor) {
    this.mdProcessor = mdProcessor;
  }

  public void setGson(Gson gson) {
    this.gson = gson;
  }

  public void setCiMapper(CIMapper ciMapper) {
    this.ciMapper = ciMapper;
  }

  public void setValidateComponentRelations(ValidateComponentRelations validateComponentRelations) {
    this.validateComponentRelations = validateComponentRelations;
  }


  public boolean createComponentRelations(String ns, String platformName, String ooPhase,
      String envName) {

    boolean result = false;
    String nsForPlatformCiComponents =
        CircuitconsolidationUtil.getnsForPlatformCiComponents(ns, platformName, ooPhase, envName);

    CiRelationModel[] CiRelationsArr = getCiRelationsConfigs(ooPhase);

    if (validateComponentRelations.validateComponentExists(CiRelationsArr,
        nsForPlatformCiComponents, platformName, ns)) {

      Map<String, CmsCI> cmsCIComponentsMap = validateComponentRelations.getCmsCIComponentsMap();

      for (CiRelationModel ciRelation : CiRelationsArr) {
        log.info("processing CI relation for " + gson.toJson(ciRelation));

        CmsCI fromCmsCI = cmsCIComponentsMap.get(ciRelation.getFromCi());
        CmsCI toCmsCI = cmsCIComponentsMap.get(ciRelation.getToCi());

        Map<String, CmsCIRelationAttribute> defautAttributes =
            getDefaultCIRelationAttributesFromCMSDB(ciRelation.getRelationName());

        defautAttributes.putAll(getDefaultCIRelationAttributesFromConfigs(ciRelation));


        CmsCIRelation cmsCIRelation_existing = getExistingCmsCIRelation(nsForPlatformCiComponents,
            ciRelation.getRelationName(), fromCmsCI, toCmsCI);
        if (cmsCIRelation_existing == null) {
          CmsCIRelation cmsCIRelation = createRelations(fromCmsCI, toCmsCI,
              ciRelation.getRelationName(), defautAttributes, nsForPlatformCiComponents);
          log.info("cmsCIRelation createRelations response: " + cmsCIRelation);
          result = true;
        } else {
     
          cmsCIRelation_existing.setAttributes(defautAttributes);
          CmsCIRelation updatedCmsCIRelation = cmProcessor.updateRelation(cmsCIRelation_existing);
          log.info("updatedCmsCIRelation: " + gson.toJson(updatedCmsCIRelation));
          result = true;
        }

      }

    } else {
      log.error(
          "All required components were not found for ns {}, platformName {}, ooPhase {}, envName {} ",
          ns, platformName, ooPhase, envName);
      result = false;
    }

    return result;
  }
  
  public CmsCIRelation createRelations(CmsCI fromCmsCI, CmsCI toCmsCI, String relationName,
      Map<String, CmsCIRelationAttribute> CmsCIRelationAttributeMap, String nsPath) {
    // propagate_to attrib is missing in original compute, need to work on it
    log.info("creating CmsCI relation name {} ", relationName);
    CmsCIRelation cmsCIRelation = new CmsCIRelation();
    // create FROM Relation

    cmsCIRelation.setNsPath(nsPath);
    cmsCIRelation.setRelationName(relationName);


    cmsCIRelation.setFromCiId(fromCmsCI.getCiId());
    cmsCIRelation.setToCiId(toCmsCI.getCiId());

    cmsCIRelation.setAttributes(CmsCIRelationAttributeMap);
    cmsCIRelation.setComments(CmsUtil.generateRelComments(fromCmsCI.getCiName(),
        fromCmsCI.getCiClassName(), toCmsCI.getCiName(), toCmsCI.getCiClassName()));
    log.info("created CmsCI relation name {} ", relationName);
    return cmProcessor.createRelation(cmsCIRelation);


  }

  public Map<String, CmsCIRelationAttribute> getDefaultCIRelationAttributesFromCMSDB(
      String relationName) {

    Map<String, CmsCIRelationAttribute> cmsRelationAttributeMap =
        new HashMap<String, CmsCIRelationAttribute>();

    CmsRelation relationClazz = mdProcessor.getRelation(relationName);
    log.info("relationClazz: :" + gson.toJson(relationClazz));

    for (CmsRelationAttribute relationAttr : relationClazz.getMdAttributes()) {
      CmsCIRelationAttribute cmsCIRelationAttribute = new CmsCIRelationAttribute();
      log.info("relationAttr :" + gson.toJson(relationAttr));


      cmsCIRelationAttribute.setAttributeId(relationAttr.getAttributeId());
      cmsCIRelationAttribute.setAttributeName(relationAttr.getAttributeName());
      cmsCIRelationAttribute.setComments("updated for circuit consolidation");
      cmsCIRelationAttribute.setDfValue(relationAttr.getDefaultValue());
      cmsCIRelationAttribute.setDjValue(relationAttr.getDefaultValue());

      cmsRelationAttributeMap.put(relationAttr.getAttributeName(), cmsCIRelationAttribute);

    }
    return cmsRelationAttributeMap;

  }

  private Map<String, CmsCIRelationAttribute> getDefaultCIRelationAttributesFromConfigs(
      CiRelationModel ciRelation) {

    Map<String, CmsCIRelationAttribute> cmsRelationAttributeMap =
        new HashMap<String, CmsCIRelationAttribute>();

    Map<String, CiRelationDefaultAttributeModel> CiRelationDefaultAttributeMap =
        ciRelation.getCiRelationDefaultAttributeMap();
    // return empty map for cis relation do not have pre configured values
    if (CiRelationDefaultAttributeMap.size() == 0) {
      return cmsRelationAttributeMap;

    }

    CmsRelation relationClazz = mdProcessor.getRelation(ciRelation.getRelationName());
    log.info("relationClazz: :" + gson.toJson(relationClazz));

    for (CmsRelationAttribute relationAttr : relationClazz.getMdAttributes()) {

      CiRelationDefaultAttributeModel CiRelationDefaultAttribute =
          CiRelationDefaultAttributeMap.get(relationAttr.getAttributeName());

      if (CiRelationDefaultAttribute != null) {
        CmsCIRelationAttribute cmsCIRelationAttribute = new CmsCIRelationAttribute();
        log.info("relationAttr :" + gson.toJson(relationAttr));


        cmsCIRelationAttribute.setAttributeId(relationAttr.getAttributeId());
        cmsCIRelationAttribute.setAttributeName(relationAttr.getAttributeName());
        cmsCIRelationAttribute.setComments(IConstants.comments);
        cmsCIRelationAttribute.setDfValue(CiRelationDefaultAttribute.getRelationAttributeDfValue());
        cmsCIRelationAttribute.setDjValue(CiRelationDefaultAttribute.getRelationAttributeDjValue());
        cmsRelationAttributeMap.put(relationAttr.getAttributeName(), cmsCIRelationAttribute);
      }



    }
    return cmsRelationAttributeMap;
  }


  private CiRelationModel[] getCiRelationsConfigs(String ooPhase) {

    try {

      switch (ooPhase) {
        case IConstants.DESIGN_PHASE:
          return this.ciRelationsConfigurationMap.get(IConstants.DESIGN_PHASE);

        case IConstants.TRANSITION_PHASE:
          return this.ciRelationsConfigurationMap.get(IConstants.TRANSITION_PHASE);

        case IConstants.OPERATE_PHASE:
          log.error("ooPhase {} not supported", ooPhase);
          throw new UnSupportedOperation(ooPhase + " not supported");

        default:
          log.error("ooPhase {} not supported", ooPhase);

          throw new UnSupportedOperation(ooPhase + " not supported");

      }

    } catch (Exception e) {
      throw new InvalidCacheLoadException("Error while loading CiRelations configurations", e);
    }

  }

  private Map<String, CiRelationModel[]> loadCiRelationsConfigs() {
    Map<String, CiRelationModel[]> ciRelationsConfigurationMap =
        new HashMap<String, CiRelationModel[]>();
    Gson gson = new Gson();

    try {
      ciRelationsConfigurationMap.put(IConstants.DESIGN_PHASE,
          gson.fromJson(
              CircuitconsolidationUtil.getFileContent(IConstants.DESIGN_PHASE_CI_RELATIONS_File),
              CiRelationModel[].class));
      ciRelationsConfigurationMap.put(IConstants.TRANSITION_PHASE, gson.fromJson(
          CircuitconsolidationUtil.getFileContent(IConstants.TRANISTION_PHASE_CI_RELATIONS_File),
          CiRelationModel[].class));

    } catch (Exception e) {
      throw new InvalidCacheLoadException("Error while loading ciRelations configurations", e);
    }
    return ciRelationsConfigurationMap;
  }

  private CmsCIRelation getExistingCmsCIRelation(String nsForPlatformCiComponents, String relationName,
      CmsCI fromCmsCI, CmsCI toCmsCI) {
    String shortRelName = null;
    String fromNames_className = fromCmsCI.getCiClassName();
    String fromNames_shortClassName = null;
    String toNames_className = toCmsCI.getCiClassName();
    String toNames_shortClassName = null;

    List<CmsCIRelation> cmsCIRelationList = ciMapper.getCIRelations(nsForPlatformCiComponents,
        relationName, shortRelName, fromNames_className, fromNames_shortClassName,
        toNames_className, toNames_shortClassName);
    log.info("cmsCIRelationList " + gson.toJson(cmsCIRelationList));

    for (CmsCIRelation cmsCIRelation : cmsCIRelationList) {

      if (cmsCIRelation.getFromCiId() == fromCmsCI.getCiId()
          && cmsCIRelation.getToCiId() == toCmsCI.getCiId()) {
        return cmsCIRelation;
      }
    }

    return null;

  }

}
