package com.oneops.circuitconsolidation.util;

import static org.testng.Assert.assertEquals;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.service.TransformCiAttributesService;

public class TransformCiAttributesServiceTest {

  private final Logger log = LoggerFactory.getLogger(TransformCiAttributesServiceTest.class);


  @Test(enabled = true)
  private void test_loadCiClazzesTransformationConfigs() throws IOException {


    try {

      TransformCiAttributesService service = new TransformCiAttributesService();

      Map<String, String> mapFromService = service.loadCiClazzesTransformationConfigs();

      @SuppressWarnings("unchecked")
      Map<String, String> mapFromFile = new Gson().fromJson(
          CircuitconsolidationUtil.getFileContent(IConstants.CI_CLAZZES_TRANSFORMATION_MAP_FILE),
          Map.class);
      assertEquals(mapFromService, mapFromFile);

    } catch (Exception e) {
      log.error("Failed to create TransformCiAttributesService cache", e);

      Assert.fail();
    }

  }



}
