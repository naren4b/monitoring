package com.naren.sample.grpc.helloworld.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.naren.monitoring.prometheus.Configuration;
import com.naren.monitoring.prometheus.MonitoringClientInterceptor;

import io.grpc.ClientInterceptor;

public class ClientUtility {

	static MonitoringClientInterceptor monitoringInterceptor = MonitoringClientInterceptor
			.create(Configuration.cheapMetricsOnly());

	public static void main(String[] args) throws IOException, InterruptedException {
		// fullStart();
		sayHello();
	}

	private static void sayHello() throws InterruptedException {
		HelloWorldClient client = new HelloWorldClient();
		client.activate();
		client.greet("world ");
		client.shutdown();

	}

	private static void fullStart() {
		List<ClientInterceptor> interceptors = new ArrayList();
		interceptors.add(monitoringInterceptor);
		HelloWorldClient client = new HelloWorldClient(interceptors);
		MonitoringClientUtility mcu = new MonitoringClientUtility(client);
		mcu.start();
		client.activate();
		try {
			int i = 0;
			while (true) {
				client.greet("world " + i++);
				Thread.sleep(1000);
				if (i > 5000) {
					break;
				}
			}

			client.shutdown();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

}
