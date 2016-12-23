package io.bsoa.rpc.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhanggeng on 2016/12/22.
 */
public class HelloServiceClient {

    public static final Logger LOGGER = LoggerFactory.getLogger(HelloServiceClient.class);

    private String host = "127.0.0.1";
    private int port = 8888;

    ManagedChannel channel;

    HelloServiceGrpc.HelloServiceStub asyncStub;
    HelloServiceGrpc.HelloServiceBlockingStub blockingStub;
    HelloServiceGrpc.HelloServiceFutureStub futureStub;

    public void connect() {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext(true) // 不用ssl
                .build();
        blockingStub = HelloServiceGrpc.newBlockingStub(channel);
        asyncStub = HelloServiceGrpc.newStub(channel);
        futureStub = HelloServiceGrpc.newFutureStub(channel);
    }

    public void close() throws Exception {
        if (channel != null) {
            channel.shutdown().awaitTermination(5000, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 测试普通调用
     */
    public void testSimpleRpc() {
        HelloRequest request = HelloRequest.newBuilder().setName("zhanggeng").setId(11111).build();
        HelloResponse response = blockingStub.simpleRpc(request);
        LOGGER.info("receive simpleRpc response:{}", response, response.getResultList().size());
    }

    /**
     * 测试同步的服务端多次返回
     */
    public void testServerSideStreamRpcWithSync() {
        HelloRequest request = HelloRequest.newBuilder().setName("zhanggeng").setId(11111).build();
        Iterator<HelloResponse> responses = blockingStub.serverSideStreamRpc(request);
        final AtomicInteger responseTimes = new AtomicInteger();
        while (responses.hasNext()) {
            LOGGER.info("receive serverSideStreamRpc response:{}", responses.next());
            responseTimes.incrementAndGet();
        }
        LOGGER.info("receive serverSideStreamRpc over!");
        LOGGER.info("serverSideStreamRpc: send :{}  receive: {}", 1, responseTimes.get());
    }

    /**
     * 测试异步的服务端多次返回
     */
    public void testServerSideStreamRpcWithAsync() throws InterruptedException {
        final AtomicInteger responseTimes = new AtomicInteger();
        HelloRequest request = HelloRequest.newBuilder().setName("zhanggeng").setId(11111).build();
        asyncStub.serverSideStreamRpc(request,
                new StreamObserver<HelloResponse>() {
                    @Override
                    public void onNext(HelloResponse response) {
                        LOGGER.info("receive serverSideStreamRpc response:{}", response);
                        responseTimes.incrementAndGet();
                    }

                    @Override
                    public void onError(Throwable t) {
                        LOGGER.error("", t);
                    }

                    @Override
                    public void onCompleted() {
                        LOGGER.info("receive serverSideStreamRpc over!");
                    }
                });
        channel.awaitTermination(10000, TimeUnit.MILLISECONDS);
        LOGGER.info("serverSideStreamRpc: send :{}  receive: {}", 1, responseTimes.get());
    }

    /**
     * 测试客户地多次发送
     */
    public void testCientSideStreamRpc() throws InterruptedException {
        final AtomicInteger responseTimes = new AtomicInteger();
        StreamObserver<HelloRequest> requestSender = asyncStub.clientSideStreamRpc(
                new StreamObserver<HelloResponse>() {
                    @Override
                    public void onNext(HelloResponse response) {
                        LOGGER.info("receive cientSideStreamRpc response:{}", response);
                        responseTimes.incrementAndGet();
                    }

                    @Override
                    public void onError(Throwable t) {
                        LOGGER.error("", t);
                    }

                    @Override
                    public void onCompleted() {
                        LOGGER.info("receive cientSideStreamRpc over!");
                    }
                }
        );
        for (int i = 0; i < 3; i++) {
            // 发送三次Request
            HelloRequest request = HelloRequest.newBuilder().setName("zhanggeng").setId(i).build();
            requestSender.onNext(request);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
        requestSender.onCompleted();
        channel.awaitTermination(10000, TimeUnit.MILLISECONDS);
        LOGGER.info("clientSideStreamRpc: send :{}  receive: {}", 3, responseTimes.get());
    }


    /**
     * 测试客户地多次发送
     */
    public void testBidirectionalStreamRpc() throws InterruptedException {
        final AtomicInteger responseTimes = new AtomicInteger();
        StreamObserver<HelloRequest> requestSender = asyncStub.bidirectionalStreamRpc(
                new StreamObserver<HelloResponse>() {
                    @Override
                    public void onNext(HelloResponse response) {
                        responseTimes.incrementAndGet();
                        LOGGER.info("receive bidirectionalStreamRpc response:{}", response);
                    }

                    @Override
                    public void onError(Throwable t) {
                        LOGGER.error("", t);
                    }

                    @Override
                    public void onCompleted() {
                        LOGGER.info("receive bidirectionalStreamRpc over!");
                    }
                }
        );
        for (int i = 0; i < 3; i++) {
            // 发送三次Request
            HelloRequest request = HelloRequest.newBuilder().setName("zhanggeng").setId(i + 1).build();
            requestSender.onNext(request);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
        requestSender.onCompleted();
        channel.awaitTermination(10000, TimeUnit.MILLISECONDS);
        LOGGER.info("bidirectionalStreamRpc: send :{}  receive: {}", 3, responseTimes.get());
    }


    public static void main(String[] args) throws Exception {
        HelloServiceClient client = new HelloServiceClient();

        client.connect();

        // 测试普通调用
        LOGGER.info("===========simpleRpc=============");
        client.testSimpleRpc();

        // 测试服务端多次返回
        LOGGER.info("===========serverSideStreamRpc=============");
        client.testServerSideStreamRpcWithSync();
        client.testServerSideStreamRpcWithAsync();

        // 测试客户断多次发送
        LOGGER.info("===========cientSideStreamRpc=============");
        client.testCientSideStreamRpc();

        LOGGER.info("===========bidirectionalStreamRpc=============");
        client.testBidirectionalStreamRpc();


        client.close();
    }
}
