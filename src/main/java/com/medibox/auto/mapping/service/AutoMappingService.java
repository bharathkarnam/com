package com.medibox.auto.mapping.service;

import java.util.List;

import com.medibox.auto.mapping.response.ResponseDTO;
import com.medibox.data.models.MasterProduct;


public interface AutoMappingService {

	ResponseDTO mapAllunMappedDistributors();
	List<MasterProduct> getAllMasterProducts();
	long getCountDistributor();
}
