/*
 * This table is created to store CmsCi & CmsCiAttributes transformation mappings & action flag
 * Following is the sample data for the table
	{
		"sourcePack": "walmartlabs-apache_cassandra",
		"sourceClassname": "catalog.Cassandra",
		"sourceClassId": 10227,
		"sourceAttributeName": "cluster",
		"sourceAttributeId": 10174,
		"sourceDefaultValue": "TestCluster",
		"targetPack": "oneops-apache_cassandra",
		"targetClassname": "catalog.oneops.1.Cassandra",
		"targetClassId": 1997,
		"targetAttributeName": "cluster",
		"targetAttributeId": 1941,
		"targetDefaultValue": "TestCluster",
		"action": "UPDATE_SOURCE_ATTRIBUTE_ID",
		"entityType": "CMCI_ATTRIBUTE"
	}
*/
CREATE TABLE kloopzcm.CmsCiAndCmsCiAttributesActionMappings (
		sourcePack VARCHAR(200) NOT NULL,
		sourceClassname VARCHAR(200) NOT NULL,
		sourceClassId BIGINT NOT NULL,
		sourceAttributeName VARCHAR(200) ,
		sourceAttributeId BIGINT,
		sourceDefaultValue VARCHAR(200),
		targetPack VARCHAR(200) NOT NULL,
		targetClassname VARCHAR(200) NOT NULL,
		targetClassId BIGINT NOT NULL,
		targetAttributeName VARCHAR(200),
		targetAttributeId BIGINT,
		targetDefaultValue VARCHAR(200),
		action VARCHAR(200) NOT NULL,
		entityType VARCHAR(200) NOT NULL,
		created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

