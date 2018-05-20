package com.oneops.circuitconsolidation.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.exceptions.UnSupportedOperation;

public class CircuitconsolidationUtilTest {

  private final Logger log = LoggerFactory.getLogger(getClass());


  Gson gson = new Gson();


  @Test(enabled = true)
  private void test_mdclasses_per_oophase() {


    try {

      String fileContents = CircuitconsolidationUtil
          .getFileContent(IConstants.AllMDClassesForAllOneOpsPhases_FILENAME);
      assertEquals(fileContents,
          getFileContent(IConstants.AllMDClassesForAllOneOpsPhases_FILENAME));
      @SuppressWarnings("unchecked")
      Map<String, Map<String, String>> mdclasses_per_oophase =
          gson.fromJson(fileContents, Map.class);

      assertEquals(true, mdclasses_per_oophase.containsKey(IConstants.DESIGN_PHASE));
      assertEquals(true, mdclasses_per_oophase.containsKey(IConstants.TRANSITION_PHASE));
      assertEquals(true, mdclasses_per_oophase.containsKey(IConstants.OPERATE_PHASE));
    } catch (Exception e) {
      log.error("test_mdclasses_per_oophase test case failed with error:  ", e);
      fail();
    }
  }

  @Test(enabled = true)
  private void test_getnsForPlatformCiComponents() {
    String ns = "/testnspath";
    String platformName = "testPlatformName";
    String ooPhase = IConstants.DESIGN_PHASE;
    String envName = null;

    // Test Design Phase
    String nsForPlatformCiComponents =
        CircuitconsolidationUtil.getnsForPlatformCiComponents(ns, platformName, ooPhase, envName);
    assertEquals("/testnspath/_design/testPlatformName", nsForPlatformCiComponents);

    // Test Transition Phase
    ooPhase = IConstants.TRANSITION_PHASE;
    envName="dev";
    nsForPlatformCiComponents =
        CircuitconsolidationUtil.getnsForPlatformCiComponents(ns, platformName, ooPhase, envName);
    assertEquals("/testnspath/dev/manifest/testPlatformName/1", nsForPlatformCiComponents);

    // Test Operate Phase
    ooPhase = IConstants.OPERATE_PHASE;
    
    try {
      
    nsForPlatformCiComponents =
        CircuitconsolidationUtil.getnsForPlatformCiComponents(ns, platformName, ooPhase, envName);
    Assert.fail("Expected UnSupportedOperation Exception");
    
    } catch (Exception e) {
      assertEquals(e.getClass(), UnSupportedOperation.class);
     
    }
    

  }



  private String getFileContent(String fileName) throws IOException {
    String fileAsString = new String();
    InputStream is = ClassLoader.getSystemResourceAsStream(fileName);
    BufferedReader buf = new BufferedReader(new InputStreamReader(is));
    String line = buf.readLine();
    StringBuilder sb = new StringBuilder();
    while (line != null) {
      sb.append(line).append("\n");
      line = buf.readLine();
    }
    fileAsString = sb.toString();
    log.info("Contents : " + fileAsString);
    buf.close();
    return fileAsString;
  }

}
