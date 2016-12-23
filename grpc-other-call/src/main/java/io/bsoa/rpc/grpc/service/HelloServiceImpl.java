package io.bsoa.rpc.grpc.service;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanggeng on 2016/12/22.
 */
public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

    public static final Logger LOGGER = LoggerFactory.getLogger(HelloServiceImpl.class);

    /**
     * <pre>
     *  request-response
     * </pre>
     */
    public void simpleRpc(HelloRequest request,
                          StreamObserver<HelloResponse> responseObserver) {
        LOGGER.info("simpleRpc request :{} , {}", request.getName(), request.getId());

        // 拼一个返回值，可忽略
        List<HelloResponse.Result> listField = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            HelloResponse.Result result = HelloResponse.Result.newBuilder()
                    .addMessage("Hello").addMessage("From")
                    .addMessage("Server").addMessage(i + "").build();
            listField.add(result);
        }
        HelloResponse response = HelloResponse.newBuilder().addAllResult(listField).build();

        // 注意 普通的一次返回
        responseObserver.onNext(response);
        // 返回后告知结束
        responseObserver.onCompleted();
    }

    /**
     * <pre>
     * request-stream response，一般用于下载
     * </pre>
     */
    public void serverSideStreamRpc(HelloRequest request,
                                    StreamObserver<HelloResponse> responseObserver) {
        LOGGER.info("serverSideStreamRpc request :{} , {}", request.getName(), request.getId());

        // 拼一个返回值，可忽略
        List<HelloResponse.Result> listField = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            HelloResponse.Result result = HelloResponse.Result.newBuilder()
                    .addMessage("Hello").addMessage("From")
                    .addMessage("Server").addMessage(i + "").build();
            listField.add(result);
        }
        HelloResponse response1 = HelloResponse.newBuilder().addAllResult(listField).build();
        // 注意 一次返回
        responseObserver.onNext(response1);

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }

        HelloResponse response2 = HelloResponse.newBuilder()
                .addResult(HelloResponse.Result.newBuilder().addMessage("second message").build())
                .build();
        // 注意 二次返回
        responseObserver.onNext(response2);

        // 返回后告知结束
        responseObserver.onCompleted();
    }

    /**
     * <pre>
     * stream request-response，一般用于上传
     * </pre>
     */
    public StreamObserver<HelloRequest> clientSideStreamRpc(
            StreamObserver<HelloResponse> responseObserver) {
        // 流式解析请求
        return new StreamObserver<HelloRequest>() {
            private List<HelloRequest> requests = new ArrayList<>();

            @Override
            public void onNext(HelloRequest request) {
                LOGGER.info("clientSideStreamRpc request :{} , {}", request.getName(), request.getId());
                requests.add(request);
            }

            @Override
            public void onError(Throwable throwable) {
                LOGGER.error("", throwable);
            }

            @Override
            public void onCompleted() {
                LOGGER.info("Server receive {} clientSideStreamRpc request completed !", requests.size());

                // 拼一个返回值，可忽略
                List<HelloResponse.Result> listField = new ArrayList<>();
                for (HelloRequest request : requests) {
                    HelloResponse.Result result = HelloResponse.Result.newBuilder().addMessage("one message:" + request.getId()).build();
                    listField.add(result);
                }
                HelloResponse response = HelloResponse.newBuilder().addAllResult(listField).build();
                // 注意 一次性返回
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }

    /**
     * <pre>
     * 双向流连接，
     * </pre>
     */
    public StreamObserver<HelloRequest> bidirectionalStreamRpc(
            StreamObserver<HelloResponse> responseObserver) {
        // 流式解析请求
        return new StreamObserver<HelloRequest>() {
            private List<HelloRequest> requests = new ArrayList<>();

            @Override
            public void onNext(HelloRequest request) {
                LOGGER.info("bidirectionalStreamRpc request :{} , {}", request.getName(), request.getId());
                HelloResponse response = HelloResponse.newBuilder()
                        .addResult(HelloResponse.Result.newBuilder().addMessage("one message:" + request.getId()).build())
                        .build();
                for (int i = 0; i < request.getId() % 4; i++) {
                    // 注意 每一次都返回,  可能返回多次
                    responseObserver.onNext(response);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                LOGGER.error("", throwable);
            }

            @Override
            public void onCompleted() {
                LOGGER.info("Server receive {} bidirectionalStreamRpc request completed !", requests.size());
                responseObserver.onCompleted();
            }
        };
    }
}
