package com.naren.sample.grpc.helloworld.client;

import com.naren.daaas.prometheus.MonitoringService;

public class MonitoringClientUtility extends Thread {

	private MonitoringService client;

	MonitoringClientUtility(MonitoringService client) {
		this.client = client;
	}

	@Override
	public void run() {
		showMetrics();
	}

	private void showMetrics() {
		try {
			while (true) {
				Thread.sleep(1000);
				String helloWorldmetrics = client.metrics();
				System.out.println(helloWorldmetrics);
			}

		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

}
