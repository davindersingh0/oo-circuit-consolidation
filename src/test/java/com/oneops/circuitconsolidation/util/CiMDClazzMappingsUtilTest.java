package com.oneops.circuitconsolidation.util;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.model.CmsCiTransformationModel;

public class CiMDClazzMappingsUtilTest {

  
  private final Logger log = LoggerFactory.getLogger(CiMDClazzMappingsUtilTest.class);

  
  private ApplicationContext context;
  private Gson gson; 
  CiMDClazzMappingsUtil ciMDClazzMappingsUtil;

  @BeforeClass
  private void init() {
    CircuitConsolidationMain app = new CircuitConsolidationMain();
    app.loadApplicationContext();
    context = app.getContext();
    gson=context.getBean(Gson.class);
    ciMDClazzMappingsUtil=context.getBean(CiMDClazzMappingsUtil.class);

  }
  
  @Test(enabled = true)
  private void testGetMDClazzMappingsForSourceAndTartgetPacks() {
    
    String ooPhase = IConstants.DESIGN_PHASE;
    // String ooPhase = IConstants.TRANSITION_PHASE;
    String envName = "dev";


    String ns_sourcePack = "/TestOrg2/ms-wmtlabs-a-cass";
    String platformName_sourcePack = "cass";
    String nsForPlatformCiComponents_sourcePack = CircuitconsolidationUtil
        .getnsForPlatformCiComponents(ns_sourcePack, platformName_sourcePack, ooPhase, envName);


    String ns_targetPack = "/TestOrg2/ms-oneops-a-cass";
    String platformName_targetPack = "oneops-apache-cassandra";
    String nsForPlatformCiComponents_targetPack = CircuitconsolidationUtil
        .getnsForPlatformCiComponents(ns_targetPack, platformName_targetPack, ooPhase, envName);


    List<CmsCiTransformationModel> mapppings =  ciMDClazzMappingsUtil.getMDClazzMappingsForSourceAndTartgetPacks(nsForPlatformCiComponents_sourcePack, nsForPlatformCiComponents_targetPack);
  
    log.info("mapppings for {} phase : {}", ooPhase, gson.toJson(mapppings));
    
    
  }
  
  
  
}
