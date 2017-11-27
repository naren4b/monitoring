package com.naren.monitoring.prometheus;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.naren.sample.grpc.api.GreeterGrpc;
import com.naren.sample.grpc.api.GreeterGrpc.GreeterBlockingStub;
import com.naren.sample.grpc.api.HelloRequest;
import com.naren.sample.grpc.server.internal.GreeterService;

import io.grpc.Server;
import io.grpc.ServerInterceptors;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.okhttp.OkHttpChannelBuilder;
import io.prometheus.client.Collector;
import io.prometheus.client.Collector.MetricFamilySamples;
import io.prometheus.client.CollectorRegistry;

/** Integration tests for the client-side monitoring pipeline. */
public class MonitoringClientInterceptorIntegrationTest {
	private static final Configuration ALL_METRICS = Configuration.allMetrics();

	private static final String RECIPIENT = "Jane";
	private static final HelloRequest REQUEST = HelloRequest.newBuilder().setName(RECIPIENT).build();

	private int grpcPort = 5000;
	private Server grpcServer;
	private CollectorRegistry collectorRegistry;
	private GreeterService service = new GreeterService();

	@Before
	public void setUp() {
		collectorRegistry = new CollectorRegistry();

		startGrpcServer();
	}

	@After
	public void tearDown() throws Throwable {
		grpcServer.shutdown().awaitTermination();
	}

	@Test
	public void unaryRpcMetrics() throws Throwable {
		createClientStub(ALL_METRICS).sayHello(REQUEST);
		Collector.MetricFamilySamples handled = findRecordedMetricOrThrow("grpc_client_completed");
		assertThat(handled.samples).hasSize(1);

	}

	private GreeterBlockingStub createClientStub(Configuration configuration) {
		return GreeterGrpc.newBlockingStub(OkHttpChannelBuilder.forAddress("localhost", grpcPort)
				.intercept(MonitoringClientInterceptor.create(configuration.withCollectorRegistry(collectorRegistry)))
				.usePlaintext(true).build());
	}

	private void startGrpcServer() {
		grpcServer = NettyServerBuilder.forPort(grpcPort)
				.addService(ServerInterceptors.intercept(GreeterGrpc.bindService(service))).build();

		try {
			grpcServer.start();
		} catch (IOException e) {
			throw new RuntimeException("Exception while running grpc server", e);
		}
	}

	private MetricFamilySamples findRecordedMetricOrThrow(String name) {
		return RegistryHelper.findRecordedMetricOrThrow(name, collectorRegistry);
	}

}
