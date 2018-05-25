package com.oneops.circuitconsolidation.integrationtest;

import org.testng.annotations.Test;
import com.oneops.circuitconsolidation.util.CircuitConsolidationMain;
import com.oneops.circuitconsolidation.util.IConstants;

public class CircuitConsolidationMainTest {


  CircuitConsolidationMain app = new CircuitConsolidationMain();

  @Test(enabled = false)
  private void test_loadApplicationContext() {

    //platfromCi 369848286 for      /stgqe/dsing17delme-stgoo for cassandra-main3 
    
    /*String ns = "/stgqe/dsing17-delme-4";
    String platformName = "main-cassandra";
    */
    
    String ns = "/TestOrg2/TestTransformation";
    String platformName = "main-cassandra";
    
    String ooPhase = IConstants.DESIGN_PHASE;
    String envName = null;

    CircuitConsolidationMain app = new CircuitConsolidationMain();
    app.loadApplicationContext();
    app.performCircuitConsolidation(ns, platformName, ooPhase, envName);

  }

  
  /*
   * fromCi, ToCI, RelationName, DefaultAttributes
   * 
   * DefaultAttributes: AttributeName , setDfValue, setDjValue
   * 
   * 
   * osCmsCI, 
   * computeCmsCI
   * catalog.DependsOn
     defautAttributes
   * 
   * 
   */
}
