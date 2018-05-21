package com.oneops.circuitconsolidation.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.oneops.circuitconsolidation.model.CiRelationModel;
import com.oneops.cms.cm.dal.CIMapper;
import com.oneops.cms.cm.domain.CmsCI;

public class ValidateComponentRelations {
  @Autowired
  CIMapper ciMapper;

  private final Logger log = LoggerFactory.getLogger(getClass());
  private Map<String, CmsCI> cmsCIComponentsMap = new HashMap<String, CmsCI>();


  public Map<String, CmsCI> getCmsCIComponentsMap() {
    return cmsCIComponentsMap;
  }


  public void setCiMapper(CIMapper ciMapper) {
    this.ciMapper = ciMapper;
  }


  public boolean validateComponentExists(CiRelationModel[] CiRelationsArr,
      String nsForPlatformCiComponents, String platformName, String ns) {


    Set<String> componentCiNamesSet = new HashSet<>();

    boolean isRequiredComponentExist = false;
    //populate componentCiNamesSet to optimize number of queries to database
    for (CiRelationModel ciRelation : CiRelationsArr) {
      componentCiNamesSet.add(ciRelation.getFromCi());
      componentCiNamesSet.add(ciRelation.getToCi());

    }

    
    for (String ciName : componentCiNamesSet) {
      if (ciName.equals("platform")) {
        isRequiredComponentExist = isCiExists(ns, platformName, "platform");
        continue;
      }
      // check if ciCicomponent exists in database
      isRequiredComponentExist = isCiExists(nsForPlatformCiComponents, ciName, ciName);
      if (!isRequiredComponentExist) {
        log.error("ciName: "+ciName +" does not exists for nsForPlatformCiComponents: " +nsForPlatformCiComponents);
        return false;
      }

    }
    return isRequiredComponentExist;
  }

  public boolean validateComponentsRelationExists() {


    return false;
  }

  private boolean isCiExists(String nsForPlatformCiComponents, String ciName, String hMapkey) {

    List<CmsCI> cmsCIList = ciMapper.getCIby3(nsForPlatformCiComponents, null, null, ciName);

    if (cmsCIList == null || cmsCIList.size() == 0) {
      log.error(ciName + "does not exists");
      return false;
    } else {
      // populate ciComponents for reuse
      this.cmsCIComponentsMap.put(hMapkey, cmsCIList.get(0));
      return true;
    }

  }
  


}
