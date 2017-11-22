package com.naren.sample.grpc.server;

import com.naren.daaas.prometheus.GrpcMetricService;
import com.naren.sample.grpc.server.GrpcServer;

public class ServerUtility {

	public static void main(String[] args) throws Exception {
		System.out.println("Testing");

		final GrpcServer greetServer = new GreetServerImpl();
		greetServer.activate();
		greetServer.blockUntilShutdown();
		GrpcMetricService.metrics();
		System.out.println("Ended");
		
	}
}
