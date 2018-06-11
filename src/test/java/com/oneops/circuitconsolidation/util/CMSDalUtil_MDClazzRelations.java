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
import com.oneops.cms.cm.dal.CIMapper;

/**
 * @author dsing17 This class is created to generate CMSCi mapping Clazzes data for populating
 *         ci_clazzes_transformation_map.json file
 */
public class CMSDalUtil_MDClazzRelations {



  private final Logger log = LoggerFactory.getLogger(CiRelationsMappingsTest.class);

  private ApplicationContext context;
  private Gson gson = new Gson();
  private CIMapper ciMapper;
  private OOConsolidationMapper oOConsolidationMapper;

  @BeforeClass
  private void init() {
    CircuitConsolidationMain app = new CircuitConsolidationMain();
    app.loadApplicationContext();
    context = app.getContext();
    ciMapper = context.getBean(CIMapper.class);
    oOConsolidationMapper = context.getBean(OOConsolidationMapper.class);

  }


  @Test(enabled = true)
  private void getAllCIsForNsPath() {

    // :TODO: update Comments for each relation since clazz names have changed, though it does not
    // impact functionlaity but for cleanup purpose
    String ooPhase = IConstants.DESIGN_PHASE;
    // String ooPhase = IConstants.TRANSITION_PHASE;
    String envName = "dev";


    String ns_sourcePack = "/TestOrg2/ms-wmtlabs-a-cass";
    String platformName_sourcePack = "cass";
    String nsForPlatformCiComponents_sourcePack = CircuitconsolidationUtil
        .getnsForPlatformCiComponents(ns_sourcePack, platformName_sourcePack, ooPhase, envName);


    String ns_targetPack = "/TestOrg2/ms-oneops-a-cass";
    String platformName_targetPack = "oneops-apache-cassandra";
    String nsForPlatformCiComponents_targetPack = CircuitconsolidationUtil
        .getnsForPlatformCiComponents(ns_targetPack, platformName_targetPack, ooPhase, envName);


    List<CmsCIRelationModel> cmsCIRelation_nsForPlatformCiComponents_sourcePack =
        oOConsolidationMapper.getCIRelations(nsForPlatformCiComponents_sourcePack, null, null, null,
            null, null, null);

    log.info(
        "cmsCIRelation_nsForPlatformCiComponents_sourcePack <size> : {}, jsonfiled cmsCIRelation_nsForPlatformCiComponents_sourcePack: {}",
        cmsCIRelation_nsForPlatformCiComponents_sourcePack.size(),
        gson.toJson(cmsCIRelation_nsForPlatformCiComponents_sourcePack));

    Map<String, CmsCIRelationModel> cmsCIRelationTypes_sourcePack =
        getCmsCIRelationTypesForAllCIRelationsInPack(
            cmsCIRelation_nsForPlatformCiComponents_sourcePack);

    log.info(
        "cmsCIRelationTypes_sourcePack <size> : {}, jsonfiled cmsCIRelationTypes_sourcePack: {}",
        cmsCIRelationTypes_sourcePack.size(), gson.toJson(cmsCIRelationTypes_sourcePack));

    List<CmsCIRelationModel> cmsCIRelation_nsForPlatformCiComponents_targetPack =
        oOConsolidationMapper.getCIRelations(nsForPlatformCiComponents_targetPack, null, null, null,
            null, null, null);

    log.info(
        "cmsCIRelation_nsForPlatformCiComponents_targetPack <size> : {}, jsonfiled cmsCIRelation_nsForPlatformCiComponents_targetPack: {}",
        cmsCIRelation_nsForPlatformCiComponents_targetPack.size(),
        gson.toJson(cmsCIRelation_nsForPlatformCiComponents_targetPack));

    Map<String, CmsCIRelationModel> cmsCIRelationTypes_targetPack =
        getCmsCIRelationTypesForAllCIRelationsInPack(
            cmsCIRelation_nsForPlatformCiComponents_targetPack);

    log.info(
        "cmsCIRelationTypes_targetPack <size> : {}, jsonfiled cmsCIRelationTypes_targetPack: {}",
        cmsCIRelationTypes_targetPack.size(), gson.toJson(cmsCIRelationTypes_targetPack));

    List<CmsCIRelationAndRelationAttributesActionMappingsModel> cmsCIRelationAndRelationAttributesActionMappingsList =
        createCmsCIRelationAndRelationAttributesActionMappings("walmartlabs-apache_cassandra",
            cmsCIRelationTypes_sourcePack, "oneops-apache_cassandra",
            cmsCIRelationTypes_targetPack);
    log.info("jsonifiled cmsCIRelationAndRelationAttributesActionMappingsList: "
        + gson.toJson(cmsCIRelationAndRelationAttributesActionMappingsList));

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
            .setSourceCmsCiRelationName(sourceCmsCIRelation.getRelationName());
        cmsCIRelationAndRelationAttributesActionMapping
            .setSourceCmsCiRelationId(sourceCmsCIRelation.getRelationId());
        cmsCIRelationAndRelationAttributesActionMapping
            .setSourceFromCmsCiClazzName(sourceCmsCIRelation.getFromCiClazz());
        cmsCIRelationAndRelationAttributesActionMapping
            .setSourceFromCmsCiClazzId(sourceCmsCIRelation.getFromCiClazzId());


        cmsCIRelationAndRelationAttributesActionMapping.setTargetPack(targetPack);
        cmsCIRelationAndRelationAttributesActionMapping
            .setTargetCmsCiRelationName(targetCmsCIRelation.getRelationName());
        cmsCIRelationAndRelationAttributesActionMapping
            .setTargetCmsCiRelationId(targetCmsCIRelation.getRelationId());
        cmsCIRelationAndRelationAttributesActionMapping
            .setTargetFromCmsCiClazzName(targetCmsCIRelation.getFromCiClazz());
        cmsCIRelationAndRelationAttributesActionMapping
            .setTargetFromCmsCiClazzId(targetCmsCIRelation.getFromCiClazzId());

        cmsCIRelationAndRelationAttributesActionMapping.setAction("NO_ACTION");

        // private String targetCmsCiRelationKey;no need to set here

        cmsCIRelationAndRelationAttributesActionMapping.setEntityType("CMSCI_RELATION");
        /*
         * cmsCIRelationAndRelationAttributesActionMappingsList
         * .add(cmsCIRelationAndRelationAttributesActionMapping);
         */ } else {

        cmsCIRelationAndRelationAttributesActionMapping.setSourcePack(sourcePack);
        cmsCIRelationAndRelationAttributesActionMapping
            .setSourceCmsCiRelationKey(cmsCIRelationTypeKey_source);
        cmsCIRelationAndRelationAttributesActionMapping
            .setSourceCmsCiRelationName(sourceCmsCIRelation.getRelationName());
        cmsCIRelationAndRelationAttributesActionMapping
            .setSourceCmsCiRelationId(sourceCmsCIRelation.getRelationId());
        cmsCIRelationAndRelationAttributesActionMapping
            .setSourceFromCmsCiClazzName(sourceCmsCIRelation.getFromCiClazz());
        cmsCIRelationAndRelationAttributesActionMapping
            .setSourceFromCmsCiClazzId(sourceCmsCIRelation.getFromCiClazzId());

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
            .setTargetCmsCiRelationName(targetCmsCIRelation.getRelationName());
        cmsCIRelationAndRelationAttributesActionMapping
            .setTargetCmsCiRelationId(targetCmsCIRelation.getRelationId());
        cmsCIRelationAndRelationAttributesActionMapping
            .setTargetFromCmsCiClazzName(targetCmsCIRelation.getFromCiClazz());
        cmsCIRelationAndRelationAttributesActionMapping
            .setTargetFromCmsCiClazzId(targetCmsCIRelation.getFromCiClazzId());

        cmsCIRelationAndRelationAttributesActionMapping.setAction("CREATE_RELATION");

        // private String targetCmsCiRelationKey;no need to set here

        cmsCIRelationAndRelationAttributesActionMapping.setEntityType("CMSCI_RELATION");

        cmsCIRelationAndRelationAttributesActionMappingsList
            .add(cmsCIRelationAndRelationAttributesActionMapping);
        continue;
      }

    }


    return cmsCIRelationAndRelationAttributesActionMappingsList;
  }


  private Map<String, CmsCIRelationModel> getCmsCIRelationTypesForAllCIRelationsInPack(
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
    log.info("strArr: " + strArr);

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
