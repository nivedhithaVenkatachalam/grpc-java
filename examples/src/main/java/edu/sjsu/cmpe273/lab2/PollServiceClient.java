package edu.sjsu.cmpe273.lab2;

import io.grpc.ChannelImpl;
import io.grpc.transport.netty.NegotiationType;
import io.grpc.transport.netty.NettyChannelBuilder;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
/**
 * A simple client that requests a greeting from the {@link HelloWorldServer}.
 */
public class PollServiceClient {
  private static final Logger logger = Logger.getLogger(PollServiceClient.class.getName());

  private final ChannelImpl channel;
  private final PollServiceGrpc.PollServiceBlockingStub blockingStub;

  public PollServiceClient(String host, int port) {
    channel =
        NettyChannelBuilder.forAddress(host, port).negotiationType(NegotiationType.PLAINTEXT)
            .build();
    blockingStub = PollServiceGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTerminated(5, TimeUnit.SECONDS);
  }

  public void createPoll(String moderatorId, String question, String startedAt, String expiredAt, String[] choice) {
    if(choice==null|| choice.length<2){
    logger.info("Choice needs to be greater than 1.");
    }
    try {
      logger.info("Creating a poll for moderator " + moderatorId + " ...");
      //String a=choice[0];
      //String b= choice[1];
      //logger.info(a+b);
           PollRequest request = PollRequest.newBuilder().setModeratorId(moderatorId)
	.setQuestion(question).setStartedAt(startedAt).setExpiredAt(expiredAt).addChoice(choice[0])
        .addChoice(choice[1])
	.build();
      //logger.info("not me");
      PollResponse response = blockingStub.createPoll(request);
      logger.info("Poll ID created: " + response.getId());
    } catch (RuntimeException e) {
      logger.log(Level.WARNING, "RPC failed", e);
      return;
    }
  }

  public static void main(String[] args) throws Exception {
    PollServiceClient client = new PollServiceClient("localhost", 50051);
    try {
      /* Access a service running on the local machine on port 50051 */
      String moderatorId = "1";
      String question= "What type of smartphone do you have?";
      String startedAt= "2015-02-23T13:00:00.000Z";
      String expiredAt= "2015-02-24T13:00:00.000Z";
      String[] choice= {"Android", "iPhone"};		
      //if (args.length > 0) {
        //user = args[0]; /* Use the arg as the name to greet if provided */
      //}
      client.createPoll(moderatorId, question, startedAt, expiredAt, choice);
    } finally {
      client.shutdown();
    }
  }
}
