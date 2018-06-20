package com.oneops.circuitconsolidation.util;

import static org.testng.Assert.assertEquals;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.model.CmsCIRelationModel;

public class PackDefinitionUtilTest {

  private ApplicationContext context;
  private PackDefinitionUtil packDefinitionUtil;
  private Gson gson;
 
  private final Logger log = LoggerFactory.getLogger(PackDefinitionUtilTest.class);

  @BeforeClass
  private void init() {
    CircuitConsolidationMain app = new CircuitConsolidationMain();
    app.loadApplicationContext();
    context = app.getContext();
    
    gson=context.getBean(Gson.class);
    packDefinitionUtil= context.getBean(PackDefinitionUtil.class);
  }

  // DESIGN PHASE: mgmt.Requires >> base.Requires
  // mgmt.catalog.DependsOn >>catalog.DependsOn
  // Transition phase : mgmt.Requires >> manifest.Requires
  // mgmt.manifest.DependsOn >> manifest.DependsOn
  // mgmt.Entrypoint >> base.Entrypoint
  // base.Consumes -->stg-cdc2

  @Test(enabled = true)
  private void test() {

    String packSource = "oneops";
    String packName = "apache_cassandra";

    String ooPhase = IConstants.DESIGN_PHASE;
    String deplomentType = IConstants.DEPLOYMENT_TYPE_SINGLE;


    String nsForPackDefination = CircuitconsolidationUtil.getnsForPackDefinition(packSource,
        packName, ooPhase, deplomentType);
    log.info("nsForPackDefination: " + nsForPackDefination);

    
    Map<String, CmsCIRelationModel> cmsCIRelationTypesInpackDefinition =
        packDefinitionUtil.getCiRelationsFromPackDefinition(packSource, packName, ooPhase, deplomentType);

    log.info("cmsCIRelationTypesInpackDefinition Design Phase: " + gson.toJson(cmsCIRelationTypesInpackDefinition));


  }

 
  @Test(enabled = false)
  private void testGetMappingKey() {

    String keyspace = "mgmt.catalog.oneops.1.Keyspace";
    String Apache_cassandra = "mgmt.catalog.oneops.1.Apache_cassandra";
    String Jolokia_proxy = "mgmt.catalog.oneops.1.Jolokia_proxy";
    String Secgroup = "mgmt.catalog.oneops.1.Secgroup";

    String Wmt_keyspacekey = packDefinitionUtil.getMappingKey(keyspace);
    String Apache_cassandrakey = packDefinitionUtil.getMappingKey(Apache_cassandra);
    String Jolokia_proxyKey = packDefinitionUtil.getMappingKey(Jolokia_proxy);
    String SecgroupKey = packDefinitionUtil.getMappingKey(Secgroup);


    log.info("Wmt_keyspacekey: " + Wmt_keyspacekey);
    log.info("Apache_cassandrakey: " + Apache_cassandrakey);
    log.info("Jolokia_proxyKey: " + Jolokia_proxyKey);
    log.info("SecgroupKey: " + SecgroupKey);

    assertEquals(Wmt_keyspacekey, "catalogkeyspace");
    assertEquals(Apache_cassandrakey, "catalogcassandra");
    assertEquals(Jolokia_proxyKey, "catalogproxy");
    assertEquals(SecgroupKey, "catalogsecgroup");


  }
}

