/*
 * This table is created to store CmsCi & CmsCiAttributes transformation mappings & action flag
 * Following is the sample data for the table
	{
  String fromCiClazz;
  String toCiClazz;
  String relationName;
  List<CiRelationDefaultAttributeModel> ciRelationDefaultAttributeList =
      new ArrayList<CiRelationDefaultAttributeModel>();
      
	}
*/
--DROP TABLE kloopzcm.CiClazzRelationMappings;

/*
 * 
 * 
CREATE TABLE kloopzcm.CiClazzRelationMappings (
		sourcePack VARCHAR(200) ,
		targetPack VARCHAR(200) ,
		relationId INTEGER NOT NULL,
		relationName VARCHAR(200) ,		
		fromCiClazz VARCHAR(200) NOT NULL,
		FromCiClazzId INTEGER NOT NULL,
		toCiClazz VARCHAR(200) NOT NULL,
		toCiClazzId INTEGER NOT NULL,
		created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
*/
--DROP TABLE kloopzcm.CmsCIRelationAndRelationAttributesActionMappings;
CREATE TABLE kloopzcm.CmsCIRelationAndRelationAttributesActionMappings (
		sourcePack VARCHAR(200) ,
		targetPack VARCHAR(200) ,
		sourceCmsCiRelationKey VARCHAR(200),
		sourceCmsCiRelationName VARCHAR(200),
		sourceCmsCiRelationId INTEGER,
		sourceFromCmsCiClazzName VARCHAR(200),
		sourceFromCmsCiClazzId INTEGER,
		
		targetCmsCiRelationKey VARCHAR(200),
		targetCmsCiRelationName VARCHAR(200),
		targetCmsCiRelationId INTEGER,
		targetFromCmsCiClazzName VARCHAR(200),		
		targetFromCmsCiClazzId VARCHAR(200),		
		action VARCHAR(200),	
		entityType VARCHAR(200),	
		created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
			
		
);
