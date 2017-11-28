package com.naren.sample.grpc.server.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.naren.monitoring.prometheus.GrpcMetricService;
import com.naren.sample.grpc.api.GreeterGrpc;
import com.naren.sample.grpc.api.HelloReply;
import com.naren.sample.grpc.api.HelloRequest;
import com.naren.sample.grpc.api.MetricReply;
import com.naren.sample.grpc.api.MetricRequest;

import io.grpc.BindableService;
import io.grpc.stub.StreamObserver;

public class GreeterService extends GreeterGrpc.AbstractGreeter implements BindableService {

	private static final Logger LOG = LoggerFactory.getLogger(GreeterService.class);

	public static final String SERVICE_NAME = "helloworld.Greeter";
	public static final String UNARY_METHOD_NAME = "SayHello";

	@Override
	public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
		LOG.info("SayHello endpoint received request from " + request.getName());
		HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
		responseObserver.onNext(reply);
		responseObserver.onCompleted();
	}

	@Override
	public void metric(MetricRequest request, StreamObserver<MetricReply> responseObserver) {
		LOG.info("Metric endpoint received request from " + request.getName());
		MetricReply reply = MetricReply.newBuilder().setMetric(GrpcMetricService.metrics()).build();
		responseObserver.onNext(reply);
		responseObserver.onCompleted();

	}

}
