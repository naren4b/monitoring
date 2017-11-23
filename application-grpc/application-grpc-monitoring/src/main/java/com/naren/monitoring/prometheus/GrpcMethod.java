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
		String serviceName = MethodDescriptor.extractFullServiceName(method.getFullMethodName());

		// Full method names are of the form: "full.serviceName/MethodName". We extract
		// the last part.
		String methodName = method.getFullMethodName().substring(serviceName.length() + 1);
		return new GrpcMethod(serviceName, methodName, method.getType());
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
    return type == MethodType.CLIENT_STREAMING || type == MethodType.BIDI_STREAMING || type == MethodType.UNARY ;
  }

  boolean streamsResponses() {
    return type == MethodType.SERVER_STREAMING || type == MethodType.BIDI_STREAMING || type == MethodType.UNARY ;
  }
}
