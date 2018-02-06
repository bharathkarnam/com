package com.medibox.auto.mapping.dao.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.medibox.data.models.DistributorProduct;


public interface DistributorProductDAO extends MongoRepository<DistributorProduct, String> {

  //  @Query("{ 'masterProductId' : null}")
	@Query("{ 'distributorId': ?0 }")
	List<DistributorProduct> getUnmappedDistributorProducts(ObjectId id);
    
    @Query(value = "{ 'masterProductId' : null}", count= true)
    long getcount();
    
    
}
