package com.oneops.circuitconsolidation.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.service.OsComponentService;
import com.oneops.circuitconsolidation.service.PlatformCiService;
import com.oneops.cms.cm.domain.CmsCI;

public class CircuitConsolidationMain {


  private static final Logger log = LoggerFactory.getLogger(CircuitConsolidationMain.class);
  ApplicationContext context;

  @Autowired
  OsComponentService osComponentService;
  
  
  public ApplicationContext getContext() {
    return context;
  }

  public void setOsComponentService(OsComponentService osComponentService) {
    this.osComponentService = osComponentService;
  }

  public void setContext(ApplicationContext context) {
    this.context = context;
  }

  public static void main(String[] args) {

    log.info("starting circuit consoliation for ");
    CircuitConsolidationMain app = new CircuitConsolidationMain();

    app.loadApplicationContext();
    log.info("starting circuit consoliation activity complete");


  }

  public void performCircuitConsolidation(String ns, String platformName, String ooPhase,
      String envName) {
    // TODO Auto-generated method stub
    // Validate inputs
    // createNewCIs
    Gson gson= (Gson) this.context.getBean(Gson.class);
    PlatformCiService platformCiService=(PlatformCiService) this.context.getBean(PlatformCiService.class);
    platformCiService.transformPlatformPackSourceAttribute(ns, platformName);
    
    
    OsComponentService osComponentService = (OsComponentService) this.context.getBean(OsComponentService.class);
    CmsCI osComponent=osComponentService.createOsComponentFromCompute(ns, platformName, ooPhase, envName);
    log.info(
        "OS component for ns {}, platformName {}, ooPhase {}, envName {} ",
        ns, platformName, ooPhase, envName);
    log.info("jsonified osComponent: "+gson.toJson(osComponent));
    
    boolean isOsComponentRelationsCreated=osComponentService.createOsComponentRelations(ns, platformName, ooPhase, envName);
    log.info("isOsComponentRelationsCreated: "+isOsComponentRelationsCreated);
    // createCIRelationss
    

  }

  public void loadApplicationContext() {
    log.info("loading application context...");
    setContext(new FileSystemXmlApplicationContext(
        "/WebContent/WEB-INF/oo-circuit-consolidation-servlet.xml"));
    log.info("application context loading complete");

  }
}
