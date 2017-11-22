package com.naren.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metrics")
public class MonitorServiceController {

	@RequestMapping(method = RequestMethod.GET)
	public String metrics(HttpServletRequest req) throws Exception {
		return "hello";
	}

}
