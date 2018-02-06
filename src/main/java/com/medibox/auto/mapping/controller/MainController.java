package com.medibox.auto.mapping.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.medibox.auto.mapping.service.AutoMappingService;
import com.medibox.auto.mapping.util.Constants;

@RestController
@RequestMapping("/api")
public class MainController {

	@Autowired
	AutoMappingService service;

	@RequestMapping(method = RequestMethod.GET, path = "/startautomapping", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, String>> async() {
		service.mapAllunMappedDistributors();
		Map<String, String> response = new HashMap<>();
		response.put(Constants.MESSAGE, Constants.REQUEST_IS_UNDER_PROCESS);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, path = "/ping", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<Map<String, String>> home() {
		Map<String, String> response = new HashMap<>();
		response.put(Constants.MESSAGE, Constants.I_AM_ALIVE);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@Scheduled( cron = "${distributorProductsMappingCron}" )
	void triggerDistributorProductIndexJob(){
		service.mapAllunMappedDistributors();
	}

}
