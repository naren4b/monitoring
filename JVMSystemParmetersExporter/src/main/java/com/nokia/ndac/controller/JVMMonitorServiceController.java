package com.nokia.ndac.controller;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.ndac.bean.SystemParameter;
import com.nokia.ndac.exception.JVMSystemParmetersExporterException;
import com.nokia.ndac.repository.SystemParmeterRepository;
import com.nokia.ndac.system.MonitoredSystem;
import com.nokia.ndac.system.MySystem;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Summary;
import io.prometheus.client.exporter.common.TextFormat;

@RestController
@RequestMapping("/SystemParmeters")
public class JVMMonitorServiceController {

	@Autowired
	SystemParmeterRepository systemParmetersRepo;

	private final Counter promRequestsTotal = Counter.build().name("requests_total").help("Total number of requests.")
			.register();
	private static final Gauge inprogressRequests = Gauge.build().name("inprogress_requests")
			.help("Inprogress requests.").register();
	private static final Summary receivedBytes = Summary.build().name("requests_size_bytes")
			.help("Request size in bytes.").register();
	static final Summary requestLatency = Summary.build().name("requests_latency_seconds")
			.help("Request latency in seconds.").register();

	@RequestMapping(method = RequestMethod.GET)
	public List<SystemParameter> getAllSystemParmeters(HttpServletRequest req) throws Exception {
		Summary.Timer requestTimer = requestLatency.startTimer();
		List<SystemParameter> stat = new ArrayList<SystemParameter>();
		try {

			promRequestsTotal.inc();
			inprogressRequests.inc();
			List<SystemParameter> systemParams = systemParmetersRepo.findAll();
			MonitoredSystem system = new MySystem(systemParams);
			stat = system.stat(systemParams);
			inprogressRequests.dec();
			// Your code here.
		} finally {
			receivedBytes.observe(req.getContentLength());
			requestTimer.observeDuration();
		}
		return stat;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{name}")
	public SystemParameter getSystemParmeters(@PathVariable String name) throws Exception {
		SystemParameter systemParameter = validate(name);
		MonitoredSystem system = new MySystem(Arrays.asList(systemParameter));
		return system.stat(Arrays.asList(systemParameter)).stream().findFirst().get();
	}

	@RequestMapping(path = "/prometheus")
	public void metrics(Writer responseWriter) throws IOException {
		TextFormat.write004(responseWriter, CollectorRegistry.defaultRegistry.metricFamilySamples());
		responseWriter.close();
	}

	private SystemParameter validate(String name) {
		return systemParmetersRepo.findByName(name).orElseThrow(
				() -> new JVMSystemParmetersExporterException("SystemParmeter Type not found for the name: " + name));
	}

}
