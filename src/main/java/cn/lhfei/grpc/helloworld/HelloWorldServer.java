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

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;



/**
 * @version 0.1
 *
 * @author Hefei Li
 *
 * @since Jan 23, 2017
 */
public class HelloWorldServer {
	private static final Logger logger = LoggerFactory.getLogger(HelloWorldServer.class);

	/* The port on which the server should run */
	private int port = 50051;
	private Server server;

	private void start() throws IOException {
		server = ServerBuilder.forPort(port).addService(new GreeterImpl()).build().start();
		logger.info("Server started, listening on {}", port);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// Use stderr here since the logger may have been reset by its
				// JVM shutdown hook.
				logger.info("*** shutting down gRPC server since JVM is shutting down");
				HelloWorldServer.this.stop();
				logger.info("*** server shut down");
			}
		});
	}

	private void stop() {
		if (server != null) {
			server.shutdown();
		}
	}

	/**
	 * Await termination on the main thread since the grpc library uses daemon
	 * threads.
	 */
	private void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			server.awaitTermination();
		}
	}

	/**
	 * Main launches the server from the command line.
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		final HelloWorldServer server = new HelloWorldServer();
		server.start();
		server.blockUntilShutdown();
	}

	private class GreeterImpl extends GreeterGrpc.GreeterImplBase {

		@Override
		public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
			HelloReply reply = HelloReply.newBuilder().setMessage(" == Hello " + req.getName()).build();
			responseObserver.onNext(reply);
			responseObserver.onCompleted();
		}
	}
}
