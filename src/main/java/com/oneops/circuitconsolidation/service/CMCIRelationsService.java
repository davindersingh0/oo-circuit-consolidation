package com.oneops.circuitconsolidation.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.Gson;
import com.oneops.cms.cm.domain.CmsCI;
import com.oneops.cms.cm.domain.CmsCIRelation;
import com.oneops.cms.cm.domain.CmsCIRelationAttribute;
import com.oneops.cms.cm.service.CmsCmProcessor;
import com.oneops.cms.md.domain.CmsRelation;
import com.oneops.cms.md.domain.CmsRelationAttribute;
import com.oneops.cms.md.service.CmsMdProcessor;
import com.oneops.cms.util.CmsUtil;

public class CMCIRelationsService {

  @Autowired
  CmsCmProcessor cmProcessor;
  @Autowired
  private CmsMdProcessor mdProcessor;
  @Autowired
  Gson gson;
  
  private final Logger log = LoggerFactory.getLogger(getClass());

  
  public CmsCIRelation createRelations(CmsCI fromCmsCI, CmsCI toCmsCI, String relationName,
      Map<String, CmsCIRelationAttribute> CmsCIRelationAttributeMap, String nsPath) throws IOException {
    // propagate_to attrib is missing in original compute, need to work on it
    log.info("creating CmsCI relation name {} " ,relationName );
    CmsCIRelation cmsCIRelation = new CmsCIRelation();
    // create FROM Relation

    cmsCIRelation.setNsPath(nsPath);
    cmsCIRelation.setRelationName(relationName);


    cmsCIRelation.setFromCiId(fromCmsCI.getCiId());
    cmsCIRelation.setToCiId(toCmsCI.getCiId());

    cmsCIRelation.setAttributes(CmsCIRelationAttributeMap);
    cmsCIRelation.setComments(CmsUtil.generateRelComments(fromCmsCI.getCiName(),
        fromCmsCI.getCiClassName(), toCmsCI.getCiName(), toCmsCI.getCiClassName()));
    log.info("created CmsCI relation name {} " ,relationName );
    return cmProcessor.createRelation(cmsCIRelation);


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

  public Map<String, CmsCIRelationAttribute> getDefaultCmsRelationAttributes(String relationName) {

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
  
}
