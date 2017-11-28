package com.naren.monitoring.prometheus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import io.grpc.MethodDescriptor;
import io.grpc.Status.Code;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import io.prometheus.client.SimpleCollector;

/**
 * Prometheus metric definitions used for server-side monitoring of grpc
 * services.
 *
 * Instances of this class hold the counters we increment for a specific pair of
 * grpc service definition and collection registry.
 */
class ServerMetrics {
	private static final String GRPC_METHOD = "grpc_method";

	private static final String GRPC_SERVICE = "grpc_service";

	private static final String GRPC_TYPE = "grpc_type";

	private static final String SERVER = "server";

	private static final String GRPC = "grpc";

	private static final Counter.Builder serverStartedBuilder = Counter.build().namespace(GRPC).subsystem(SERVER)
			.name("started_total").labelNames(GRPC_TYPE, GRPC_SERVICE, GRPC_METHOD)
			.help("Total number of RPCs started on the server .");

	private static final Counter.Builder serverHandledBuilder = Counter.build().namespace(GRPC).subsystem(SERVER)
			.name("handled_total").labelNames(GRPC_TYPE, GRPC_SERVICE, GRPC_METHOD, "code")
			.help("Total number of RPCs completed on the server, regardless of success or failure .");

	private static final Histogram.Builder serverHandledLatencySecondsBuilder = Histogram.build().namespace(GRPC)
			.subsystem(SERVER).name("handled_latency_seconds").labelNames(GRPC_TYPE, GRPC_SERVICE, GRPC_METHOD)
			.help("Histogram of response latency (seconds) of gRPC that had been application-level "
					+ "handled by the server .");

	private static final Counter.Builder serverStreamMessagesReceivedBuilder = Counter.build().namespace(GRPC)
			.subsystem(SERVER).name("msg_received_total").labelNames(GRPC_TYPE, GRPC_SERVICE, GRPC_METHOD)
			.help("Total number of stream messages received from the client .");

	private static final Counter.Builder serverStreamMessagesSentBuilder = Counter.build().namespace(GRPC)
			.subsystem(SERVER).name("msg_sent_total").labelNames(GRPC_TYPE, GRPC_SERVICE, GRPC_METHOD)
			.help("Total number of stream messages sent by the server .");

	private final Counter serverStarted;
	private final Counter serverHandled;
	private final Counter serverStreamMessagesReceived;
	private final Counter serverStreamMessagesSent;
	private final Optional<Histogram> serverHandledLatencySeconds;

	private final GrpcMethod method;

	private ServerMetrics(GrpcMethod method, Counter serverStarted, Counter serverHandled,
			Counter serverStreamMessagesReceived, Counter serverStreamMessagesSent,
			Optional<Histogram> serverHandledLatencySeconds) {
		this.method = method;
		this.serverStarted = serverStarted;
		this.serverHandled = serverHandled;
		this.serverStreamMessagesReceived = serverStreamMessagesReceived;
		this.serverStreamMessagesSent = serverStreamMessagesSent;
		this.serverHandledLatencySeconds = serverHandledLatencySeconds;
	}

	public void recordCallStarted() {
		addLabels(serverStarted).inc();
	}

	public void recordServerHandled(Code code) {
		addLabels(serverHandled, code.toString()).inc();
	}

	public void recordStreamMessageSent() {
		addLabels(serverStreamMessagesSent).inc();
	}

	public void recordStreamMessageReceived() {
		addLabels(serverStreamMessagesReceived).inc();
	}

	/**
	 * Only has any effect if monitoring is configured to include latency
	 * histograms. Otherwise, this does nothing.
	 */
	public void recordLatency(double latencySec) {
		if (this.serverHandledLatencySeconds.isPresent()) {
			addLabels(this.serverHandledLatencySeconds.get()).observe(latencySec);
		}
	}

	/**
	 * Knows how to produce {@link ServerMetrics} instances for individual methods.
	 */
	static class Factory {
		private final Counter serverStarted;
		private final Counter serverHandled;
		private final Counter serverStreamMessagesReceived;
		private final Counter serverStreamMessagesSent;
		private final Optional<Histogram> serverHandledLatencySeconds;

		Factory(Configuration configuration) {
			CollectorRegistry registry = configuration.getCollectorRegistry();
			this.serverStarted = serverStartedBuilder.register(registry);
			this.serverHandled = serverHandledBuilder.register(registry);
			this.serverStreamMessagesReceived = serverStreamMessagesReceivedBuilder.register(registry);
			this.serverStreamMessagesSent = serverStreamMessagesSentBuilder.register(registry);

			if (configuration.isIncludeLatencyHistograms()) {
				this.serverHandledLatencySeconds = Optional.of(serverHandledLatencySecondsBuilder
						.buckets(configuration.getLatencyBuckets()).register(registry));
			} else {
				this.serverHandledLatencySeconds = Optional.empty();
			}
		}

		/** Creates a {@link ServerMetrics} for the supplied method. */
		<R, S> ServerMetrics createMetricsForMethod(MethodDescriptor<R, S> methodDescriptor) {
			return new ServerMetrics(GrpcMethod.of(methodDescriptor), serverStarted, serverHandled,
					serverStreamMessagesReceived, serverStreamMessagesSent, serverHandledLatencySeconds);
		}

		ServerMetrics createMetricsForMethod(GrpcMethod grpcMethod) {
			return new ServerMetrics(grpcMethod, serverStarted, serverHandled, serverStreamMessagesReceived,
					serverStreamMessagesSent, serverHandledLatencySeconds);
		}
	}

	private <T> T addLabels(SimpleCollector<T> collector, String... labels) {
		List<String> allLabels = new ArrayList<>();
		Collections.addAll(allLabels, method.type(), method.serviceName(), method.methodName());
		Collections.addAll(allLabels, labels);
		return collector.labels(allLabels.toArray(new String[0]));
	}
}
