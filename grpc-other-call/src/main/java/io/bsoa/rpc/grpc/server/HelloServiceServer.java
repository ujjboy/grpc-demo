package io.bsoa.rpc.grpc.server;

import io.bsoa.rpc.grpc.service.ByeServiceImpl;
import io.bsoa.rpc.grpc.service.HelloServiceImpl;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class HelloServiceServer {

    public static final Logger LOGGER = LoggerFactory.getLogger(HelloServiceServer.class);

    private int port = 8888;

    private Server server;

    void start() throws Exception {
        if (server == null) {
            server = NettyServerBuilder.forPort(port)
//                .bossEventLoopGroup()
//                .workerEventLoopGroup()
                    .addService(new HelloServiceImpl())
                    .addService(new ByeServiceImpl())
                    .build();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> server.shutdown()));
        }
        server.start();
        LOGGER.info("启动grpc服务端，端口：{}", port);
        server.awaitTermination();//阻塞直到退出
    }

    public static void main(String[] args) throws Exception {
        HelloServiceServer serviceServer = new HelloServiceServer();
        serviceServer.start();
    }
}
