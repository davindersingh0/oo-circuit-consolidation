package com.oneops.circuitconsolidation.model;

import java.io.Serializable;

public class CiRelationDefaultAttributeModel implements Serializable{

  private static final long serialVersionUID = 1L;
  
  String relationAttributeName;
  String relationAttributeDfValue;
  String relationAttributeDjValue;
  public String getRelationAttributeName() {
    return relationAttributeName;
  }
  public void setRelationAttributeName(String relationAttributeName) {
    this.relationAttributeName = relationAttributeName;
  }
  public String getRelationAttributeDfValue() {
    return relationAttributeDfValue;
  }
  public void setRelationAttributeDfValue(String relationAttributeDfValue) {
    this.relationAttributeDfValue = relationAttributeDfValue;
  }
  public String getRelationAttributeDjValue() {
    return relationAttributeDjValue;
  }
  public void setRelationAttributeDjValue(String relationAttributeDjValue) {
    this.relationAttributeDjValue = relationAttributeDjValue;
  }
}
