package com.naren.monitoring.prometheus;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;

public class GrpcMetricService {
	private static final Logger LOGGER = LoggerFactory.getLogger(GrpcMetricService.class.getName());

	private GrpcMetricService() {
	}

	public static String metrics() {
		Writer writer = new StringWriter();
		try {
			TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples());
		} catch (IOException e) {

			LOGGER.error(
					"Prometheus writer failed to write out the text version 0.0.4 of the given MetricFamilySamples.",
					e);
		}
		return writer.toString();
	}

}
