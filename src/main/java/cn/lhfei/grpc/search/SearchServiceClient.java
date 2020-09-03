package cn.lhfei.grpc.search;

import cn.lhfei.grpc.search.SearchProto.RequestParam;
import cn.lhfei.grpc.search.SearchProto.RequestParam.Corpus;
import cn.lhfei.grpc.search.SearchProto.SearchRequest;
import cn.lhfei.grpc.search.SearchProto.SearchResponse;
import com.google.gson.Gson;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchServiceClient {
  private static final Logger LOG = LoggerFactory.getLogger(SearchServiceClient.class);
  private Gson gson = new Gson();
  private final ManagedChannel channel;
  private final SearchServiceGrpc.SearchServiceBlockingStub blockingStub;

  /**
   * Construct client connecting to Search server at {@code host:port}.
   */
  public SearchServiceClient(String host, int port) {
    channel = ManagedChannelBuilder.forAddress(host, port)
        // Channels are secure by default (via SSL/TLS). For the example
        // we disable TLS to avoid
        // needing certificates.
        .usePlaintext().build();
    blockingStub = SearchServiceGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  public void search(RequestParam param) {
    SearchResponse response;
    SearchRequest request;
    request = SearchRequest.newBuilder().setParam(param).build();

    try {

      response = blockingStub.search(request);
      LOG.info("ResponseBody: {}", gson.toJson(response));
    } catch (StatusRuntimeException e) {
      LOG.warn("RPC failed: {0}", e.getMessage(), e);
      return;
    }
  }

  /**
   * Search server.
   */
  public static void main(String[] args) throws Exception {
    String hostName = "localhost";
    int port = 50051;
    RequestParam param;

    SearchServiceClient client = new SearchServiceClient(hostName, port);

    try {
      /* Access a service running on the local machine on port 50051 */
      param =
          RequestParam.newBuilder()
              .setCorpus(Corpus.NEWS)
              .setQuery("Tensorflow")
              .setPageNumber(0)
              .setResultPerPage(50)
              .build();
      client.search(param);
    } finally {
      client.shutdown();
    }
  }
}
