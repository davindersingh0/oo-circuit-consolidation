package com.oneops.circuitconsolidation.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.exceptions.UnSupportedOperation;
import com.oneops.circuitconsolidation.util.CircuitconsolidationUtil;
import com.oneops.circuitconsolidation.util.IConstants;
import com.oneops.cms.cm.dal.CIMapper;
import com.oneops.cms.cm.domain.CmsCI;
import com.oneops.cms.cm.domain.CmsCIAttribute;
import com.oneops.cms.cm.domain.CmsCIRelation;
import com.oneops.cms.cm.domain.CmsCIRelationAttribute;
import com.oneops.cms.cm.service.CmsCmProcessor;
import com.oneops.cms.md.domain.CmsClazz;
import com.oneops.cms.md.domain.CmsClazzAttribute;
import com.oneops.cms.md.service.CmsMdProcessor;

public class OsComponentService {

  private final Logger log = LoggerFactory.getLogger(getClass());


  @Autowired
  Gson gson;
  @Autowired
  CIMapper ciMapper;
  @Autowired
  CmsCmProcessor cmProcessor;
  @Autowired
  MDClassesService mdClassesService;
  @Autowired
  CMCIRelationsService cmciRelationsService;
  @Autowired
  private CmsMdProcessor mdProcessor;

  public void setGson(Gson gson) {
    this.gson = gson;
  }

  public void setCiMapper(CIMapper ciMapper) {
    this.ciMapper = ciMapper;
  }

  public void setCmProcessor(CmsCmProcessor cmProcessor) {
    this.cmProcessor = cmProcessor;
  }

  public void setMdClassesService(MDClassesService mdClassesService) {
    this.mdClassesService = mdClassesService;
  }

  public void setCmciRelationsService(CMCIRelationsService cmciRelationsService) {
    this.cmciRelationsService = cmciRelationsService;
  }

  public void setMdProcessor(CmsMdProcessor mdProcessor) {
    this.mdProcessor = mdProcessor;
  }



  public CmsCI createOsComponentFromCompute(String ns, String platformName, String ooPhase,
      String envName) {
    CmsCI osComponent = new CmsCI();

    Map<String, CmsCIAttribute> osComponentCmsCIAttributes = new HashMap<String, CmsCIAttribute>();
    try {

      String nsForPlatformCiComponents =
          CircuitconsolidationUtil.getnsForPlatformCiComponents(ns, platformName, ooPhase, envName);
      log.info(
          "create OS component for ns {}, platformName {}, ooPhase {}, envName {}, nsForPlatformCiComponents {}: ",
          ns, platformName, ooPhase, envName, nsForPlatformCiComponents);

      List<CmsCI> cmsCIList_os =
          ciMapper.getCIby3(nsForPlatformCiComponents, null, null, IConstants.OS_CI_NAME);

      if (cmsCIList_os != null && cmsCIList_os.size() > 0) {
        log.warn("os component already exists!!");
        return cmsCIList_os.get(0);
      }


      List<CmsCI> cmsCIList =
          ciMapper.getCIby3(nsForPlatformCiComponents, null, null, IConstants.COMPUTE_CI_NAME);
      if (cmsCIList == null || cmsCIList.size() == 0) {
        log.error("compute does not exists, can not create OS component for <ns> " + ns
            + ", <platformName> " + platformName + ", <ooPhase> " + ooPhase + ", <envName> "
            + envName);
        throw new UnSupportedOperation(
            "compute does not exists, can not create OS component for <ns> " + ns
            + ", <platformName> " + platformName + ", <ooPhase> " + ooPhase + ", <envName> "
            + envName);

      }

      CmsCI computeComponent = cmsCIList.get(0);
      log.info("computeComponent: " + gson.toJson(computeComponent));

      osComponent.setCiName(IConstants.OS_CI_NAME);
      osComponent.setCiClassName(
          mdClassesService.getMdClassNameForCIinPhase(IConstants.OS_CI_NAME, ooPhase));
      osComponent.setNsPath(computeComponent.getNsPath());
      osComponent.setComments(IConstants.comments);
      // osComponent.setUpdatedBy(updatedBy); :TODO: check if this field is mandatory

      CmsClazz cmsClazz = mdProcessor
          .getClazz(mdClassesService.getMdClassNameForCIinPhase(IConstants.OS_CI_NAME, ooPhase));
      List<CmsClazzAttribute> cmsClazzAttributeList = cmsClazz.getMdAttributes();
      Map<String, CmsCIAttribute> computeCmsCIAttributeMap =
          getCIAttrsMap(computeComponent.getCiId());

      for (CmsClazzAttribute cmsClazzAttribute : cmsClazzAttributeList) {
        String attributeName = cmsClazzAttribute.getAttributeName();
        log.info("processing attribute <cmsClazzAttribute>: {}", attributeName);

        /*
         * if (attributeName.equals("env_vars") || attributeName.equals("features")) { continue;
         * }:TODO: delete if platform design works fine after this change
         */
        CmsCIAttribute cmsCIAttribute = new CmsCIAttribute();
        cmsCIAttribute.setAttributeId(cmsClazzAttribute.getAttributeId());
        cmsCIAttribute.setAttributeName(attributeName);

        CmsCIAttribute computeComponentCmsCIAttribute = computeCmsCIAttributeMap.get(attributeName);


        if (computeComponentCmsCIAttribute == null) { // attribute does not exists in compute
          // component, set default values

          cmsCIAttribute.setDjValue(cmsClazzAttribute.getDefaultValue());
          cmsCIAttribute.setDfValue(cmsClazzAttribute.getDefaultValue());
        } else {

          cmsCIAttribute.setDjValue(computeComponentCmsCIAttribute.getDjValue());
          cmsCIAttribute.setDfValue(computeComponentCmsCIAttribute.getDfValue());
        }
        osComponentCmsCIAttributes.put(cmsClazzAttribute.getAttributeName(), cmsCIAttribute);

      }
      osComponent.setAttributes(osComponentCmsCIAttributes);
      log.info("creating os component : " + gson.toJson(osComponent));

      osComponent = cmProcessor.createCI(osComponent);
      log.info("cms DB response for creating os component osCi: " + gson.toJson(osComponent));
      return osComponent;


    } catch (Exception e) {
      log.error("Error while creating OS Component : {}", e);
      return null;
    }

  }

  private Map<String, CmsCIAttribute> getCIAttrsMap(long ciId) {
    Map<String, CmsCIAttribute> cmsCIAttributeMap = new HashMap<String, CmsCIAttribute>();
    List<CmsCIAttribute> cmsCIAttributeList = ciMapper.getCIAttrs(ciId);
    for (CmsCIAttribute cmsCIAttribute : cmsCIAttributeList) {
      cmsCIAttributeMap.put(cmsCIAttribute.getAttributeName(), cmsCIAttribute);
    }

    return cmsCIAttributeMap;
  }



  public boolean createOsComponentRelations(String ns, String platformName, String ooPhase,
      String envName) {

    CmsCI platformCi = new CmsCI();
    String nsForPlatformCiComponents =
        CircuitconsolidationUtil.getnsForPlatformCiComponents(ns, platformName, ooPhase, envName);


    List<CmsCI> cmsCIList_compute =
        ciMapper.getCIby3(nsForPlatformCiComponents, null, null, "compute");

    if (cmsCIList_compute == null || cmsCIList_compute.size() == 0) {
      log.error("os component relation can not be set because compute component does not exists");
      return false;
    }


    CmsCI computeCmsCI = cmsCIList_compute.get(0);

    List<CmsCI> cmsCIList_os = ciMapper.getCIby3(nsForPlatformCiComponents, null, null, "os");

    if (cmsCIList_os == null || cmsCIList_os.size() == 0) {
      log.error("os component relation can not be set because os component does not exists");
      return false;
    }
    CmsCI osCmsCI = cmsCIList_os.get(0);

    List<CmsCI> cmsCIList_volume =
        ciMapper.getCIby3(nsForPlatformCiComponents, null, null, "volume");

    if (cmsCIList_volume == null || cmsCIList_volume.size() == 0) {
      log.error("volume component relation can not be set because os component does not exists");
      return false;
    }
    CmsCI volumeCmsCI = cmsCIList_volume.get(0);


    try {

      String relationName = "catalog.DependsOn";
      Map<String, CmsCIRelationAttribute> defautAttributes =
          cmciRelationsService.getDefaultCmsRelationAttributes(relationName);
      defautAttributes.remove("source");
      defautAttributes.remove("propagate_to");
      defautAttributes.get("converge").setDfValue("false");// was set true
      defautAttributes.get("converge").setDjValue("false");// was set true

      log.info("relationName {}, defautAttributes {} ", relationName, defautAttributes);

      CmsCIRelation osToComputeCmsCIRelation = cmciRelationsService.createRelations(osCmsCI,
          computeCmsCI, "catalog.DependsOn", defautAttributes, nsForPlatformCiComponents);
      log.info("osToComputeCmsCIRelation response: " + osToComputeCmsCIRelation);

      defautAttributes.get("converge").setDfValue("false");
      defautAttributes.get("converge").setDjValue("false");

      CmsCIRelation volumeToOsCmsCIRelation = cmciRelationsService.createRelations(volumeCmsCI,
          osCmsCI, relationName, defautAttributes, nsForPlatformCiComponents);
      log.info("volumeToOsCmsCIRelation response: " + volumeToOsCmsCIRelation);

      relationName = "base.Requires";
      defautAttributes = cmciRelationsService.getDefaultCmsRelationAttributes(relationName);
      defautAttributes.remove("priority");

      defautAttributes.get("template").setDfValue("os");
      defautAttributes.get("template").setDjValue("os");

      defautAttributes.get("services").setDfValue("compute,dns,*mirror,*ntp,*windows-domain");
      defautAttributes.get("services").setDjValue("compute,dns,*mirror,*ntp,*windows-domain");


      CmsCIRelation platformToOsCmsCIRelation = cmciRelationsService.createRelations(platformCi,
          osCmsCI, relationName, defautAttributes, nsForPlatformCiComponents);


      log.info("platformToOsCmsCIRelation response: " + platformToOsCmsCIRelation);
      log.info("processing for OS to compute Relation complete: ");
      return true;


    } catch (IOException e) {
      log.error("unable to create osToComputeCmsCIRelation, {}", e);
      e.printStackTrace();
      return false;

    }

  }

}
