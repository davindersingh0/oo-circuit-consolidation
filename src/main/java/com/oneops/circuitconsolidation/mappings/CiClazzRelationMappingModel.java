package com.oneops.circuitconsolidation.mappings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.oneops.circuitconsolidation.model.CiRelationDefaultAttributeModel;

public class CiClazzRelationMappingModel implements Serializable {

  public static final long serialVersionUID = 1L;

  String sourcePack;
  String targetPack;
  
  private int relationId;
  private String relationName;

  private String fromCiClazz;
  private int fromCiClazzId;
  
  private String toCiClazz;
  private int toCiClazzId;


  List<CiRelationDefaultAttributeModel> ciRelationDefaultAttributeList =
      new ArrayList<CiRelationDefaultAttributeModel>();

  
  
  public String getSourcePack() {
    return sourcePack;
  }



  public void setSourcePack(String sourcePack) {
    this.sourcePack = sourcePack;
  }



  public String getTargetPack() {
    return targetPack;
  }



  public void setTargetPack(String targetPack) {
    this.targetPack = targetPack;
  }



  public int getRelationId() {
    return relationId;
  }



  public void setRelationId(int relationId) {
    this.relationId = relationId;
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



  public List<CiRelationDefaultAttributeModel> getCiRelationDefaultAttributeList() {
    return ciRelationDefaultAttributeList;
  }



  public void setCiRelationDefaultAttributeList(
      List<CiRelationDefaultAttributeModel> ciRelationDefaultAttributeList) {
    this.ciRelationDefaultAttributeList = ciRelationDefaultAttributeList;
  }



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



  public String getRelationName() {
    return relationName;
  }



  public void setRelationName(String relationName) {
    this.relationName = relationName;
  }


}
