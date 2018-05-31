package com.oneops.circuitconsolidation.mappings.dal;
import com.oneops.cms.cm.domain.CmsCI;
import com.oneops.cms.cm.domain.CmsCIAttribute;

public interface OOConsolidationMapper {

  // Begin: Added  deleteCIAttribute for circuit consolidation
  //returns CiId of  :TODO: review the function for return statement
  void deleteCIAttribute(CmsCIAttribute attr);
  void cmUpdateCiClassidClassnameGoid(CmsCI ci);
  
  
  // End: Added  deleteCIAttribute for circuit consolidation
  
  CmsCI getCIById(long id);


  
  
}
