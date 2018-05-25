package com.oneops.circuitconsolidation.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.service.CIRelationsService;
import com.oneops.circuitconsolidation.service.OsComponentService;
import com.oneops.circuitconsolidation.service.PlatformCiService;
import com.oneops.circuitconsolidation.service.TransformCiAttributesService;
import com.oneops.cms.cm.domain.CmsCI;

public class CircuitConsolidationMain {


  private static final Logger log = LoggerFactory.getLogger(CircuitConsolidationMain.class);
  ApplicationContext context;

  
  public ApplicationContext getContext() {
    return context;
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
   
    
    Gson gson= (Gson) this.context.getBean(Gson.class);
   
    //Change Platform Source
   PlatformCiService platformCiService=(PlatformCiService) this.context.getBean(PlatformCiService.class);
    platformCiService.transformPlatformPackSourceAttribute(ns, platformName);
    
  
    // Create New CIs
    OsComponentService osComponentService = (OsComponentService) this.context.getBean(OsComponentService.class);
    CmsCI osComponent=osComponentService.createOsComponentFromCompute(ns, platformName, ooPhase, envName);
    log.info(
        "OS component for ns {}, platformName {}, ooPhase {}, envName {} ",
        ns, platformName, ooPhase, envName);
    log.info("jsonified osComponent: "+gson.toJson(osComponent));

    if (osComponent==null) {
      log.info("osComponent does not exists, Transformation can not be performed");
      return;
    }
    // Create CI Relations
    CIRelationsService ciRelationsService= (CIRelationsService) this.context.getBean(CIRelationsService.class);
    boolean isComponentsRelationsCreated=ciRelationsService.createComponentRelations(ns, platformName, ooPhase, envName);
    log.info("created CI relations successfully? :"+isComponentsRelationsCreated);
    if (!isComponentsRelationsCreated) {
      log.info("Required Ci Relations missing, Transformation can not be performed");
      return;
    }
    TransformCiAttributesService transformCiAttributesService= (TransformCiAttributesService) this.context.getBean(TransformCiAttributesService.class);
    transformCiAttributesService.transformCIs(ns, platformName, ooPhase, envName);
    transformCiAttributesService.cleanUpCmsCIAttributes(ns, platformName, ooPhase, envName);
    
    

  }

  public void loadApplicationContext() {
    log.info("loading application context...");
    setContext(new FileSystemXmlApplicationContext(
        "/WebContent/WEB-INF/oo-circuit-consolidation-servlet.xml"));
    log.info("application context loading complete");

  }
}
