package com.oneops.circuitconsolidation.mappings.dal;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.oneops.circuitconsolidation.mappings.CiClazzRelationMappingModel;
import com.oneops.circuitconsolidation.mappings.CmsCiAndCmsCiAttributesActionMappingsModel;
import com.oneops.circuitconsolidation.model.CmsCIRelationAndRelationAttributesActionMappingsModel;
import com.oneops.circuitconsolidation.model.CmsCIRelationModel;
import com.oneops.cms.cm.domain.CmsCI;
import com.oneops.cms.cm.domain.CmsCIAttribute;

public interface OOConsolidationMapper {

  // Begin: Added  deleteCIAttribute for circuit consolidation

  void deleteCIAttribute(CmsCIAttribute attr);
  void cmUpdateCiClassidClassnameGoid(CmsCI ci);
  
  
  // End: Added  deleteCIAttribute for circuit consolidation
  
  public void populateCmsCiAndCmsCiAttributesActionMappings(CmsCiAndCmsCiAttributesActionMappingsModel cmsCiAndCmsCiAttributesActionMappings);
  public void populateCmsCiAndCmsCiAttributesActionMappingsList(@Param("cmsCiAndCmsCiAttributesActionMappingsList") List<CmsCiAndCmsCiAttributesActionMappingsModel> cmsCiAndCmsCiAttributesActionMappingsList);
  public void populateCiClazzRelationMappings(CiClazzRelationMappingModel ciClazzRelationMapping);
  
  //public List<CmsCIRelationModel> getCIRelations(CiClazzRelationMappingModel ciClazzRelationMappingModel);
  
  public List<CmsCIRelationModel> getCIRelations(@Param("nsPath") String nsPath, @Param("relationName") String relationName, @Param("shortRelName") String shortRelName, @Param("fromClazzName") String fromClazzName, @Param("fromShortClazzName") String fromShortClazzName, @Param("toClazzName") String toClazzName, @Param("toShortClazzName") String toShortClazzName);

  
  public void populateCmsCIRelationAndRelationAttributesActionMappings(CmsCIRelationAndRelationAttributesActionMappingsModel cmsCIRelationAndRelationAttributesActionMappings);
  
}
