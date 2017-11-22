package com.naren.sample.grpc.server;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.naren.daaas.prometheus.Configuration;
import com.naren.daaas.prometheus.MonitoringServerInterceptor;
import com.naren.sample.grpc.api.GreeterGrpc;
import com.naren.sample.grpc.server.internal.GreeterService;

import io.grpc.Server;
import io.grpc.ServerInterceptors;
import io.grpc.netty.NettyServerBuilder;

public class GreetServerImpl implements GrpcServer {

	private static final Logger LOG = LoggerFactory.getLogger(GreetServerImpl.class);
	private final int port = 5000;
	private Server server;
	private GreeterService service = new GreeterService();

	@Override
	public void activate() throws Exception {
		start();
		System.out.println("Server started, listening on " + port);
	}

	private void start() throws Exception {
		try {
			if (service == null) {
				throw new Exception("please set the service.");
			}
			MonitoringServerInterceptor monitoringInterceptor = MonitoringServerInterceptor
					.create(Configuration.cheapMetricsOnly());
			server = NettyServerBuilder.forPort(port)
					.addService(ServerInterceptors.intercept(GreeterGrpc.bindService(service), monitoringInterceptor))
					.build().start();
			LOG.info("Server started, listening on {}", port);
			CompletableFuture.runAsync(() -> {
				try {
					server.awaitTermination();
				} catch (InterruptedException ex) {
					LOG.error(ex.getMessage(), ex);
				}
			});
		} catch (IOException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	@Override
	public void deactivate() {
		if (server != null) {
			server.shutdown();
		}
	}

	/**
	 * Await termination on the main thread since the grpc library uses daemon
	 * threads.
	 */
	@Override
	public void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			server.awaitTermination();
		}
	}

}
