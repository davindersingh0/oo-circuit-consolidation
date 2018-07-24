package com.oneops.circuitconsolidation.util;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.mappings.CmsCiAndCmsCiAttributesActionMappingsModel;
import com.oneops.circuitconsolidation.service.MappingsMDClazzCiAndCiAttributes;

public class MappingsMDClazzCiAndCiAttributesTest {

  private final Logger log = LoggerFactory.getLogger(MappingsMDClazzCiAndCiAttributesTest.class);
  
  private ApplicationContext context;
  private MappingsMDClazzCiAndCiAttributes mappingsMDClazzCiAndCiAttributes;
  Gson gson;
  
  @BeforeClass
  private void init() {
    CircuitConsolidationMain app = new CircuitConsolidationMain();
    app.loadApplicationContext();
    context = app.getContext();
    gson=context.getBean(Gson.class);
    mappingsMDClazzCiAndCiAttributes= context.getBean(MappingsMDClazzCiAndCiAttributes.class);

  }
  
  @Test(enabled = true)
  private void testGetMDClazzMappingsForSourceAndTartgetPacks() {
    
    String ooPhase_Design= IConstants.DESIGN_PHASE;
    
    List<CmsCiAndCmsCiAttributesActionMappingsModel> designPhaseMappings= mappingsMDClazzCiAndCiAttributes.createCiAndAttributesMappings(ooPhase_Design);
    log.info("designPhaseMappings: "+gson.toJson(designPhaseMappings));
    //TODO: populate data in table
    
    mappingsMDClazzCiAndCiAttributes.publishCmsCIAndCmsCIAttributesActionMappings(ooPhase_Design, designPhaseMappings);
    
    String ooPhase_Transition= IConstants.TRANSITION_PHASE;
    
    List<CmsCiAndCmsCiAttributesActionMappingsModel> transitionPhaseMappings= mappingsMDClazzCiAndCiAttributes.createCiAndAttributesMappings(ooPhase_Transition);
    log.info("transitionPhaseMappings: "+gson.toJson(transitionPhaseMappings));
    //TODO: populate data in table
    
    mappingsMDClazzCiAndCiAttributes.publishCmsCIAndCmsCIAttributesActionMappings(ooPhase_Transition, transitionPhaseMappings);
    
    
  // change nagio scripts : 
    // Delete CI - shall delete CI attributes too !
    
  }
  
}
