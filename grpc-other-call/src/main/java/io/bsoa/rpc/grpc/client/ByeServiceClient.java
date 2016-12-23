package io.bsoa.rpc.grpc.client;

import io.bsoa.rpc.grpc.ByeServiceGrpc.ByeServiceBlockingStub;
import io.bsoa.rpc.grpc.ByeServiceOuterClass.ByeRequest;
import io.bsoa.rpc.grpc.ByeServiceOuterClass.ByeResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhanggeng on 2016/12/22.
 */
public class ByeServiceClient {

    public static final Logger LOGGER = LoggerFactory.getLogger(ByeServiceClient.class);

    private static String host = "127.0.0.1";
    private static int port = 8888;

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext(true) // 不用ssl
                .build();
        LOGGER.info("connected to: {}:{}", host, port);
        ByeServiceBlockingStub blockingStub  = ByeServiceGrpc.newBlockingStub(channel);
        ByeRequest request = ByeRequest.newBuilder().setName("zhanggeng").setId(11111).build();
        ByeResponse response = blockingStub.bye(request);
        LOGGER.info("receice simpleRpc response:{}", response);
    }
}
