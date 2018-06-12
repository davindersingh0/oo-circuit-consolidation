package com.oneops.circuitconsolidation.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.google.gson.Gson;
import com.oneops.circuitconsolidation.mappings.CiRelationsMappingsTest;
import com.oneops.circuitconsolidation.mappings.dal.OOConsolidationMapper;
import com.oneops.circuitconsolidation.model.CmsCIRelationAndRelationAttributesActionMappingsModel;
import com.oneops.circuitconsolidation.model.CmsCIRelationModel;

/**
 * @author dsing17 This class is created to generate CMSCi mapping Clazzes data for populating
 *         ci_clazzes_transformation_map.json file
 */
public class CMSDalUtil_MDClazzRelations {

  private final Logger log = LoggerFactory.getLogger(CiRelationsMappingsTest.class);

  private ApplicationContext context;
  private Gson gson = new Gson();
  private OOConsolidationMapper oOConsolidationMapper;

  @BeforeClass
  private void init() {
    CircuitConsolidationMain app = new CircuitConsolidationMain();
    app.loadApplicationContext();
    context = app.getContext();
    oOConsolidationMapper = context.getBean(OOConsolidationMapper.class);

  }

  // :TODO: update Comments for each relation since clazz names have changed, though it does not
  // impact functionlaity but for cleanup purpose
  // String ooPhase = IConstants.DESIGN_PHASE;
  @Test(enabled = true)
  private void createCiRelationsMappings() {

    String sourcePackCatlogName = "walmartlabs-apache_cassandra";
    String targetPackCatlogName = "oneops-apache_cassandra";

    String ooPhase = IConstants.DESIGN_PHASE;
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
        continue;
      }

    }


    return cmsCIRelationAndRelationAttributesActionMappingsList;
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



    /*
     * assertEquals(Wmt_keyspacekey, "catalogkeyspace"); assertEquals(Apache_cassandrakey,
     * "catalogcassandra");
     */

  }

}
