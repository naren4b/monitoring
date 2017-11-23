package com.naren.target;

import com.naren.sample.grpc.helloworld.client.HelloWorldClient;

public class MonitoringGreeterTarget implements MonitoringTarget {

	private HelloWorldClient client = new HelloWorldClient();

	public MonitoringGreeterTarget() {
		client.activate();
	}

	@Override
	public String metrics() {
		return client.metrics();
	}

}
