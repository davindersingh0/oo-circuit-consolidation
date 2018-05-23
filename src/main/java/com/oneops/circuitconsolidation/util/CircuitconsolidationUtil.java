package com.oneops.circuitconsolidation.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.oneops.circuitconsolidation.exceptions.UnSupportedOperation;

public class CircuitconsolidationUtil {
  private final static Logger log = LoggerFactory.getLogger(CircuitconsolidationUtil.class);
 
  public static String getFileContent(String fileName) throws IOException {
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


  public static String getnsForPlatformCiComponents(String ns, String platformName, String ooPhase,
      String envName) {

    switch (ooPhase) {
      case IConstants.DESIGN_PHASE:

        return ns + "/_design/" + platformName;
      case IConstants.TRANSITION_PHASE:

        return ns + "/" + envName + "/manifest/" + platformName + "/1";
      case IConstants.OPERATE_PHASE:
        log.error("ooPhase {} not supported", ooPhase);
        throw new UnSupportedOperation(ooPhase +" not supported");
        
      default:
        log.error("ooPhase {} not supported", ooPhase);
       
        throw new UnSupportedOperation(ooPhase +" not supported");

    }

  }

}