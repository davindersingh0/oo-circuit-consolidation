package com.oneops.circuitconsolidation.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.model.CmsCiTransformationModel;
import com.oneops.cms.cm.dal.CIMapper;
import com.oneops.cms.cm.domain.CmsCI;

public class CiMDClazzMappingsUtil {

  private final Logger log = LoggerFactory.getLogger(CiMDClazzMappingsUtil.class);

  @Autowired
  private Gson gson;

  @Autowired
  private CIMapper ciMapper;


  public void setGson(Gson gson) {
    this.gson = gson;
  }


  public void setCiMapper(CIMapper ciMapper) {
    this.ciMapper = ciMapper;
  }


  public List<CmsCiTransformationModel> getMDClazzMappingsForSourceAndTartgetPacks(
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

        cmsCiTransformationModel.setCiAction(IConstants.TRANSFORM_CMSCI);
        cmsCiTransformationModel.setSourceCiClazzName(sourcePackCmsCI.getCiClassName());
        cmsCiTransformationModel.setTargetCiClazzName(targetPackCmsCI.getCiClassName());
      } else {
        log.info(
            "corresponding mapping not found in target pack, mark CI for deletion <sourceClazzesMappingKey>: "
                + sourceClazzesMappingKey);
        cmsCiTransformationModel.setCiAction(IConstants.DELETE_CMSCI);
        cmsCiTransformationModel.setSourceCiClazzName(sourcePackCmsCI.getCiClassName());

      }

      mapppingsList.add(cmsCiTransformationModel);
    }

    for (String targetClazzesMappingKey : targetClazzesTrasnformedKeySetmap.keySet()) {

      log.info(
          "check mapping in source pack for <targetClazzesMappingKey>:" + targetClazzesMappingKey);

      CmsCI sourcePackCmsCI = sourceClazzesTrasnformedKeySetmap.get(targetClazzesMappingKey);
      CmsCI targetPackCmsCI = targetClazzesTrasnformedKeySetmap.get(targetClazzesMappingKey);

      if (sourcePackCmsCI == null) {
        CmsCiTransformationModel cmsCiTransformationModel = new CmsCiTransformationModel();
        log.info(
            "corresponding mapping not found in source pack for <targetClazzesMappingKey>:{} , mark CMSCI for create",
            targetClazzesMappingKey);

        cmsCiTransformationModel.setCiAction(IConstants.CREATE_CMSCI);
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


}
