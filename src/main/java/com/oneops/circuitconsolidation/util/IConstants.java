package com.oneops.circuitconsolidation.util;

public interface IConstants {

  String AllMDClassesForAllOneOpsPhases_FILENAME="mdclasses_per_oophase.json";
  String DESIGN_PHASE="design";
  String TRANSITION_PHASE="transition";
  String OPERATE_PHASE="operate";
  String OS_CI_NAME="os";
  String COMPUTE_CI_NAME="compute";
  String comments="Updated for oneops circuit consolidation";
  String DESIGN_PHASE_CI_RELATIONS_File="design_phase_ci_relations.json";
  String TRANISTION_PHASE_CI_RELATIONS_File="transition_phase_ci_relations.json";
  String OPERATE_PHASE_CI_RELATIONS_File="operate_phase_ci_relations.json";
  String CI_CLAZZES_TRANSFORMATION_MAP_FILE= "ci_clazzes_transformation_map.json";
  String CI_RELATIONS_BLUEPRINT_FILE= "CIRelationsBluePrint.json";
  String DEPLOYMENT_TYPE_SINGLE="single";
  String DEPLOYMENT_TYPE_REDUNDATNT="redundant";
  String PUBLIC= "public";
  String PACK_SOURCE_ONEOPS="oneops";
  String PACK_SOURCE_WALMARTLABS="walmartlabs";
  String PACK_NAME_APACHE_CASSANDRA="apache_cassandra";

  String TRANSFORM_CMSCI="TRANSFORM_CMSCI";
  String DELETE_CMSCI="DELETE_CMSCI";
  String CREATE_CMSCI="CREATE_CMSCI";
  
  String ENTITY_TYPE_CMSCI="CMSCI";
  String ENTITY_TYPE_CMSCI_ATTRIBUTE="CMSCI_ATTRIBUTE";
  
  String SET_DEFAULT_CMSCI_ATTRIBUTE_VALUE="SET_DEFAULT_CMSCI_ATTRIBUTE_VALUE";
  String CREATE_CMSCI_ATTRIBUTE_WITH_SOURCE_CLAZZ_ATTRIBUTE_VALUE="CREATE_CMSCI_ATTRIBUTE_WITH_SOURCE_CLAZZ_ATTRIBUTE_VALUE";
  String DELETE_CMSCI_ATTRIBUTE="DELETE_CMSCI_ATTRIBUTE";
  String SWITCH_CMSCI_ATTRIBUTE_ID="SWITCH_CMSCI_ATTRIBUTE_ID";
  String SWITCH_CMCI_CLAZZID_CLAZZNAME_GOID="SWITCH_CMCI_CLAZZID_CLAZZNAME_GOID";    
}
