package com.naren.monitoring.prometheus;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.naren.sample.grpc.api.GreeterGrpc;
import com.naren.sample.grpc.api.HelloRequest;
import com.naren.sample.grpc.api.MetricReply;
import com.naren.sample.grpc.api.MetricRequest;
import com.naren.sample.grpc.server.internal.GreeterService;

import io.grpc.Channel;
import io.grpc.Server;
import io.grpc.ServerInterceptors;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.NettyServerBuilder;
import io.prometheus.client.Collector.MetricFamilySamples;
import io.prometheus.client.CollectorRegistry;

/**
 * Integrations tests which make sure that if a service is started with a
 * {@link MonitoringServerInterceptor}, then all Prometheus metrics get recorded
 * correctly.
 */
public class MonitoringServerInterceptorIntegrationTest {

	private final int grpcPort = 5000;
	private Server grpcServer;
	private GreeterService service = new GreeterService();
	private static final String RECIPIENT = "Dave";
	private static final HelloRequest REQUEST = HelloRequest.newBuilder().setName(RECIPIENT).build();
	private static final MetricRequest METRIC_REQUEST = MetricRequest.newBuilder().setName(RECIPIENT).build();

	private static final Configuration ALL_METRICS = Configuration.allMetrics();

	private CollectorRegistry collectorRegistry;

	@Before
	public void setUp() {
		collectorRegistry = new CollectorRegistry();
	}

	@After
	public void tearDown() throws Exception {
		grpcServer.shutdown().awaitTermination();
	}

	@Test
	public void sayHelloServiceMetricTest() throws Throwable {
		MonitoringServerInterceptor interceptor = MonitoringServerInterceptor
				.create(ALL_METRICS.withCollectorRegistry(collectorRegistry));
		startGrpcServer(interceptor);
		createGrpcBlockingStub().sayHello(REQUEST);
		assertThat(findRecordedMetricOrThrow("grpc_server_started_total").samples).hasSize(1);
		assertThat(findRecordedMetricOrThrow("grpc_server_msg_received_total").samples).hasSize(1);
		assertThat(findRecordedMetricOrThrow("grpc_server_msg_sent_total").samples).hasSize(1);

		MetricFamilySamples handled = findRecordedMetricOrThrow("grpc_server_handled_total");
		assertThat(handled.samples).hasSize(1);
		assertThat(handled.samples.get(0).labelValues).containsExactly("UNARY", GreeterService.SERVICE_NAME,
				GreeterService.UNARY_METHOD_NAME, "OK");
		assertThat(handled.samples.get(0).value).isWithin(0).of(1);
	}

	@Test
	public void metricServiceMetricTest() throws Throwable {
		MonitoringServerInterceptor monitoringInterceptor = MonitoringServerInterceptor
				.create(Configuration.allMetrics());
		startGrpcServer(monitoringInterceptor);
		MetricReply metric = createGrpcBlockingStub().metric(METRIC_REQUEST);
		String result = metric.getMetric();
		assertTrue(result.contains("grpc_server_started_total"));
	}

	private void startGrpcServer(MonitoringServerInterceptor interceptor) {

		grpcServer = NettyServerBuilder.forPort(grpcPort)
				.addService(ServerInterceptors.intercept(GreeterGrpc.bindService(service), interceptor)).build();

		try {
			grpcServer.start();
		} catch (IOException e) {
			throw new RuntimeException("Exception while running grpc server", e);
		}
	}

	private MetricFamilySamples findRecordedMetricOrThrow(String name) {
		return RegistryHelper.findRecordedMetricOrThrow(name, collectorRegistry);
	}

	private GreeterGrpc.GreeterBlockingStub createGrpcBlockingStub() {
		return GreeterGrpc.newBlockingStub(createGrpcChannel());
	}

	private Channel createGrpcChannel() {
		return NettyChannelBuilder.forAddress("localhost", grpcPort).negotiationType(NegotiationType.PLAINTEXT).build();
	}

}
