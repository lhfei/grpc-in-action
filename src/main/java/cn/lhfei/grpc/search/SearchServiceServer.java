package cn.lhfei.grpc.search;

import cn.lhfei.grpc.search.SearchProto.Result;
import cn.lhfei.grpc.search.SearchProto.SearchRequest;
import cn.lhfei.grpc.search.SearchProto.SearchResponse;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchServiceServer {
  private static final Logger LOG = LoggerFactory.getLogger(SearchServiceServer.class);

  /* The port on which the server should run */
  private int port = 50051;
  private Server server;

  private void start() throws IOException {
    server = ServerBuilder.forPort(port).addService(new SearchImpl()).build().start();
    LOG.info("Server started, listening on {}", port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its
        // JVM shutdown hook.
        LOG.info("*** shutting down gRPC server since JVM is shutting down");
        SearchServiceServer.this.stop();
        LOG.info("*** server shut down");
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
    final SearchServiceServer server = new SearchServiceServer();
    server.start();
    server.blockUntilShutdown();
  }

  private class SearchImpl extends SearchServiceGrpc.SearchServiceImplBase {

    @Override
    public void search(SearchRequest request, StreamObserver<SearchResponse> responseObserver) {
      Result r1 = Result.newBuilder().setTitle("KNN").setUrl("knn.io").build();
      Result r2 = Result.newBuilder().setTitle("LR").setUrl("lr.io").build();
      SearchResponse response =
          SearchResponse.newBuilder().addResults(r1).addResults(r2).build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }
  }
}
