package com.oneops.circuitconsolidation.mappings;

import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.oneops.circuitconsolidation.util.CircuitConsolidationMain;

public class MappingBuilderTest {

  
  private ApplicationContext context;
  
  @BeforeClass
  private void init() {
    CircuitConsolidationMain app = new CircuitConsolidationMain();
    app.loadApplicationContext();
    context= app.getContext();
    
    
  }
  
  
  @Test
  private void testMappings() {
    
   String sourcePack="walmartlabs-apache_cassandra";
   String targetPack="oneops-apache_cassandra";
    MappingBuilder mappingBuilder=context.getBean(MappingBuilder.class);
    
    mappingBuilder.createCmCiAttributesMappings(sourcePack, targetPack);
    
    
    
  }
  
  
  
}
