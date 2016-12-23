package io.bsoa.rpc.grpc.service;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhanggeng on 2016/12/19.
 */
public class TestServiceImpl extends TestRpcServiceGrpc.TestRpcServiceImplBase {

    public static final Logger LOGGER = LoggerFactory.getLogger(TestServiceImpl.class);

    @Override
    public void sayHello(TestModel.TestRequest request, StreamObserver<TestModel.TestResponse> responseObserver) {
        LOGGER.info("*************server receive*****************");
        LOGGER.info("[{}]{}", request.getName().length(), request.getName());
        String result = request.getName() + request.getId() + "from server";
        TestModel.TestResponse response = TestModel.TestResponse.newBuilder().setMessage(result).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }
}