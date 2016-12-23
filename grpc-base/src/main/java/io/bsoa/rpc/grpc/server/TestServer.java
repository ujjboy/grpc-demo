package io.bsoa.rpc.grpc.server;

import io.bsoa.rpc.grpc.service.TestServiceImpl;
import io.grpc.internal.ServerImpl;
import io.grpc.netty.NettyServerBuilder;

/**
 * Created by zhanggeng on 2016/12/19.
 */
public class TestServer {

    public static void main(String[] args) throws Exception {

        ServerImpl server = NettyServerBuilder.forPort(50010)
                .addService(new TestServiceImpl())
                .build();
        server.start();
        server.awaitTermination();//阻塞直到退出
    }
}