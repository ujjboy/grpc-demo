package io.bsoa.rpc.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhanggeng on 2016/12/19.
 */
public class TestClient {

    private final TestRpcServiceGrpc.TestRpcServiceBlockingStub client;

    public TestClient(String host,int port) {
        ManagedChannel channel =  NettyChannelBuilder.forAddress(host, port).usePlaintext(true).build();
        client = TestRpcServiceGrpc.newBlockingStub(channel).withDeadlineAfter(60000, TimeUnit.MILLISECONDS);
    }

    public String sayHello(String name,Integer id) {
        TestModel.TestRequest request = TestModel.TestRequest.newBuilder().setId(id).setName(name).build();
        TestModel.TestResponse response = client.sayHello(request);
        return response.getMessage();
    }

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 50010;
        String req = "1";
        for (int i = 0; i < 16 ; i++) {
            req = req + req; // 32k
        }
        TestClient client = new TestClient(host, port);
        String res = client.sayHello(req, 11);
        System.out.println(res);
    }
}
