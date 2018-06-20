package com.oneops.circuitconsolidation.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.mappings.CiRelationsMappingsTest;
import com.oneops.cms.cm.dal.CIMapper;
import com.oneops.cms.cm.domain.CmsCI;

/**
 * @author dsing17 This class is created to generate CMSCi mapping Clazzes data for populating
 *         ci_clazzes_transformation_map.json file
 */
public class CMSDalUtil_MDClazzMappings2 {



  private final Logger log = LoggerFactory.getLogger(CiRelationsMappingsTest.class);

  private ApplicationContext context;
  private Gson gson = new Gson();
  private CIMapper ciMapper;

  @BeforeClass
  private void init() {
    CircuitConsolidationMain app = new CircuitConsolidationMain();
    app.loadApplicationContext();
    context = app.getContext();
    ciMapper = context.getBean(CIMapper.class);

  }


  @Test(enabled = true)
  private void getAllCIsForNsPath() {

    String ooPhase = IConstants.DESIGN_PHASE;
    // String ooPhase = IConstants.TRANSITION_PHASE;
    String envName = "dev";


    String ns_sourcePack = "/TestOrg2/ms-wmtlabs-a-cass";
    String platformName_sourcePack = "cass";
    String nsForPlatformCiComponents_sourcePack = CircuitconsolidationUtil
        .getnsForPlatformCiComponents(ns_sourcePack, platformName_sourcePack, ooPhase, envName);


    String ns_targetPack = "/TestOrg2/ms-oneops-a-cass";
    String platformName_targetPack = "oneops-apache-cassandra";
    String nsForPlatformCiComponents_targetPack = CircuitconsolidationUtil
        .getnsForPlatformCiComponents(ns_targetPack, platformName_targetPack, ooPhase, envName);


    List<CmsCiTransformationModel> mapppings = getMDClazzMappingsForSourceAndTartgetPacks(
        nsForPlatformCiComponents_sourcePack, nsForPlatformCiComponents_targetPack);

    log.info("mapppings for {} phase : {}", ooPhase, gson.toJson(mapppings));


  }



  private List<CmsCiTransformationModel> getMDClazzMappingsForSourceAndTartgetPacks(
      String nsForPlatformCiComponents_sourcePack, String nsForPlatformCiComponents_targetPack) {

    List<CmsCI> cmsCIListin_sourcePack =
        ciMapper.getCIby3(nsForPlatformCiComponents_sourcePack, null, null, null);
    log.info("cmsCIListin_sourcePack: " + gson.toJson(cmsCIListin_sourcePack));

    List<CmsCI> cmsCIListin_targetPack =
        ciMapper.getCIby3(nsForPlatformCiComponents_targetPack, null, null, null);
    log.info("cmsCIListin_targetPack: " + gson.toJson(cmsCIListin_targetPack));


    Map<String, CmsCI> supportedClazzesMap_sourcePack =
        getSupportedClazzesMap(cmsCIListin_sourcePack);
    Map<String, CmsCI> supportedClazzesMap_targetPack =
        getSupportedClazzesMap(cmsCIListin_targetPack);

    List<CmsCiTransformationModel> mapppings =
        getMappings(supportedClazzesMap_sourcePack, supportedClazzesMap_targetPack);

    return mapppings;
  }


  private List<CmsCiTransformationModel> getMappings(
      Map<String, CmsCI> supportedClazzesMap_sourcePack,
      Map<String, CmsCI> supportedClazzesMap_targetPack) {

    List<CmsCiTransformationModel> mapppingsList = new ArrayList<CmsCiTransformationModel>();


    Map<String, CmsCI> sourceClazzesTrasnformedKeySetmap =
        getClazzesMapForComparison(supportedClazzesMap_sourcePack);
    Map<String, CmsCI> targetClazzesTrasnformedKeySetmap =
        getClazzesMapForComparison(supportedClazzesMap_targetPack);



    for (String sourceClazzesMappingKey : sourceClazzesTrasnformedKeySetmap.keySet()) {

      log.info(
          "check mapping in target pack for <sourceClazzesMappingKey>:" + sourceClazzesMappingKey);
      CmsCiTransformationModel cmsCiTransformationModel = new CmsCiTransformationModel();

      CmsCI sourcePackCmsCI = sourceClazzesTrasnformedKeySetmap.get(sourceClazzesMappingKey);
      CmsCI targetPackCmsCI = targetClazzesTrasnformedKeySetmap.get(sourceClazzesMappingKey);

      if (targetPackCmsCI != null) {
        log.info("corresponding mapping found in target pack for <sourceClazzesMappingKey>: "
            + sourceClazzesMappingKey);

        cmsCiTransformationModel.setCiAction("TRANSFORM_CMSCI");
        cmsCiTransformationModel.setSourceCiClazzName(sourcePackCmsCI.getCiClassName());
        cmsCiTransformationModel.setTargetCiClazzName(targetPackCmsCI.getCiClassName());
      } else {
        log.info(
            "corresponding mapping not found in target pack, mark CI for deletion <sourceClazzesMappingKey>: "
                + sourceClazzesMappingKey);
        cmsCiTransformationModel.setCiAction("DELETE_CMSCI");
        cmsCiTransformationModel.setSourceCiClazzName(sourcePackCmsCI.getCiClassName());

      }

      mapppingsList.add(cmsCiTransformationModel);
    }

    for (String targetClazzesMappingKey : targetClazzesTrasnformedKeySetmap.keySet()) {
      Map<String, String> clazzesMap = new HashMap<String, String>();
      log.info(
          "check mapping in source pack for <targetClazzesMappingKey>:" + targetClazzesMappingKey);

      CmsCI sourcePackCmsCI = sourceClazzesTrasnformedKeySetmap.get(targetClazzesMappingKey);
      CmsCI targetPackCmsCI = targetClazzesTrasnformedKeySetmap.get(targetClazzesMappingKey);

      if (sourcePackCmsCI == null) {
        CmsCiTransformationModel cmsCiTransformationModel = new CmsCiTransformationModel();
        log.info(
            "corresponding mapping not found in source pack for <targetClazzesMappingKey>:{} , mark CMSCI for create",
            targetClazzesMappingKey);
        clazzesMap.put(targetPackCmsCI.getCiClassName(), "CREATE_CMSCI");

        cmsCiTransformationModel.setCiAction("CREATE_CMSCI");
        cmsCiTransformationModel.setTargetCiClazzName(targetPackCmsCI.getCiClassName());

        mapppingsList.add(cmsCiTransformationModel);
        continue;
      }

    }


    return mapppingsList;
  }


  private Map<String, CmsCI> getClazzesMapForComparison(Map<String, CmsCI> supportedClazzesMap) {
    Map<String, CmsCI> clazzesMapForComparison = new HashMap<String, CmsCI>();
    Set<String> inputKeySet = supportedClazzesMap.keySet();

    for (String keyStr : inputKeySet) {
      String originalKey = keyStr;
      String transformedKey = getMappingKey(keyStr);
      clazzesMapForComparison.put(transformedKey, supportedClazzesMap.get(originalKey));

    }
    log.info(
        "transformed KeySet Map <clazzesMapForComparison>" + gson.toJson(clazzesMapForComparison));
    return clazzesMapForComparison;
  }


  private Map<String, CmsCI> getSupportedClazzesMap(List<CmsCI> cmsCIList) {
    Map<String, CmsCI> supportedClazzesHashMap = new HashMap<String, CmsCI>();

    for (CmsCI cmsCI : cmsCIList) {
      String clazzName = cmsCI.getCiClassName();
      supportedClazzesHashMap.put(clazzName, cmsCI);

    }
    return supportedClazzesHashMap;
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

  private class CmsCiTransformationModel {

    String ciAction;
    String SourceCiClazzName;
    String targetCiClazzName;

    public String getCiAction() {
      return ciAction;
    }

    public void setCiAction(String ciAction) {
      this.ciAction = ciAction;
    }

    public String getSourceCiClazzName() {
      return SourceCiClazzName;
    }

    public void setSourceCiClazzName(String sourceCiClazzName) {
      SourceCiClazzName = sourceCiClazzName;
    }

    public String getTargetCiClazzName() {
      return targetCiClazzName;
    }

    public void setTargetCiClazzName(String targetCiClazzName) {
      this.targetCiClazzName = targetCiClazzName;
    }



  }

}
