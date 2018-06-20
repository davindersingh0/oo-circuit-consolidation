package com.oneops.circuitconsolidation.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.exceptions.UnSupportedOperation;
import com.oneops.circuitconsolidation.mappings.dal.OOConsolidationMapper;
import com.oneops.circuitconsolidation.model.CmsCIRelationModel;
import com.oneops.cms.cm.dal.CIMapper;
import com.oneops.cms.cm.domain.CmsCIRelationAttribute;

public class PackDefinitionUtil {

  private final Logger log = LoggerFactory.getLogger(PackDefinitionUtil.class);

  @Autowired
  private Gson gson;
  @Autowired
  private OOConsolidationMapper ooConsolidationMapper;
  @Autowired
  private CIMapper ciMapper;


  public void setGson(Gson gson) {
    this.gson = gson;
  }



  public void setOoConsolidationMapper(OOConsolidationMapper ooConsolidationMapper) {
    this.ooConsolidationMapper = ooConsolidationMapper;
  }

  public void setCiMapper(CIMapper ciMapper) {
    this.ciMapper = ciMapper;
  }

  private Map<String, CmsCIRelationAttribute> populateCmsCIRelationAttributesInPackDefinition(
      long relationId) {
    Map<String, CmsCIRelationAttribute> cmsCIRelationAttributeMap =
        new HashMap<String, CmsCIRelationAttribute>();
    List<CmsCIRelationAttribute> CmsCIRelationAttributeList =
        ciMapper.getCIRelationAttrs(relationId);

    for (CmsCIRelationAttribute cmsCIRelationAttribute : CmsCIRelationAttributeList) {
      cmsCIRelationAttributeMap.put(cmsCIRelationAttribute.getAttributeName(),
          cmsCIRelationAttribute);
    }

    return cmsCIRelationAttributeMap;

  }

  @Deprecated
  private Map<String, CmsCIRelationModel> getCmsCIRelationsInPackDefinition(
      String nsForPackDefinition, String ooPhase) {


    List<CmsCIRelationModel> cmsCIRelationsListForNsForPackDefination = ooConsolidationMapper
        .getCIRelations(nsForPackDefinition, null, null, null, null, null, null);

    log.info("cmsCIRelationsForNsForPackDefination: "
        + gson.toJson(cmsCIRelationsListForNsForPackDefination));

    Map<String, CmsCIRelationModel> cmsCIRelationTypesForPackDefination =
        cmsCIRelationTypesForPackDefination(cmsCIRelationsListForNsForPackDefination, ooPhase);

    return cmsCIRelationTypesForPackDefination;

  }

  private Map<String, CmsCIRelationModel> cmsCIRelationTypesForPackDefination(
      List<CmsCIRelationModel> cmsCIRelationList, String ooPhase) {
    Map<String, CmsCIRelationModel> cmsCIRelationTypes = new TreeMap<String, CmsCIRelationModel>();

    for (CmsCIRelationModel cmsCIRelation : cmsCIRelationList) {

      String key = createRelationNameKey(cmsCIRelation.getRelationName(), ooPhase) + "|"
          + getMappingKey(cmsCIRelation.getFromCiClazz()) + "|"
          + getMappingKey(cmsCIRelation.getToCiClazz());

      cmsCIRelation.setAttributes(
          populateCmsCIRelationAttributesInPackDefinition(cmsCIRelation.getCiRelationId()));
      cmsCIRelationTypes.put(key, cmsCIRelation);

    }
    return cmsCIRelationTypes;
  }

  private String createRelationNameKey(String relationName, String ooPhase) {

    String str = relationName.toLowerCase();
    if (str.contains("requires")) {
      switch (ooPhase) {
        case IConstants.DESIGN_PHASE:

          relationName = "base.Requires";
          break;
        case IConstants.TRANSITION_PHASE:

          relationName = "manifest.Requires";
          break;
        case IConstants.OPERATE_PHASE:
          log.error("ooPhase {} not supported", ooPhase);
          throw new UnSupportedOperation(ooPhase + " not supported");

      }

    } else {

      relationName = relationName.substring("mgmt.".length(), relationName.length());
    }
    return relationName;

  }


  public Map<String, CmsCIRelationModel> getCiRelationsFromPackDefinition(String packSource,
      String packName, String ooPhase, String deplomentType) {

    String nsForPackDefinition = CircuitconsolidationUtil.getnsForPackDefinition(packSource,
        packName, ooPhase, deplomentType);

    log.info("nsForPackDefinition: " + nsForPackDefinition);
    List<CmsCIRelationModel> cmsCIRelationsListForNsForPackDefination = ooConsolidationMapper
        .getCIRelations(nsForPackDefinition, null, null, null, null, null, null);

    log.info("cmsCIRelationsForNsForPackDefination: "
        + gson.toJson(cmsCIRelationsListForNsForPackDefination));

    return cmsCIRelationTypesForPackDefination(cmsCIRelationsListForNsForPackDefination, ooPhase);
  }
  
  public String getMappingKey(String str) {

    String[] strArr = str.split("\\.");

    String prefix = strArr[1];
    String suffix = strArr[strArr.length - 1];

    String[] suffixArr = suffix.split("_");
    String refinedSuffix = suffixArr[suffixArr.length - 1];

    String key = prefix + refinedSuffix;
    return key.toLowerCase();

  }
  
}
