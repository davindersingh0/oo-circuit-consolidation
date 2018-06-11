package com.oneops.circuitconsolidation.mappings;

import java.util.List;
import org.springframework.context.ApplicationContext;
import com.oneops.circuitconsolidation.mappings.dal.OOConsolidationMapper;
import com.oneops.circuitconsolidation.util.CircuitConsolidationMain;

public class CircuitConsolidationMappingsMain {



  public static void main(String[] args) {

    CircuitConsolidationMain app = new CircuitConsolidationMain();
    app.loadApplicationContext();
    ApplicationContext context = app.getContext();

    String sourcePack = "wmtlabs-apache_cassandra";
    String targetPack = "oneops-apache_cassandra";

    MappingBuilder mappingBuilder = context.getBean(MappingBuilder.class);


    List<CmsCiAndCmsCiAttributesActionMappingsModel> cmsCiAndCmsCiAttributesActionMappingsList =
        mappingBuilder.createCmCiAttributesMappings(sourcePack, targetPack);
    OOConsolidationMapper ooConsolidationMapper = context.getBean(OOConsolidationMapper.class);

   for (CmsCiAndCmsCiAttributesActionMappingsModel cmsCiAndCmsCiAttributesActionMappings : cmsCiAndCmsCiAttributesActionMappingsList) {
      ooConsolidationMapper
          .populateCmsCiAndCmsCiAttributesActionMappings(cmsCiAndCmsCiAttributesActionMappings);
    }


  }



}
