package com.psl.cc.analytics.controller;

import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Transactional
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = { "Customers" })
public class TestController {
	@ApiOperation(value = "List of customers", response = String.class)
	@RequestMapping(value = "/customers", method = RequestMethod.GET)
	public String getCustomersByPage() {
		return "Hello";
	}
}
