package com.oneops.circuitconsolidation.util;

import static org.testng.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.mappings.CiRelationsMappingsTest;
import com.oneops.circuitconsolidation.mappings.dal.OOConsolidationMapper;
import com.oneops.circuitconsolidation.model.CmsCIRelationAndRelationAttributesActionMappingsModel;
import com.oneops.circuitconsolidation.model.CmsCIRelationModel;
import com.oneops.cms.md.domain.CmsRelation;
import com.oneops.cms.md.domain.CmsRelationAttribute;
import com.oneops.cms.md.service.CmsMdProcessor;

/**
 * @author dsing17 This class is created to generate CMSCi mapping Clazzes data for populating
 *         ci_clazzes_transformation_map.json file
 */
public class CMSDalUtil_MDClazzRelationsTest {

  private final Logger log = LoggerFactory.getLogger(CMSDalUtil_MDClazzRelationsTest.class);

  private ApplicationContext context;
  private Gson gson = new Gson();
  private OOConsolidationMapper oOConsolidationMapper;
  private CmsMdProcessor mdProcessor;
  private PackDefinitionUtil packDefinitionUtil;

  private Map<String, CmsCIRelationModel> cmsCIRelationTypesInpackDefinitionMap;
  
  
  public Map<String, CmsCIRelationModel> getCmsCIRelationTypesInpackDefinitionMap() {
    return cmsCIRelationTypesInpackDefinitionMap;
  }

  public void setCmsCIRelationTypesInpackDefinitionMap(
      Map<String, CmsCIRelationModel> cmsCIRelationTypesInpackDefinitionMap) {
    this.cmsCIRelationTypesInpackDefinitionMap = cmsCIRelationTypesInpackDefinitionMap;
  }

  @BeforeClass
  private void init() {
    CircuitConsolidationMain app = new CircuitConsolidationMain();
    app.loadApplicationContext();
    context = app.getContext();
    oOConsolidationMapper = context.getBean(OOConsolidationMapper.class);
    mdProcessor= context.getBean(CmsMdProcessor.class);
    packDefinitionUtil= context.getBean(PackDefinitionUtil.class);

  }

  // :TODO: update Comments for each relation since clazz names have changed, though it does not
  // impact functionlaity but for cleanup purpose
  // String ooPhase = IConstants.DESIGN_PHASE;
  @Test(enabled = true)
  private void createCiRelationsMappings() {

    
    
    // /public/walmartlabs/packs/apache_cassandra/1/single
    // /public/walmartlabs/packs/apache_cassandra/1/redundant

    // following is the oneOps ApacheCassandra Pack defination
    // http://localhost:8080/cms-admin/ci.do?id=10750

    String targetPackSource = "oneops";
    String packName = "apache_cassandra";

    String sourcePackSource = "walmartlabs";
    
    String ooPhase = IConstants.DESIGN_PHASE;
    String deplomentType = IConstants.DEPLOYMENT_TYPE_SINGLE;


    String nsForPackDefinition = CircuitconsolidationUtil.getnsForPackDefinition(targetPackSource,
        packName, ooPhase, deplomentType);
    log.info("nsForPackDefinition: " + nsForPackDefinition);


    
    cmsCIRelationTypesInpackDefinitionMap =
        packDefinitionUtil.getCiRelationsFromPackDefinition(targetPackSource, packName, ooPhase, deplomentType);

    log.info("cmsCIRelationTypesInpackDefinition Design Phase: " + gson.toJson(cmsCIRelationTypesInpackDefinitionMap));
    
    
    
    String sourcePackCatlogName = sourcePackSource+"-"+packName;
    String targetPackCatlogName = targetPackSource+"-"+packName;
    
    String envName = null;


    String ns_sourcePack = "/TestOrg2/ms-wmtlabs-a-cass";
    String platformName_sourcePack = "cass";

    String ns_targetPack = "/TestOrg2/ms-oneops-a-cass";
    String platformName_targetPack = "oneops-apache-cassandra";


    List<CmsCIRelationAndRelationAttributesActionMappingsModel> cmsCIRelationAndRelationAttributesActionMappingsList_designPhase =
        createCmsCIRelationMappings(ns_sourcePack, platformName_sourcePack, ns_targetPack,
            platformName_targetPack, ooPhase, envName, sourcePackCatlogName, targetPackCatlogName);

    log.info("jsonifiled cmsCIRelationAndRelationAttributesActionMappingsList_designPhase: "
        + gson.toJson(cmsCIRelationAndRelationAttributesActionMappingsList_designPhase));


    ooPhase = IConstants.TRANSITION_PHASE;
    envName = "dev";

    nsForPackDefinition = CircuitconsolidationUtil.getnsForPackDefinition(targetPackSource,
        packName, ooPhase, deplomentType);
    
    cmsCIRelationTypesInpackDefinitionMap =
        packDefinitionUtil.getCiRelationsFromPackDefinition(targetPackSource, packName, ooPhase, deplomentType);
    
    List<CmsCIRelationAndRelationAttributesActionMappingsModel> cmsCIRelationAndRelationAttributesActionMappingsList_transitionPhase =
        createCmsCIRelationMappings(ns_sourcePack, platformName_sourcePack, ns_targetPack,
            platformName_targetPack, ooPhase, envName, sourcePackCatlogName, targetPackCatlogName);

    log.info("jsonifiled cmsCIRelationAndRelationAttributesActionMappingsList_transitionPhase: "
        + gson.toJson(cmsCIRelationAndRelationAttributesActionMappingsList_transitionPhase));

  
    publishCmsCIRelationAndRelationAttributesActionMappings(
        cmsCIRelationAndRelationAttributesActionMappingsList_designPhase);

    publishCmsCIRelationAndRelationAttributesActionMappings(
        cmsCIRelationAndRelationAttributesActionMappingsList_transitionPhase);

  }



  private List<CmsCIRelationAndRelationAttributesActionMappingsModel> createCmsCIRelationMappings(
      String ns_sourcePack, String platformName_sourcePack, String ns_targetPack,
      String platformName_targetPack, String ooPhase, String envName, String sourcePackCatlogName,
      String targetPackCatlogName) {

    Map<String, CmsCIRelationModel> cmsCIRelationTypes_sourcePack =
        getCmsCIRelationTypesForAllCIRelationsInPack(ns_sourcePack, platformName_sourcePack,
            ooPhase, envName);

    Map<String, CmsCIRelationModel> cmsCIRelationTypes_targetPack =
        getCmsCIRelationTypesForAllCIRelationsInPack(ns_targetPack, platformName_targetPack,
            ooPhase, envName);


    List<CmsCIRelationAndRelationAttributesActionMappingsModel> cmsCIRelationAndRelationAttributesActionMappingsList =
        createCmsCIRelationAndRelationAttributesActionMappings(sourcePackCatlogName,
            cmsCIRelationTypes_sourcePack, targetPackCatlogName, cmsCIRelationTypes_targetPack);

    return cmsCIRelationAndRelationAttributesActionMappingsList;
  }

  private Map<String, CmsCIRelationModel> getCmsCIRelationTypesForAllCIRelationsInPack(String ns,
      String platformName, String ooPhase, String envName) {


    String nsForPlatformCiComponents =
        CircuitconsolidationUtil.getnsForPlatformCiComponents(ns, platformName, ooPhase, envName);

    List<CmsCIRelationModel> cmsCIRelationsPlatformComponentsNsPath = oOConsolidationMapper
        .getCIRelations(nsForPlatformCiComponents, null, null, null, null, null, null);

    Map<String, CmsCIRelationModel> cmsCIRelationTypes =
        createCmsCIRelationTypesForAllCIRelationsInPack(cmsCIRelationsPlatformComponentsNsPath);

    return cmsCIRelationTypes;

  }


  private void publishCmsCIRelationAndRelationAttributesActionMappings(
      List<CmsCIRelationAndRelationAttributesActionMappingsModel> cmsCIRelationAndRelationAttributesActionMappingsList) {
    log.info("populating mappings into CMS database");
    for (CmsCIRelationAndRelationAttributesActionMappingsModel cmsCIRelationAndRelationAttributesActionMappings : cmsCIRelationAndRelationAttributesActionMappingsList) {
      oOConsolidationMapper.populateCmsCIRelationAndRelationAttributesActionMappings(
          cmsCIRelationAndRelationAttributesActionMappings);
    }

    log.info("populated mappings into CMS database");

  }


  private List<CmsCIRelationAndRelationAttributesActionMappingsModel> createCmsCIRelationAndRelationAttributesActionMappings(
      String sourcePack, Map<String, CmsCIRelationModel> cmsCIRelationTypes_sourcePack,
      String targetPack, Map<String, CmsCIRelationModel> cmsCIRelationTypes_targetPack) {

    List<CmsCIRelationAndRelationAttributesActionMappingsModel> cmsCIRelationAndRelationAttributesActionMappingsList =
        new ArrayList<CmsCIRelationAndRelationAttributesActionMappingsModel>();

    for (String cmsCIRelationTypeKey_source : cmsCIRelationTypes_sourcePack.keySet()) {
      CmsCIRelationAndRelationAttributesActionMappingsModel cmsCIRelationAndRelationAttributesActionMapping =
          new CmsCIRelationAndRelationAttributesActionMappingsModel();

      CmsCIRelationModel sourceCmsCIRelation =
          cmsCIRelationTypes_sourcePack.get(cmsCIRelationTypeKey_source);
      CmsCIRelationModel targetCmsCIRelation =
          cmsCIRelationTypes_targetPack.get(cmsCIRelationTypeKey_source);

      if (targetCmsCIRelation != null) {
        cmsCIRelationAndRelationAttributesActionMapping.setSourcePack(sourcePack);
        cmsCIRelationAndRelationAttributesActionMapping
            .setSourceCmsCiRelationKey(cmsCIRelationTypeKey_source);

        cmsCIRelationAndRelationAttributesActionMapping
            .setSourceCmsCIRelationModel(sourceCmsCIRelation);


        cmsCIRelationAndRelationAttributesActionMapping.setTargetPack(targetPack);
        cmsCIRelationAndRelationAttributesActionMapping
            .setTargetCmsCIRelationModel(targetCmsCIRelation);

        cmsCIRelationAndRelationAttributesActionMapping.setAction("NO_ACTION");
        cmsCIRelationAndRelationAttributesActionMapping.setEntityType("CMSCI_RELATION");
        /*
         * cmsCIRelationAndRelationAttributesActionMappingsList
         * .add(cmsCIRelationAndRelationAttributesActionMapping);
         */ } else {

        cmsCIRelationAndRelationAttributesActionMapping.setSourcePack(sourcePack);
        cmsCIRelationAndRelationAttributesActionMapping
            .setSourceCmsCiRelationKey(cmsCIRelationTypeKey_source);
        cmsCIRelationAndRelationAttributesActionMapping
            .setSourceCmsCIRelationModel(sourceCmsCIRelation);



        cmsCIRelationAndRelationAttributesActionMapping.setTargetPack(targetPack);
        cmsCIRelationAndRelationAttributesActionMapping.setEntityType("CMSCI_RELATION");
        cmsCIRelationAndRelationAttributesActionMapping.setAction("DELETE_RELATION");
        cmsCIRelationAndRelationAttributesActionMappingsList
            .add(cmsCIRelationAndRelationAttributesActionMapping);
      }


    }


    for (String cmsCIRelationTypeKey_target : cmsCIRelationTypes_targetPack.keySet()) {
      CmsCIRelationAndRelationAttributesActionMappingsModel cmsCIRelationAndRelationAttributesActionMapping =
          new CmsCIRelationAndRelationAttributesActionMappingsModel();

      CmsCIRelationModel sourceCmsCIRelation =
          cmsCIRelationTypes_sourcePack.get(cmsCIRelationTypeKey_target);
      CmsCIRelationModel targetCmsCIRelation =
          cmsCIRelationTypes_targetPack.get(cmsCIRelationTypeKey_target);
      if (sourceCmsCIRelation == null) {

        // createRelation
        cmsCIRelationAndRelationAttributesActionMapping.setSourcePack(sourcePack);
        cmsCIRelationAndRelationAttributesActionMapping.setTargetPack(targetPack);

        cmsCIRelationAndRelationAttributesActionMapping
            .setTargetCmsCIRelationModel(targetCmsCIRelation);

        cmsCIRelationAndRelationAttributesActionMapping.setAction("CREATE_RELATION");
        cmsCIRelationAndRelationAttributesActionMapping.setEntityType("CMSCI_RELATION");

        cmsCIRelationAndRelationAttributesActionMappingsList
            .add(cmsCIRelationAndRelationAttributesActionMapping);

        
        populateCmsCIRelationAttributesMappings(
            cmsCIRelationAndRelationAttributesActionMappingsList, cmsCIRelationTypeKey_target,
            sourcePack, targetPack, targetCmsCIRelation);
        log.info("cmsCIRelationTypeKey_target: " + cmsCIRelationTypeKey_target);


        continue;
      }

    }


    return cmsCIRelationAndRelationAttributesActionMappingsList;
  }


  private void populateCmsCIRelationAttributesMappings(
      List<CmsCIRelationAndRelationAttributesActionMappingsModel> cmsCIRelationAndRelationAttributesActionMappingsList,
      String cmsCIRelationTypeKey_target, String sourcePack, String targetPack,
      CmsCIRelationModel targetCmsCIRelation) {

    
    String relationName=targetCmsCIRelation.getRelationName();
    
    CmsRelation relationClazz = mdProcessor.getRelation(relationName);
    log.info("relationClazz: :" + gson.toJson(relationClazz));
    
    for (CmsRelationAttribute relationAttr : relationClazz.getMdAttributes()) {
    
      CmsCIRelationAndRelationAttributesActionMappingsModel cmsCIRelationAndRelationAttributesActionMapping =
          new CmsCIRelationAndRelationAttributesActionMappingsModel();

      cmsCIRelationAndRelationAttributesActionMapping.setSourcePack(sourcePack);
      cmsCIRelationAndRelationAttributesActionMapping.setTargetPack(targetPack);

      cmsCIRelationAndRelationAttributesActionMapping.setAction("ADD_RELATION_ATTRIBUTE");
      cmsCIRelationAndRelationAttributesActionMapping.setEntityType("CMSCI_RELATION_ATTRIBUTE");

      cmsCIRelationAndRelationAttributesActionMapping
          .setTargetCmsCIRelationModel(targetCmsCIRelation);

      cmsCIRelationAndRelationAttributesActionMapping.setRelationId(relationAttr.getRelationId());
      cmsCIRelationAndRelationAttributesActionMapping.setAttributeId(relationAttr.getAttributeId());
      cmsCIRelationAndRelationAttributesActionMapping.setAttributeName(relationAttr.getAttributeName());
     // cmsCIRelationAttribute.setComments("updated for circuit consolidation");
      
      // get values from pack definition
      log.info("cmsCIRelationTypeKey_target: "+cmsCIRelationTypeKey_target);
      log.info("cmsCIRelationTypesInpackDefinitionMap: "+gson.toJson(cmsCIRelationTypesInpackDefinitionMap));
      CmsCIRelationModel CmsCIRelationFromPackDefinition=cmsCIRelationTypesInpackDefinitionMap.get(cmsCIRelationTypeKey_target);
      log.info("CmsCIRelationFromPackDefinition: "+gson.toJson(CmsCIRelationFromPackDefinition));
      
      String dfValueFromPackDefinition=CmsCIRelationFromPackDefinition.getAttributes().get(relationAttr.getAttributeName()).getDfValue();
      String djValueFromPackDefinition=CmsCIRelationFromPackDefinition.getAttributes().get(relationAttr.getAttributeName()).getDjValue();
      log.info("dfValueFromPackDefinition: "+dfValueFromPackDefinition);
      log.info("djValueFromPackDefinition: "+djValueFromPackDefinition);
 
      if (!relationAttr.isMandatory() && StringUtils.isEmpty(dfValueFromPackDefinition) && StringUtils.isEmpty(djValueFromPackDefinition)) {
        continue;
      }
      
      cmsCIRelationAndRelationAttributesActionMapping.setDfValue(dfValueFromPackDefinition);
      cmsCIRelationAndRelationAttributesActionMapping.setDjValue(djValueFromPackDefinition);

      cmsCIRelationAndRelationAttributesActionMappingsList
      .add(cmsCIRelationAndRelationAttributesActionMapping);

    }
    

  }

  private Map<String, CmsCIRelationModel> createCmsCIRelationTypesForAllCIRelationsInPack(
      List<CmsCIRelationModel> cmsCIRelationList) {
    Map<String, CmsCIRelationModel> cmsCIRelationTypes = new TreeMap<String, CmsCIRelationModel>();

    for (CmsCIRelationModel cmsCIRelation : cmsCIRelationList) {

      String key =
          cmsCIRelation.getRelationName() + "|" + getMappingKey(cmsCIRelation.getFromCiClazz())
              + "|" + getMappingKey(cmsCIRelation.getToCiClazz());
      cmsCIRelationTypes.put(key, cmsCIRelation);

    }
    return cmsCIRelationTypes;
  }

  private String getMappingKey(String str) {

    String[] strArr = str.split("\\.");

    String prefix = strArr[0];
    String suffix = strArr[strArr.length - 1];

    String[] suffixArr = suffix.split("_");
    String refinedSuffix = suffixArr[suffixArr.length - 1];

    String key = prefix + refinedSuffix;
    return key.toLowerCase();

  }

  @Test(enabled = false)
  private void testGetMappingKey() {

    String Wmt_keyspace = "catalog.walmartlabs.Wmt_keyspace";
    String Apache_cassandra = "catalog.walmartlabs.Apache_cassandra";
    String Jolokia_proxy = "catalog.walmartlabs.Jolokia_proxy";
    String Secgroup = "catalog.Secgroup";

    String Wmt_keyspacekey = getMappingKey(Wmt_keyspace);
    String Apache_cassandrakey = getMappingKey(Apache_cassandra);
    String Jolokia_proxyKey = getMappingKey(Jolokia_proxy);
    String SecgroupKey = getMappingKey(Secgroup);


    log.info("Wmt_keyspacekey: " + Wmt_keyspacekey);
    log.info("Apache_cassandrakey: " + Apache_cassandrakey);
    log.info("Jolokia_proxyKey: " + Jolokia_proxyKey);
    log.info("SecgroupKey: " + SecgroupKey);

    assertEquals(Wmt_keyspacekey, "catalogkeyspace");
    assertEquals(Apache_cassandrakey, "catalogcassandra");
    assertEquals(Jolokia_proxyKey, "catalogproxy");
    assertEquals(SecgroupKey, "catalogsecgroup");


  }

}
