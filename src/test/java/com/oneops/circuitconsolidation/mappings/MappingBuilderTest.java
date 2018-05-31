package com.oneops.circuitconsolidation.mappings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.mappings.dal.OOConsolidationMapper;
import com.oneops.circuitconsolidation.util.CircuitConsolidationMain;
import com.oneops.cms.cm.domain.CmsCI;

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

    mappingBuilder.createCmCiAttributesMappings(sourcePack, targetPack);


  }


  @Test(enabled = true)
  private void testDal() {

    OOConsolidationMapper ooConsolidationMapper = context.getBean(OOConsolidationMapper.class);

    CmsCI ci = ooConsolidationMapper.getCIById(255691);

    log.info("gson: " + gson.toJson(ci));

  }

}
