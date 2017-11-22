package com.naren.sample.grpc.helloworld.client;

import java.io.IOException;

public class ClientUtility {

	public static void main(String[] args) throws IOException, InterruptedException {
		HelloWorldClient client = new HelloWorldClient();
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
