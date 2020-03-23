/*
 * Copyright 2010-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.lhfei.grpc.helloworld;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

/**
 * @version 0.1
 *
 * @author Hefei Li
 *
 * @since Jan 23, 2017
 */
public class HelloWorldClient {
	private static final Logger logger = LoggerFactory.getLogger(HelloWorldClient.class);
	
	private final ManagedChannel channel;
	private final GreeterGrpc.GreeterBlockingStub blockingStub;

	/**
	 * Construct client connecting to HelloWorld server at {@code host:port}.
	 */
	public HelloWorldClient(String host, int port) {
		channel = ManagedChannelBuilder.forAddress(host, port)
				// Channels are secure by default (via SSL/TLS). For the example
				// we disable TLS to avoid
				// needing certificates.
				.usePlaintext().build();
		blockingStub = GreeterGrpc.newBlockingStub(channel);
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

	/** Say hello to server. */
	public void greet(String name) {
		logger.info("Will try to greet {} {}", name, "...");
		HelloRequest request = HelloRequest.newBuilder().setName(name).build();
		HelloReply response;
		try {
			response = blockingStub.sayHello(request);
		} catch (StatusRuntimeException e) {
			logger.warn("RPC failed: {0}", e.getStatus());
			return;
		}
		logger.info("Greeting: {}", response.getMessage());
	}

	/**
	 * Greet server. If provided, the first element of {@code args} is the name
	 * to use in the greeting.
	 */
	public static void main(String[] args) throws Exception {
		String hostName = "localhost";
		int port = 50051;
		
		HelloWorldClient client = new HelloWorldClient(hostName, port);
		
		try {
			/* Access a service running on the local machine on port 50051 */
			String user = "Hefei Li";
			if (args.length > 0) {
				user = args[0];
			}
			client.greet(user);
		} finally {
			client.shutdown();
		}
	}
}
