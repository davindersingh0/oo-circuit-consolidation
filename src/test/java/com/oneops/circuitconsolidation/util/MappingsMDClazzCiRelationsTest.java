package com.oneops.circuitconsolidation.util;

import java.util.List;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.oneops.circuitconsolidation.model.CmsCIRelationAndRelationAttributesActionMappingsModel;
import com.oneops.circuitconsolidation.service.MappingsMDClazzCiRelations;

public class MappingsMDClazzCiRelationsTest {

  // NOTE: This is the main class being used to load transformation mappings for CI relations
  private ApplicationContext context;
  private MappingsMDClazzCiRelations mappingsMDClazzRelations;
  
  
  @BeforeClass
  private void init() {
    CircuitConsolidationMain app = new CircuitConsolidationMain();
    app.loadApplicationContext();
    context = app.getContext();
    mappingsMDClazzRelations= context.getBean(MappingsMDClazzCiRelations.class);

  }
  
  @Test(enabled = true)
  private void createCiRelationsMappings() {
    
    String packSource = "oneops";
    String packName = "apache_cassandra";
    String ooPhase = IConstants.DESIGN_PHASE;
    String deplomentType = IConstants.DEPLOYMENT_TYPE_SINGLE;
    
    List<CmsCIRelationAndRelationAttributesActionMappingsModel> ciRelationsMappings_designPhase= mappingsMDClazzRelations.createCiRelationsMappings(packSource, packName, ooPhase, deplomentType);
    mappingsMDClazzRelations.publishCmsCIRelationAndRelationAttributesActionMappings(ooPhase, ciRelationsMappings_designPhase);
 
     ooPhase = IConstants.TRANSITION_PHASE;
    
    List<CmsCIRelationAndRelationAttributesActionMappingsModel> ciRelationsMappings_transitionPhase= mappingsMDClazzRelations.createCiRelationsMappings(packSource, packName, ooPhase, deplomentType);
    mappingsMDClazzRelations.publishCmsCIRelationAndRelationAttributesActionMappings(ooPhase, ciRelationsMappings_transitionPhase);
 
    
  
  }
  
  
}
