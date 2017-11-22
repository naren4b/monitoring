package com.naren.sample.grpc.server;

/**
 *
 * @author dcnorris
 */
public interface GrpcServer {

	void deactivate();

	void activate() throws Exception;

	void blockUntilShutdown() throws InterruptedException;

}
