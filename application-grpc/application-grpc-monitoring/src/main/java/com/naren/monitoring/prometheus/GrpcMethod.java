package com.naren.monitoring.prometheus;

import io.grpc.MethodDescriptor;
import io.grpc.MethodDescriptor.MethodType;

/** Knows how to extract information about a single grpc method. */
class GrpcMethod {
	private final String serviceName;
	private final String methodName;
	private final MethodType type;

	private GrpcMethod(String serviceName, String methodName, MethodType type) {
		this.serviceName = serviceName;
		this.methodName = methodName;
		this.type = type;
	}

	static GrpcMethod of(MethodDescriptor<?, ?> method) {
		String fullMethodName = method.getFullMethodName();
		String serviceName = MethodDescriptor.extractFullServiceName(fullMethodName);
		String methodName = fullMethodName.substring(serviceName.length() + 1);
		return of(serviceName, methodName, method.getType());
	}

	static GrpcMethod of(String serviceName, String methodName, MethodType type) {
		return new GrpcMethod(serviceName, methodName, type);
	}

	String serviceName() {
		return serviceName;
	}

	String methodName() {
		return methodName;
	}

	String type() {
		return type.toString();
	}

	boolean streamsRequests() {
		return type == MethodType.CLIENT_STREAMING || type == MethodType.BIDI_STREAMING || type == MethodType.UNARY;
	}

	boolean streamsResponses() {
		return type == MethodType.SERVER_STREAMING || type == MethodType.BIDI_STREAMING || type == MethodType.UNARY;
	}
}
