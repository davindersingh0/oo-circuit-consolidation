package com.oneops.circuitconsolidation.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.exceptions.InvalidCacheLoadException;
import com.oneops.circuitconsolidation.exceptions.UnSupportedOperation;
import com.oneops.circuitconsolidation.util.CircuitConsolidationMain;
import com.oneops.circuitconsolidation.util.CircuitconsolidationUtil;
import com.oneops.circuitconsolidation.util.IConstants;
import com.oneops.cms.cm.dal.CIMapper;
import com.oneops.cms.cm.domain.CmsCI;
import com.oneops.cms.cm.domain.CmsCIAttribute;
import com.oneops.cms.md.domain.CmsClazz;
import com.oneops.cms.md.domain.CmsClazzAttribute;
import com.oneops.cms.md.service.CmsMdProcessor;

public class TransformCiAttributesService {

  // TODO: "topo": "catalog.walmartlabs.Topo", topology component does not exists in oneops pack.
  // Need to break this component into oneops acceptable format

  // TODO:
  // get CiAttributesForNewlyupdated ClassID,Class Name - current API may throw exception because of
  // MDClass AttributeIDs from old class
  // TODO: transform_CiClassID_ciClassName_ciGoid
  // ciMapper.addCIAttributeAndPublish(updAttr); for adding new attribute
  // ciMapper.updateCIAttribute(updAttr) for updating
  //
  @Autowired
  Gson gson;

  @Autowired
  CIMapper ciMapper;

  @Autowired
  private CmsMdProcessor mdProcessor;

  private final Logger log = LoggerFactory.getLogger(CircuitConsolidationMain.class);

  Map<String, String> transformationSupportedCiClazzesConfigsMap = new HashMap<String, String>();


  public TransformCiAttributesService() {
    setTransformationSupportedCiClazzesConfigsMap(loadCiClazzesTransformationConfigs());

  }

  public Map<String, String> getTransformationSupportedCiClazzesConfigsMap() {
    return transformationSupportedCiClazzesConfigsMap;
  }


  public void setTransformationSupportedCiClazzesConfigsMap(
      Map<String, String> transformationSupportedCiClazzesConfigsMap) {
    this.transformationSupportedCiClazzesConfigsMap = transformationSupportedCiClazzesConfigsMap;
  }

  public void setGson(Gson gson) {
    this.gson = gson;
  }

  public void setCiMapper(CIMapper ciMapper) {
    this.ciMapper = ciMapper;
  }


  public void setMdProcessor(CmsMdProcessor mdProcessor) {
    this.mdProcessor = mdProcessor;
  }

  public Map<String, String> loadCiClazzesTransformationConfigs() {

    try {

      Gson gson = new Gson();
      @SuppressWarnings("unchecked")
      Map<String, String> map = gson.fromJson(
          CircuitconsolidationUtil.getFileContent(IConstants.CI_CLAZZES_TRANSFORMATION_MAP_FILE),
          Map.class);
      log.info("ciClazzesTransformationConfigsMap: " + map.keySet());
      return map;
    } catch (Exception e) {
      throw new InvalidCacheLoadException(
          "Error while loading " + IConstants.CI_CLAZZES_TRANSFORMATION_MAP_FILE, e);
    }

  }

  @Deprecated
  private List<CmsCI> getAllCmsCIComponentsForPlatform(String ns, String platformName,
      String ooPhase, String envName) {

    // getPlatformCi
    String nsForPlatformCiComponents =
        CircuitconsolidationUtil.getnsForPlatformCiComponents(ns, platformName, ooPhase, envName);
    List<CmsCI> cmsCIList = getCmsCIListForNs(nsForPlatformCiComponents);

    if (cmsCIList == null || cmsCIList.size() == 0) {

      log.error("No Cis found for for ns {}, platformName {}, ooPhase {}, envName {} ", ns,
          platformName, ooPhase, envName);
      throw new UnSupportedOperation();
    }

    log.info("AllCmsCIComponentsForPlatform : {}", gson.toJson(cmsCIList));
    return cmsCIList;

  }


  private List<CmsCI> getCmsCIListForNs(String nsPath) {

    List<CmsCI> cmsCIList = ciMapper.getCIby3(nsPath, null, null, null);
    return cmsCIList;
  }

  @SuppressWarnings("unused")
  @Deprecated
  private List<CmsCI> getTransformationSupportedCIs(String ns, String platformName, String ooPhase,
      String envName) {

    // get platform & its components CIs
    List<CmsCI> cmsCIList = getAllCmsCIComponentsForPlatform(ns, platformName, ooPhase, envName);
    List<CmsCI> transformationSupportedCmsCIList = new ArrayList<CmsCI>();
    for (CmsCI cmsCI : cmsCIList) {

      long ciId = cmsCI.getCiId();
      String ciClassName = cmsCI.getCiClassName();
      log.info("jsonifiedCi: " + gson.toJson(cmsCI));
      if (this.transformationSupportedCiClazzesConfigsMap.get(ciClassName) != null) {
        log.info("<ciId> {} for <ciClassName> {} set for transformation", ciId, ciClassName);
        transformationSupportedCmsCIList.add(cmsCI);
        continue;

      }
      log.info("<ciId> {} for <ciClassName> {} not supported for transformation", ciId,
          ciClassName);
    }
    return transformationSupportedCmsCIList;


  }


  public boolean transformCIs(String ns, String platformName, String ooPhase, String envName) {
    boolean isTransformationSuccess = false;

    String nsForPlatformCiComponents =
        CircuitconsolidationUtil.getnsForPlatformCiComponents(ns, platformName, ooPhase, envName);
    List<CmsCI> cmsCIList =
        getTransformationSupportedCmsCiComponentsForPlatform(nsForPlatformCiComponents);
    log.info("TransformationSupportedCmsCiComponentsForPlatform: " + gson.toJson(cmsCIList));

    for (CmsCI cmsCI : cmsCIList) {

      String fromCiClassName = cmsCI.getCiClassName();
      String toCiClassName = this.transformationSupportedCiClazzesConfigsMap.get(fromCiClassName);

      CmsCI cmsCI_withTransformedCmsCIAttribute =
          transformCmsCIClassAttributes(cmsCI, fromCiClassName, toCiClassName);

      log.info("<cmsCI_withTransformedCmsCIAttribute> {}", cmsCI_withTransformedCmsCIAttribute);

      boolean istransform_CiClassID_ciClassName_ciGoidSuccess =
          transform_CiClassID_ciClassName_ciGoid(cmsCI, fromCiClassName, toCiClassName);

      log.info("<istransform_CiClassID_ciClassName_ciGoidSuccess> {}",
          istransform_CiClassID_ciClassName_ciGoidSuccess);

      isTransformationSuccess = true;

    }
    log.info("<isTransformationSuccess> {}", isTransformationSuccess);
    return isTransformationSuccess;
  }

  private boolean transform_CiClassID_ciClassName_ciGoid(CmsCI cmsCI, String fromCiClassName,
      String toCiClassName) {
    log.info(
        "Begin: transform_CiClassID_ciClassName_ciGoid of ciComponentID: {}, ciComponentName {}",
        cmsCI.getCiId(), cmsCI.getCiName());


    log.info("get CmsClazz for  componentCiTransformToClass: {}", toCiClassName);
    CmsClazz toCmsClazz = mdProcessor.getClazz(toCiClassName);

    CmsCI cmsCI_transformed_ClassID_ClassName_Goid =
        transformCmsCI_ClassID_ClassName_Goid(cmsCI, toCmsClazz);
    log.info("cmsCI_transformed_ClassID_ClassName_Goid: "
        + gson.toJson(cmsCI_transformed_ClassID_ClassName_Goid));

    log.info("End: transform_CiClassID_ciClassName_ciGoid of ciComponentID: {}, ciComponentName {}",
        cmsCI.getCiId(), cmsCI.getCiName());
    return false;
  }


  private CmsCI transformCmsCI_ClassID_ClassName_Goid(CmsCI sourceCmsCI, CmsClazz cmsClazz) {

    // update classID for CmsCi
    sourceCmsCI.setCiClassId(cmsClazz.getClassId());
    // update className for CmsCi
    sourceCmsCI.setCiClassName(cmsClazz.getClassName());
    // update className for CmsCi Goid
    sourceCmsCI.setCiGoid(
        sourceCmsCI.getNsId() + "-" + cmsClazz.getClassId() + "-" + sourceCmsCI.getCiId());
    // TODO: //create database call for update

    return sourceCmsCI;
  }


  private CmsCI transformCmsCIClassAttributes(CmsCI sourceCmsCI, String fromCiClassName,
      String toCiClassName) {

    CmsClazz toCmsClazz = mdProcessor.getClazz(toCiClassName);


    Map<String, CmsCIAttribute> cmsCIAttributes_target = new HashMap<String, CmsCIAttribute>();
    List<CmsClazzAttribute> toCmsClazzAttributeList = toCmsClazz.getMdAttributes();
    Map<String, CmsCIAttribute> sourceCmsCIAttributesMap =
        getCIAttrsMapForCiId(sourceCmsCI.getCiId());


    // update attributes IDs for CmsCi
    for (CmsClazzAttribute toCmsClazzAttribute : toCmsClazzAttributeList) {
      log.info("start processing <toCmsClazzAttribute> {}", toCmsClazzAttribute.getAttributeName());
      CmsCIAttribute sourceCmsCIAttribute =
          sourceCmsCIAttributesMap.get(toCmsClazzAttribute.getAttributeName());

      if (sourceCmsCIAttribute != null) // attribute Name exists in sourceCmsCI
      {
        sourceCmsCIAttribute.setAttributeId(toCmsClazzAttribute.getAttributeId());
        log.info("Updating existing <toCmsClazzAttribute> {}",
            toCmsClazzAttribute.getAttributeName());
        // ciMapper.updateCIAttribute(sourceCmsCIAttribute); // update attribute
        log.info("Updated existing <toCmsClazzAttribute> {}",
            toCmsClazzAttribute.getAttributeName());


      } else {
        sourceCmsCIAttribute = new CmsCIAttribute();
        sourceCmsCIAttribute.setAttributeId(toCmsClazzAttribute.getAttributeId());
        sourceCmsCIAttribute.setCiId(sourceCmsCI.getCiId());
        sourceCmsCIAttribute.setAttributeName(toCmsClazzAttribute.getAttributeName());
        sourceCmsCIAttribute.setDjValue(toCmsClazzAttribute.getDefaultValue());
        sourceCmsCIAttribute.setDfValue(toCmsClazzAttribute.getDefaultValue());

        log.info("Adding new attribute <toCmsClazzAttribute> {}",
            toCmsClazzAttribute.getAttributeName());
        // publish & add attribute
        // ciMapper.addCIAttributeAndPublish(sourceCmsCIAttribute);
        log.info("Added new attribute <toCmsClazzAttribute> {}",
            toCmsClazzAttribute.getAttributeName());

      }
      cmsCIAttributes_target.put(toCmsClazzAttribute.getAttributeName(), sourceCmsCIAttribute);

    }
    sourceCmsCI.setAttributes(cmsCIAttributes_target);
    return sourceCmsCI;

  }

  private Map<String, CmsCIAttribute> getCIAttrsMapForCiId(long ciId) {
    Map<String, CmsCIAttribute> cmsCIAttributeMap = new HashMap<String, CmsCIAttribute>();
    List<CmsCIAttribute> cmsCIAttributeList = ciMapper.getCIAttrs(ciId);
    for (CmsCIAttribute cmsCIAttribute : cmsCIAttributeList) {
      cmsCIAttributeMap.put(cmsCIAttribute.getAttributeName(), cmsCIAttribute);
    }

    return cmsCIAttributeMap;
  }


  private List<CmsCI> getTransformationSupportedCmsCiComponentsForPlatform(
      String nsForPlatformCiComponents) {

    Set<String> transformationSupportedClazzes =
        this.transformationSupportedCiClazzesConfigsMap.keySet();
    List<CmsCI> cmsCIList_transformationSupported = new ArrayList<CmsCI>();


    for (String fromClazz : transformationSupportedClazzes) {

      List<CmsCI> cmsCIList_fromCMSDB =
          ciMapper.getCIby3(nsForPlatformCiComponents, fromClazz, null, null);
      if (cmsCIList_fromCMSDB == null || cmsCIList_fromCMSDB.size() == 0) {
        log.warn("No Ci Component Found for <fromClazz> {} for <nsForPlatformCiComponents> {}",
            fromClazz, nsForPlatformCiComponents);
        continue;
      }

      // TODO: As of now supporting only required CiComponents, will add CI list once first phase is
      // complete. so adding only 1 Ci Items (cmsCIList_fromCMSDB.get(0))
      // change code from cmsCIList_transformationSupported.add(cmsCIList_fromCMSDB.get(0));
      // TO
      // cmsCIList_transformationSupported.add(cmsCIList_fromCMSDB);

      // TODO: Apache Cassandra pack for OneOps circuit by default have 2 types of user classes,
      // Need to weigh in while transforming that pack
      log.info("CiComponent set for transformation: {} ", gson.toJson(cmsCIList_fromCMSDB.get(0)));
      cmsCIList_transformationSupported.add(cmsCIList_fromCMSDB.get(0));
    }

    return cmsCIList_transformationSupported;


  }
}
