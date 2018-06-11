package com.oneops.circuitconsolidation.mappings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.util.CircuitConsolidationMain;
import com.oneops.circuitconsolidation.util.CircuitconsolidationUtil;
import com.oneops.circuitconsolidation.util.IConstants;

public class CiRelationsMappingsTest {

  
  private final Logger log = LoggerFactory.getLogger(CiRelationsMappingsTest.class);

  private ApplicationContext context;
  private Gson gson = new Gson();

  @BeforeClass
  private void init() {
    CircuitConsolidationMain app = new CircuitConsolidationMain();
    app.loadApplicationContext();
    context = app.getContext();


  }
  
  @Test(enabled = false)
  private void testMappingsConfigFile() throws IOException {
    

    String fileContents=CircuitconsolidationUtil.getFileContent(IConstants.CI_RELATIONS_BLUEPRINT_FILE);
    log.info("fileContents: "+fileContents);
    
    CiClazzRelationMappingModel[] CiClazzRelationModelArr = gson.fromJson(fileContents, CiClazzRelationMappingModel[].class);
    List <CiClazzRelationMappingModel>expectedList=Arrays.asList(CiClazzRelationModelArr);
    CiRelationsMappings ciRelationsMappings = context.getBean(CiRelationsMappings.class);
    
    Assert.assertEquals(ciRelationsMappings.getCiRelationsMappingFromConfigFileList().size(), expectedList.size());

    
  }
  
  @Test(enabled = true)
  private void createCIRelationMappingsTest() {
    

    String sourcePack = "walmartlabs-apache_cassandra";
    String targetPack = "oneops-apache_cassandra";
    CiRelationsMappings ciRelationsMappings = context.getBean(CiRelationsMappings.class);
    log.info("ciRelationsMappings: "+ciRelationsMappings);
    ciRelationsMappings.createCIRelationMappings(sourcePack,targetPack );
    
    
  }
  

 // 1) create business logic to read tabe mappings and generate queries
 // 2) create table structure for CI Relations and CI Relation attribute mappings
  
    
  
  
}
