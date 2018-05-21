package com.oneops.circuitconsolidation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CiRelationModel implements Serializable {

  private static final long serialVersionUID = 1L;
  String fromCi;
  String toCi;
  String relationName;
  List<CiRelationDefaultAttributeModel> ciRelationDefaultAttributeList =
      new ArrayList<CiRelationDefaultAttributeModel>();


  public String getFromCi() {
    return fromCi;
  }

  public void setFromCi(String fromCi) {
    this.fromCi = fromCi;
  }

  public String getToCi() {
    return toCi;
  }

  public void setToCi(String toCi) {
    this.toCi = toCi;
  }

  public String getRelationName() {
    return relationName;
  }

  public void setRelationName(String relationName) {
    this.relationName = relationName;
  }

  public List<CiRelationDefaultAttributeModel> getCiRelationDefaultAttributeList() {
    return ciRelationDefaultAttributeList;
  }

  public void setCiRelationDefaultAttributeList(
      List<CiRelationDefaultAttributeModel> ciRelationDefaultAttributeList) {
    this.ciRelationDefaultAttributeList = ciRelationDefaultAttributeList;
  }

  public Map<String, CiRelationDefaultAttributeModel> getCiRelationDefaultAttributeMap() {

    Map<String, CiRelationDefaultAttributeModel> CiRelationDefaultAttributeMap =
        new HashMap<String, CiRelationDefaultAttributeModel>();

    for (CiRelationDefaultAttributeModel ciRelationDefaultAttribute : this.ciRelationDefaultAttributeList) {
      CiRelationDefaultAttributeMap.put(ciRelationDefaultAttribute.getRelationAttributeName(),
          ciRelationDefaultAttribute);
    }

    return CiRelationDefaultAttributeMap;
  }

}
