package com.oneops.circuitconsolidation.model;

import com.oneops.cms.cm.domain.CmsCIRelation;

public class CmsCIRelationModel extends CmsCIRelation {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private int fromCiClazzId;
  private String fromCiClazz;
  
  private int toCiClazzId;
  private String toCiClazz;
  
  public String getFromCiClazz() {
    return fromCiClazz;
  }
  public void setFromCiClazz(String fromCiClazz) {
    this.fromCiClazz = fromCiClazz;
  }
  public String getToCiClazz() {
    return toCiClazz;
  }
  public void setToCiClazz(String toCiClazz) {
    this.toCiClazz = toCiClazz;
  }
  public int getFromCiClazzId() {
    return fromCiClazzId;
  }
  public void setFromCiClazzId(int fromCiClazzId) {
    this.fromCiClazzId = fromCiClazzId;
  }
  public int getToCiClazzId() {
    return toCiClazzId;
  }
  public void setToCiClazzId(int toCiClazzId) {
    this.toCiClazzId = toCiClazzId;
  }
  
  
  
}
