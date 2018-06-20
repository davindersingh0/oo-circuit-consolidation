package com.oneops.circuitconsolidation.util;

import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.oneops.circuitconsolidation.service.MappingsMDClazzCiAndCiAttributes;

public class MappingsMDClazzCiAndCiAttributesTest {


  private ApplicationContext context;
  private MappingsMDClazzCiAndCiAttributes mappingsMDClazzCiAndCiAttributes;
  
  
  @BeforeClass
  private void init() {
    CircuitConsolidationMain app = new CircuitConsolidationMain();
    app.loadApplicationContext();
    context = app.getContext();
    mappingsMDClazzCiAndCiAttributes= context.getBean(MappingsMDClazzCiAndCiAttributes.class);

  }
  
  @Test(enabled = true)
  private void testGetMDClazzMappingsForSourceAndTartgetPacks() {
    
    String ooPhase= IConstants.DESIGN_PHASE;
    
    mappingsMDClazzCiAndCiAttributes.createCiAndAttributesMappings(ooPhase);
    //TODO: add 2 pending features and populate data in table
    
    
    
  }
  
}
