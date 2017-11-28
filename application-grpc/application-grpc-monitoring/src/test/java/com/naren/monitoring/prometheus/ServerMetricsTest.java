package com.naren.monitoring.prometheus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.naren.monitoring.prometheus.ServerMetrics.Factory;

import io.grpc.MethodDescriptor.MethodType;
import io.grpc.Status.Code;
import io.prometheus.client.Collector.MetricFamilySamples;
import io.prometheus.client.CollectorRegistry;

public class ServerMetricsTest {

	private Factory factory;
	private ServerMetrics metrics;
	private CollectorRegistry collectorRegistry;
	private static final Configuration ALL_METRICS = Configuration.allMetrics();

	@Before
	public void setUp() throws Exception {
		collectorRegistry = new CollectorRegistry();
		
	}

	private void config(Configuration configuration) {
		factory = new ServerMetrics.Factory(configuration.withCollectorRegistry(collectorRegistry));
		GrpcMethod grpcMethod = GrpcMethod.of("HelloWorldService", "SayHello", MethodType.UNARY);
		metrics = factory.createMetricsForMethod(grpcMethod);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetServerMetrics() {
		config(ALL_METRICS);
		assertNotNull(metrics);
	}

	@Test
	public void testRecordCallStarted() {
		config(ALL_METRICS);
		metrics.recordCallStarted();
		assertEquals(1, findRecordedMetricOrThrow("grpc_server_started_total").samples.size());
	}

	@Test
	public void testRecordServerHandled() {
		config(ALL_METRICS);
		metrics.recordServerHandled(Code.OK);
		assertEquals(1, findRecordedMetricOrThrow("grpc_server_handled_total").samples.size());
	}

	@Test
	public void testRecordStreamMessageSent() {
		config(ALL_METRICS);
		metrics.recordStreamMessageSent();
		assertEquals(1, findRecordedMetricOrThrow("grpc_server_msg_sent_total").samples.size());
	}

	@Test
	public void testRecordStreamMessageReceived() {
		config(ALL_METRICS);
		metrics.recordStreamMessageReceived();
		assertEquals(1, findRecordedMetricOrThrow("grpc_server_msg_received_total").samples.size());
	}

	@Test
	public void addsHistogramIfEnabled() throws Throwable {
		config(ALL_METRICS);
		int latencySec = 2;
		metrics.recordLatency(latencySec);
		assertTrue(RegistryHelper.findRecordedMetric("grpc_server_handled_latency_seconds", collectorRegistry)
				.isPresent());
	}
	@Test
	public void addsHistogramIfDisabled() throws Throwable {
		config(Configuration.cheapMetricsOnly());
		int latencySec = 2;
		metrics.recordLatency(latencySec);
		assertFalse(RegistryHelper.findRecordedMetric("grpc_server_handled_latency_seconds", collectorRegistry)
				.isPresent());
	}

	@Test
	public void testRrecordLatency() throws Throwable {
		config(ALL_METRICS);
		int latencySec = 2;
		metrics.recordLatency(latencySec);

		MetricFamilySamples latency = findRecordedMetricOrThrow("grpc_server_handled_latency_seconds");
		assertEquals(Boolean.TRUE, (latency.samples.size()) > 0);
	}

	private MetricFamilySamples findRecordedMetricOrThrow(String name) {
		return RegistryHelper.findRecordedMetricOrThrow(name, collectorRegistry);
	}

}
