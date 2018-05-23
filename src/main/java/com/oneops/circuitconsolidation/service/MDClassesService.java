package com.oneops.circuitconsolidation.service;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.exceptions.InvalidCacheLoadException;
import com.oneops.circuitconsolidation.util.CircuitconsolidationUtil;
import com.oneops.circuitconsolidation.util.IConstants;

public class MDClassesService {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  Gson gson;
  
  public void setGson(Gson gson) {
    this.gson = gson;
  }

  Map<String, Map<String, String>> mdclasses_per_oophaseMap =
      new HashMap<String, Map<String, String>>();

  MDClassesService()   {
    setMdclasses_per_oophaseMap(getAllMDClassesForAllOneOpsPhases());

  }

  public Map<String, Map<String, String>> getMdclasses_per_oophaseMap() {
    return mdclasses_per_oophaseMap;
  }

  public void setMdclasses_per_oophaseMap(
      Map<String, Map<String, String>> mdclasses_per_oophaseMap) {
    this.mdclasses_per_oophaseMap = mdclasses_per_oophaseMap;
  }


  public String getMdClassNameForCIinPhase(String ciName, String phase) {
    return this.mdclasses_per_oophaseMap.get(phase).get(ciName);

  }

  public Map<String, String> getMdClassNamesForPhase(String phase) {

    return this.mdclasses_per_oophaseMap.get(phase);
  }

  private Map<String, Map<String, String>> getAllMDClassesForAllOneOpsPhases()   {

    try {
      Gson gson = new Gson();
    
      @SuppressWarnings("unchecked")
      Map<String, Map<String, String>> map =
          gson.fromJson(CircuitconsolidationUtil
              .getFileContent(IConstants.AllMDClassesForAllOneOpsPhases_FILENAME), Map.class);
      return map;
     
    } catch (Exception e) {
      throw new InvalidCacheLoadException(
          "Unable to load cache from " + IConstants.AllMDClassesForAllOneOpsPhases_FILENAME, e);
    }
   
  }
 
}


