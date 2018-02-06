package com.medibox.auto.mapping.dao.service;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.medibox.data.models.MasterProduct;

public interface MasterProductDAO extends MongoRepository<MasterProduct, String> {

	@Query("{ 'name' : {$regex: ?0, $options: 'i'}, 'brand' : '?1', 'packSize': {$regex: ?2, $options: 'i'}, 'marketingCompany.name': {$regex: ?3, $options: 'i'}}")
	List<MasterProduct> getMasterProductsProductsByNameBrandPckSzandMC(String distributorProductName, String brand,
			String distributorpackSize, String distributormarketingCompany);

	@Query(value = "{ 'masterProductId' : null}", count = true)
	long getcount();

	@Query("{ 'name' : {$regex: ?0, $options: 'i'}, 'packSize' : {$regex: ?1, $options: 'i'}, 'marketingCompany.name':{$regex: ?2, $options: 'i'}}")
	List<MasterProduct> getMasterProductsByNamePckSzandMC(String distributorproductname,
			String distributorpackSize, String distributormarketingCompany);

	@Query("{ 'name' : {$regex: ?0, $options: 'i'}, 'brand' : '?1', 'marketingCompany.name':{$regex: ?2, $options: 'i'}}")
	List<MasterProduct> getMasterProductsProductsByNamebrandandMC(String distributorproductname,
			String distributorbrandname, String distributormarketingCompany);

	@Query("{ 'name' : {$regex: ?0, $options: 'i'}, 'brand' : '?1', 'packSize':{$regex: ?2, $options : 'i'}}")
	List<MasterProduct> getMasterProductsProductsByNamebrandandPckSZ(String distributorproductname,
			String distributorbrandname, String distributorpackSize);
}
