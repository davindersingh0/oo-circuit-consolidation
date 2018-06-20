package com.oneops.circuitconsolidation.model;

public class CmsCiTransformationModel {

  String ciAction;
  String SourceCiClazzName;
  String targetCiClazzName;

  public String getCiAction() {
    return ciAction;
  }

  public void setCiAction(String ciAction) {
    this.ciAction = ciAction;
  }

  public String getSourceCiClazzName() {
    return SourceCiClazzName;
  }

  public void setSourceCiClazzName(String sourceCiClazzName) {
    SourceCiClazzName = sourceCiClazzName;
  }

  public String getTargetCiClazzName() {
    return targetCiClazzName;
  }

  public void setTargetCiClazzName(String targetCiClazzName) {
    this.targetCiClazzName = targetCiClazzName;
  }



}