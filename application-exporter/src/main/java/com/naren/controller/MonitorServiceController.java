package com.naren.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.naren.exception.ApplicationExporterException;
import com.naren.target.MonitoringGreeterTarget;
import com.naren.target.MonitoringTarget;

@RestController
public class MonitorServiceController {

	Map<String, MonitoringTarget> targetList = new HashMap<>();

	MonitorServiceController() {
		targetList.put("greeter", new MonitoringGreeterTarget());
	}

	@RequestMapping(method = RequestMethod.GET, value = "/metrics")
	public String metrics() {
		return "Application Metric Monitoring ";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{target}/metrics", produces = {
			"text/plain; version=0.0.4; charset=utf-8" })
	public String metrics(@PathVariable String target) throws ApplicationExporterException {
		return targetList.get(target).metrics();
	}
}
