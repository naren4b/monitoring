# monitoring

Start the GRPC server 
Start the Application Server 

access the http://localhost:8080/greeter/metrics to get the metrics 


Note 
GRPC MethodType

MethodType 
/**
* One request message followed by one response message.
*/
UNARY,

/**
* Zero or more request messages followed by one response message.
*/
CLIENT_STREAMING,

/**
* One request message followed by zero or more response messages.
*/
SERVER_STREAMING,

/**
* Zero or more request and response messages arbitrarily interleaved in time.
*/
BIDI_STREAMING,

/**
* Cardinality and temporal relationships are not known. Implementations should not make
* buffering assumptions and should largely treat the same as {@link #BIDI_STREAMING}.
*/
UNKNOWN;

1.serverStarted //Server contacted 
2.serverStreamMessagesReceived //Request received Comes 
3.serverStreamMessagesSent // Response Goes out
4.serverHandled // Request Completed 
5.recordLatency // Total Duration it took 


// Target bussiness method Service Called 
com.naren.sample.grpc.server.internal.GreeterService.sayHello(HelloRequest, StreamObserver<HelloReply>)

// io.grpc.stub.StreamObserver.onNext()
com.naren.monitoring.prometheus.MonitoringServerCall.sendMessage(S)

//io.grpc.stub.StreamObserver.onCompleted()
com.naren.monitoring.prometheus.MonitoringServerCall.reportEndMetrics(Status)
	com.naren.monitoring.prometheus.ServerMetrics.recordServerHandled(Code)
	com.naren.monitoring.prometheus.ServerMetrics.recordLatency(double)

