
package com.ezc.aragenPR.webapp.persistance.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ezc.aragenPR.webapp.model.EzValueMapping;
import com.ezc.aragenPR.webapp.model.EzValueMappingKey;

public interface ValueMappingRepo extends JpaRepository<EzValueMapping,EzValueMappingKey> {
	
	List<EzValueMapping> findByMapKey(String mapKey);
	
	List<EzValueMapping> findByMapKeyIn(List<String> mapKeyList);

	EzValueMapping findByMapKeyAndValue1(String mapKey, String value1);
	
	@Modifying
	@Query(value="delete from ezc_value_mapping where evm_map_key='CHAINMAP' and evm_value1=:value1",nativeQuery = true)
	void deleteChainMap(String value1);
	
	
	@Query(value="select * from ezc_value_mapping where evm_map_key='CHAINMAP' and evm_value1=:value1",nativeQuery = true)
	List<EzValueMapping> getChainMap(String value1);

	EzValueMapping findByMapKeyAndValue2(String locmap, String plant);
}

