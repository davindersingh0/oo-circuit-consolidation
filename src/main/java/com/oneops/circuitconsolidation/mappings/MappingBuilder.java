package com.oneops.circuitconsolidation.mappings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.Gson;
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

  public void createCmCiAttributesMappings(String sourcePack, String targetPack) {


    Map<String, String> CiClazzesTransformationsMap =
        transformCiAttributesService.getTransformationSupportedCiClazzesConfigsMap();

    for (String sourceCircuitCmsClazzName : CiClazzesTransformationsMap.keySet()) {
      String targetCircuitCmsClazzName = CiClazzesTransformationsMap.get(sourceCircuitCmsClazzName);
      log.info(
          "Staring processing for <sourceCircuitCmsClazzName> {} , <targetCircuitCmsClazzName> {}",
          sourceCircuitCmsClazzName, targetCircuitCmsClazzName);

      Map<String, CmsClazzAttribute> sourceCmsClazzAttributesMap =
          getMdAttributesMap(sourceCircuitCmsClazzName);
      Map<String, CmsClazzAttribute> targetCmsClazzAttributesMap =
          getMdAttributesMap(targetCircuitCmsClazzName);
      // String sourcePack,String sourceClassname,
      List<CmCiAttributesActionMappings> list = generateCmCiAttributesActionMap(sourcePack,
          sourceCircuitCmsClazzName, sourceCmsClazzAttributesMap, targetPack,
          targetCircuitCmsClazzName, targetCmsClazzAttributesMap);

      log.info("list : " + gson.toJson(list));
    }



  }

  private Map<String, CmsClazzAttribute> getMdAttributesMap(String cmsClazzName) {
    CmsClazz cmsClazz = mdProcessor.getClazz(cmsClazzName);
    List<CmsClazzAttribute> cmsClazzAttributesList = cmsClazz.getMdAttributes();
    Map<String, CmsClazzAttribute> cmsClazzAttributesMap = new HashMap<String, CmsClazzAttribute>();

    for (CmsClazzAttribute cmsClazzAttribute : cmsClazzAttributesList) {
      cmsClazzAttributesMap.put(cmsClazzAttribute.getAttributeName(), cmsClazzAttribute);

    }

    return cmsClazzAttributesMap;

  }

  private List<CmCiAttributesActionMappings> generateCmCiAttributesActionMap(String sourcePack,
      String sourceClassname, Map<String, CmsClazzAttribute> sourceCmsClazzAttributesMap,
      String targetPack, String targetClassname,
      Map<String, CmsClazzAttribute> targetCmsClazzAttributesMap) {

    List<CmCiAttributesActionMappings> cmCiAttributesActionMappingsList =
        new ArrayList<CmCiAttributesActionMappings>();

    for (String sourceCircuitAttributeName : sourceCmsClazzAttributesMap.keySet()) {
      CmCiAttributesActionMappings cmCiAttributesActionMappings =
          new CmCiAttributesActionMappings();

      CmsClazzAttribute sourceCmsClazzAttribute =
          sourceCmsClazzAttributesMap.get(sourceCircuitAttributeName);
      if (targetCmsClazzAttributesMap.containsKey(sourceCircuitAttributeName)) {


        cmCiAttributesActionMappings.setAction("UPDATE_SOURCE_ATTRIBUTE_ID");
        cmCiAttributesActionMappings.setSourceCmsClazzAttributeMappings(sourcePack, sourceClassname,
            sourceCmsClazzAttribute);

        cmCiAttributesActionMappings.setTargetCmsClazzAttributeMappings(targetPack, targetClassname,
            targetCmsClazzAttributesMap.get(sourceCircuitAttributeName));

        targetCmsClazzAttributesMap.remove(sourceCircuitAttributeName);
        cmCiAttributesActionMappingsList.add(cmCiAttributesActionMappings);

      } else {
        cmCiAttributesActionMappings.setAction("DELETE_SOURCE_ATTRIBUTE_ID");
        cmCiAttributesActionMappings.setSourceCmsClazzAttributeMappings(sourcePack, sourceClassname,
            sourceCmsClazzAttribute);
        cmCiAttributesActionMappingsList.add(cmCiAttributesActionMappings);
        // deleteAttribute
      }
    }

    if (targetCmsClazzAttributesMap.size() > 0) {
      // setDefaults
      for (CmsClazzAttribute targetCmsClazzAttribute : targetCmsClazzAttributesMap.values()) {
        CmCiAttributesActionMappings cmCiAttributesActionMappings =
            new CmCiAttributesActionMappings();
        cmCiAttributesActionMappings.setAction("SET_DEFAULT_ATTRIBUTE_VALUE");
        cmCiAttributesActionMappings.setTargetCmsClazzAttributeMappings(targetPack, targetClassname,
            targetCmsClazzAttribute);
        cmCiAttributesActionMappingsList.add(cmCiAttributesActionMappings);
      }


    }
    log.info(
        "jsonified cmCiAttributesActionMappings: " + gson.toJson(cmCiAttributesActionMappingsList));
    return cmCiAttributesActionMappingsList;


  }

}
