package com.oneops.circuitconsolidation.mappings;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.mappings.dal.OOConsolidationMapper;
import com.oneops.circuitconsolidation.util.CircuitConsolidationMain;

public class MappingBuilderTest {
  private final Logger log = LoggerFactory.getLogger(MappingBuilder.class);

  private ApplicationContext context;
  private Gson gson = new Gson();

  @BeforeClass
  private void init() {
    CircuitConsolidationMain app = new CircuitConsolidationMain();
    app.loadApplicationContext();
    context = app.getContext();


  }


  @Test(enabled = false)

  private void testMappings() {

    String sourcePack = "walmartlabs-apache_cassandra";
    String targetPack = "oneops-apache_cassandra";
    MappingBuilder mappingBuilder = context.getBean(MappingBuilder.class);

    
    List<CmsCiAndCmsCiAttributesActionMappingsModel> mappings=mappingBuilder.createCmCiAttributesMappings(sourcePack, targetPack);
  

  }

  @Test(enabled = false)
  private void populateCmsCiAndCmsCiAttributesActionMappings() {

    OOConsolidationMapper ooConsolidationMapper = context.getBean(OOConsolidationMapper.class);

    CmsCiAndCmsCiAttributesActionMappingsModel cmsCiAndCmsCiAttributesActionMappings =
        new CmsCiAndCmsCiAttributesActionMappingsModel();

    cmsCiAndCmsCiAttributesActionMappings.setSourcePack("sourcePack");
    cmsCiAndCmsCiAttributesActionMappings.setSourceClassname("sourceClassname");
    cmsCiAndCmsCiAttributesActionMappings.setSourceClassId(111);
    cmsCiAndCmsCiAttributesActionMappings.setSourceAttributeName("sourceAttributeName");
    cmsCiAndCmsCiAttributesActionMappings.setSourceAttributeId(222);
    cmsCiAndCmsCiAttributesActionMappings.setSourceDefaultValue("sourceDefaultValue");


    cmsCiAndCmsCiAttributesActionMappings.setTargetPack("TargetPack");
    cmsCiAndCmsCiAttributesActionMappings.setTargetClassname("TargetClassname");
    cmsCiAndCmsCiAttributesActionMappings.setTargetClassId(333);
    cmsCiAndCmsCiAttributesActionMappings.setTargetAttributeName("TargetAttributeName");
    cmsCiAndCmsCiAttributesActionMappings.setTargetAttributeId(444);
    cmsCiAndCmsCiAttributesActionMappings.setTargetDefaultValue("sourceDefaultValue");

    cmsCiAndCmsCiAttributesActionMappings.setAction("TestAction");
    cmsCiAndCmsCiAttributesActionMappings.setEntityType("TestEntity");
    log.info("cmsCiAndCmsCiAttributesActionMappings: "
        + gson.toJson(cmsCiAndCmsCiAttributesActionMappings));

    log.info("cmsCiAndCmsCiAttributesActionMappings: {}, ", cmsCiAndCmsCiAttributesActionMappings);

    ooConsolidationMapper
        .populateCmsCiAndCmsCiAttributesActionMappings(cmsCiAndCmsCiAttributesActionMappings);

  }


  @Test(enabled = false)
  private void populateCmsCiAndCmsCiAttributesActionMappingsList() {

    OOConsolidationMapper ooConsolidationMapper = context.getBean(OOConsolidationMapper.class);

    CmsCiAndCmsCiAttributesActionMappingsModel cmsCiAndCmsCiAttributesActionMappings =
        new CmsCiAndCmsCiAttributesActionMappingsModel();

    cmsCiAndCmsCiAttributesActionMappings.setSourcePack("sourcePack");
    cmsCiAndCmsCiAttributesActionMappings.setSourceClassname("sourceClassname");
    cmsCiAndCmsCiAttributesActionMappings.setSourceClassId(111);
    cmsCiAndCmsCiAttributesActionMappings.setSourceAttributeName("sourceAttributeName");
    cmsCiAndCmsCiAttributesActionMappings.setSourceAttributeId(222);
    cmsCiAndCmsCiAttributesActionMappings.setSourceDefaultValue("sourceDefaultValue");


    cmsCiAndCmsCiAttributesActionMappings.setTargetPack("TargetPack");
    cmsCiAndCmsCiAttributesActionMappings.setTargetClassname("TargetClassname");
    cmsCiAndCmsCiAttributesActionMappings.setTargetClassId(333);
    cmsCiAndCmsCiAttributesActionMappings.setTargetAttributeName("TargetAttributeName");
    cmsCiAndCmsCiAttributesActionMappings.setTargetAttributeId(444);
    cmsCiAndCmsCiAttributesActionMappings.setTargetDefaultValue("sourceDefaultValue");

    cmsCiAndCmsCiAttributesActionMappings.setAction("TestAction");
    cmsCiAndCmsCiAttributesActionMappings.setEntityType("TestEntity");
    log.info("cmsCiAndCmsCiAttributesActionMappings: "
        + gson.toJson(cmsCiAndCmsCiAttributesActionMappings));

    List<CmsCiAndCmsCiAttributesActionMappingsModel> cmsCiAndCmsCiAttributesActionMappingsList =
        new ArrayList<CmsCiAndCmsCiAttributesActionMappingsModel>();

    cmsCiAndCmsCiAttributesActionMappingsList.add(cmsCiAndCmsCiAttributesActionMappings);
    cmsCiAndCmsCiAttributesActionMappingsList.add(cmsCiAndCmsCiAttributesActionMappings);

    log.info("size: {}, ", cmsCiAndCmsCiAttributesActionMappingsList.size());
    log.info("cmsCiAndCmsCiAttributesActionMappingsList: {}, ",
        cmsCiAndCmsCiAttributesActionMappingsList);

    ooConsolidationMapper.populateCmsCiAndCmsCiAttributesActionMappingsList(
        cmsCiAndCmsCiAttributesActionMappingsList);

  }



}
