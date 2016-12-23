package io.bsoa.rpc.grpc.service;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhanggeng on 2016/12/22.
 */
public class ByeServiceImpl extends ByeServiceGrpc.ByeServiceImplBase {

    public static final Logger LOGGER = LoggerFactory.getLogger(HelloServiceImpl.class);

    public void bye(ByeServiceOuterClass.ByeRequest request,
                    StreamObserver<ByeServiceOuterClass.ByeResponse> responseObserver) {
        LOGGER.info("bye request :{} , {}", request.getName(), request.getId());

        ByeServiceOuterClass.ByeResponse response = ByeServiceOuterClass.ByeResponse.newBuilder().setMessage("bye " + request.getName()).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
