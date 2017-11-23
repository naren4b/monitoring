package com.naren.sample.grpc.helloworld.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.naren.monitoring.prometheus.GrpcMetricService;
import com.naren.monitoring.prometheus.MonitoringService;
import com.naren.sample.grpc.api.GreeterGrpc;
import com.naren.sample.grpc.api.HelloReply;
import com.naren.sample.grpc.api.HelloRequest;
import com.naren.sample.grpc.api.MetricReply;
import com.naren.sample.grpc.api.MetricRequest;
import com.naren.sample.grpc.server.GrpcServer;

import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.okhttp.OkHttpChannelBuilder;

public class HelloWorldClient implements MonitoringService {

	private static final Logger LOG = LoggerFactory.getLogger(HelloWorldClient.class);

	private final String host = "localhost";
	private final int port = 5000;
	private ManagedChannel channel;
	private GreeterGrpc.GreeterBlockingStub blockingStub;

	private List<ClientInterceptor> interceptor;

	public HelloWorldClient() {
		interceptor = new ArrayList<>();
	}

	public HelloWorldClient(List<ClientInterceptor> interceptor) {
		this.interceptor = interceptor;
	}

	public void activate() {

		channel = OkHttpChannelBuilder.forAddress(host, port).intercept(interceptor).usePlaintext(true).build();
		blockingStub = GreeterGrpc.newBlockingStub(channel);

	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

	/**
	 * Say hello to server.
	 */
	public void greet(String name) {
		LOG.info("Will try to greet " + name + " ...");
		HelloRequest request = HelloRequest.newBuilder().setName(name).build();
		HelloReply response;
		try {
			response = blockingStub.sayHello(request);
		} catch (StatusRuntimeException e) {
			LOG.warn("RPC failed: {0}", e.getStatus());
			return;
		}
		System.out.println(response.getMessage());
		LOG.info("Greeting: " + response.getMessage());
	}

	public void setGrpcServer(GrpcServer grpcServer) {
		// ensures the server has started before we attempt to connect
	}

	@Override
	public String metrics() {

		LOG.info("Will try to get metrics ");

		MetricRequest request = MetricRequest.newBuilder().setName("Hello World").build();
		MetricReply response;
		try {
			response = blockingStub.metric(request);
		} catch (StatusRuntimeException e) {
			LOG.warn("RPC failed: {0}", e.getStatus());
			return "";
		}

		LOG.info("Greeting: " + response.getMetric());
		return response.getMetric() + GrpcMetricService.metrics();

	}

}
