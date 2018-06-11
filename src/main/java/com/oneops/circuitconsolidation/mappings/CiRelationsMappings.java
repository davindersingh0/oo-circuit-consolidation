package com.oneops.circuitconsolidation.mappings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.exceptions.InvalidCacheLoadException;
import com.oneops.circuitconsolidation.mappings.dal.OOConsolidationMapper;
import com.oneops.circuitconsolidation.util.CircuitconsolidationUtil;
import com.oneops.circuitconsolidation.util.IConstants;
import com.oneops.cms.md.dal.RelationMapper;
import com.oneops.cms.md.service.CmsMdProcessor;

public class CiRelationsMappings {

  private final Logger log = LoggerFactory.getLogger(CiRelationsMappings.class);
  private List<CiClazzRelationMappingModel> ciRelationsMappingFromConfigFileList;


  @Autowired
  private RelationMapper relationMapper;
  
  @Autowired
  private CmsMdProcessor mdProcessor;
  
  @Autowired
  private OOConsolidationMapper ooConsolidationMapper;
  
  
  
  public void setOoConsolidationMapper(OOConsolidationMapper ooConsolidationMapper) {
    this.ooConsolidationMapper = ooConsolidationMapper;
  }


  public void setMdProcessor(CmsMdProcessor mdProcessor) {
    this.mdProcessor = mdProcessor;
  }



  public void setRelationMapper(RelationMapper relationMapper) {
    this.relationMapper = relationMapper;
  }


  public List<CiClazzRelationMappingModel> getCiRelationsMappingFromConfigFileList() {
    return ciRelationsMappingFromConfigFileList;
  }


  public void setCiRelationsMappingFromConfigFileList(
      List<CiClazzRelationMappingModel> ciRelationsMappingFromConfigFileList) {
    this.ciRelationsMappingFromConfigFileList = ciRelationsMappingFromConfigFileList;
  }



  CiRelationsMappings() {
    setCiRelationsMappingFromConfigFileList(loadCiRelationsConfigs());

  }


  public List<CiClazzRelationMappingModel> loadCiRelationsConfigs() {



    try {
      Gson gson = new Gson();
      CiClazzRelationMappingModel[] ciRelationsMappingFromConfigFileArr = gson.fromJson(
          CircuitconsolidationUtil.getFileContent(IConstants.CI_RELATIONS_BLUEPRINT_FILE),
          CiClazzRelationMappingModel[].class);
      log.info("ciRelationsMappingListFromConfigFile: " + ciRelationsMappingFromConfigFileList);
      List <CiClazzRelationMappingModel> ciClazzRelationModelList=Arrays.asList(ciRelationsMappingFromConfigFileArr);
      return ciClazzRelationModelList;

    } catch (Exception e) {
      throw new InvalidCacheLoadException("Error while loading ciRelations configurations from "
          + IConstants.CI_RELATIONS_BLUEPRINT_FILE, e);
    }

  }


  public void createCIRelationMappings(String sourcePack, String targetPack) {

    List<CiClazzRelationMappingModel> ciClazzRelationMappingList = new ArrayList<CiClazzRelationMappingModel>();
    log.info("pouplate ciClazzRelationMappingList");
    for (CiClazzRelationMappingModel ciClazzRelationMapping : this.ciRelationsMappingFromConfigFileList) {

    
      ciClazzRelationMappingList.add(pouplateCiClazzRelationModelObject(sourcePack, targetPack, ciClazzRelationMapping));

    }
    log.info("publish ciClazzRelationMappingList to database");
    for (CiClazzRelationMappingModel ciClazzRelationMapping: ciClazzRelationMappingList) {
      ooConsolidationMapper.populateCiClazzRelationMappings(ciClazzRelationMapping);
    }
    
  }


  private CiClazzRelationMappingModel pouplateCiClazzRelationModelObject(String sourcePack, String targetPack, CiClazzRelationMappingModel ciClazzRelationMapping) {
    String fromClazzName = ciClazzRelationMapping.getFromCiClazz();
    String toClazzName = ciClazzRelationMapping.getToCiClazz();
    String relationName = ciClazzRelationMapping.getRelationName();
    
    ciClazzRelationMapping.setSourcePack(sourcePack);
    ciClazzRelationMapping.setTargetPack(targetPack);
    ciClazzRelationMapping.setRelationId(relationMapper.getRelation(relationName).getRelationId());

        
    ciClazzRelationMapping.setFromCiClazzId(mdProcessor.getClazz(fromClazzName).getClassId());
    ciClazzRelationMapping.setToCiClazzId(mdProcessor.getClazz(toClazzName).getClassId());
  
    return ciClazzRelationMapping;
   
  }



}
