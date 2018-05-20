package com.oneops.circuitconsolidation.service;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.util.IConstants;
import com.oneops.cms.cm.dal.CIMapper;
import com.oneops.cms.cm.domain.CmsCI;
import com.oneops.cms.cm.domain.CmsCIAttribute;
import com.oneops.cms.cm.service.CmsCmManager;
import com.oneops.cms.cm.service.CmsCmProcessor;

public class PlatformCiService {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  Gson gson;
  @Autowired
  CIMapper ciMapper;
  @Autowired
  CmsCmProcessor cmProcessor;
  @Autowired
  private CmsCmManager cmManager;
  
  
  public void setGson(Gson gson) {
    this.gson = gson;
  }

  public void setCiMapper(CIMapper ciMapper) {
    this.ciMapper = ciMapper;
  }

  public void setCmProcessor(CmsCmProcessor cmProcessor) {
    this.cmProcessor = cmProcessor;
  }

  public void setCmManager(CmsCmManager cmManager) {
    this.cmManager = cmManager;
  }

  public boolean transformPlatformPackSourceAttribute(String ns, String platformName) {
    CmsCI cmsCI=getPlatform(ns, platformName);
    if (cmsCI==null) {
      throw new UnsupportedOperationException("platform for <platformName> "+platformName +" <ns> "+ns+ " does not exists");
    }
    
    CmsCI platformCi = cmManager.getCiById(Long.valueOf(cmsCI.getCiId()));
    log.info("platformCi: "+gson.toJson(platformCi));
    Map<String, CmsCIAttribute> attributes = platformCi.getAttributes();
    CmsCIAttribute cmsCIAttribute = attributes.get("source");
    cmsCIAttribute.setDfValue("oneops");
    cmsCIAttribute.setDjValue("oneops");
    cmsCIAttribute.setComments(IConstants.comments);
    attributes.put("source", cmsCIAttribute);

    platformCi.setAttributes(attributes);
    
    CmsCI updatedCmsCI= cmProcessor.updateCI(platformCi);
    log.info("updated updatedCmsCI: "+updatedCmsCI);
    
    return true;
    
  }
  
  private CmsCI getPlatform(String ns, String ciName) {
    List<CmsCI> cmsCIList = ciMapper.getCIby3(ns, null, null, ciName);
    if (cmsCIList == null || cmsCIList.size() == 0) {
      return null;
    }
    return cmsCIList.get(0);
  }
  
  
}
