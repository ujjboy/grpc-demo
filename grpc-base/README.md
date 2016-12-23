grpc官网
====
英文文档：[http://www.grpc.io/docs/](http://www.grpc.io/docs/)  
中文文档：[http://doc.oschina.net/grpc?t=56831](http://doc.oschina.net/grpc?t=56831)  

protobuf地址：[https://github.com/google/protobuf](https://github.com/google/protobuf)    
protoc命令行下载地址：（需要3.x版本，自带gRPC插件）  
[http://repo1.maven.org/maven2/com/google/protobuf/protoc/3.1.0/](http://repo1.maven.org/maven2/com/google/protobuf/protoc/3.1.0/)  
[https://repo1.maven.org/maven2/io/grpc/protoc-gen-grpc-java/1.0.2/](https://repo1.maven.org/maven2/io/grpc/protoc-gen-grpc-java/1.0.2/)  

命令行方式生成代码
====
1. 将protoc加入path
2. sh生成代码脚本参考

```sh
export SRC_DIR="/Users/zhanggeng/workspace/zhanggeng.zg/gRPC-demo/grpc-base/src/main/proto"
export DST_DIR="/Users/zhanggeng/workspace/zhanggeng.zg/gRPC-demo/grpc-base/src/main/java"
export PROTOC_GEN_GRPC="/Users/zhanggeng/dev/protoc/protoc-gen-grpc-java-1.0.2-osx-x86_64.exe"
  
# 普通model文件无需grpc插件
protoc \
-I=$SRC_DIR \
--java_out=$DST_DIR \
$SRC_DIR/TestModel.proto
  
# service需要grpc插件
protoc \
--plugin=protoc-gen-grpc-java=$PROTOC_GEN_GRPC \
--proto_path=$SRC_DIR \
--grpc-java_out=$DST_DIR \
$SRC_DIR/TestService.proto
```

然后就可以编写代码了

maven插件方式：
==== 

1. 在pom.xml加入如下代码：

```sh
    <properties>
        <protobufversion>3.1.0</protobuf.version>
        <grpcversion>1.0.2</grpc.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-all</artifactId>
            <version>${grpc.version}</version>
        </dependency>
    </dependencies>

    <pluginRepositories><!-- 插件库 -->
        <pluginRepository>
            <id>protoc-plugin</id>
            <url>https://dl.bintray.com/sergei-ivanov/maven/</url>
        </pluginRepository>
    </pluginRepositories>
    <build>
        <extensions>
            <extension>
                <groupId>kr.motd.maven</groupId>
                <artifactId>os-maven-plugin</artifactId>
                <version>1.4.0.Final</version>
            </extension>
        </extensions>
        <plugins>
            <plugin>
                <groupId>com.google.protobuf.tools</groupId>
                <artifactId>maven-protoc-plugin</artifactId>
                <version>0.4.4</version>
                <configuration>
                    <protocArtifact>com.google.protobuf:protoc:${protobuf.version}:exe:${os.detected.classifier}</protocArtifact>
                    <pluginId>grpc-java</pluginId>
                    <pluginArtifact>io.grpc:protoc-gen-grpc-java:${grpc.version}:exe:${os.detected.classifier}
                    </pluginArtifact>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>compile-custom</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</xml>
```  
2. 然后将proto代码放在：`$project$/src/main/proto`
3. 执行`mvn compile`
4. 生成的代码在 `$project$/target/generated-sources`


#启动服务端和客户端
##服务端
1.编写实现类（继承生成的接口即可）

```java
public class TestServiceImpl extends TestRpcServiceGrpc.TestRpcServiceImplBase {

    @Override
    public void sayHello(TestModel.TestRequest request, StreamObserver<TestModel.TestResponse> responseObserver) {
        String result = request.getName() + request.getId();
        TestModel.TestResponse response = TestModel.TestResponse.newBuilder().setMessage(result).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
```

2. 编写服务端类：

```java
  public static void main(String[] args) throws Exception {

        ServerImpl server = NettyServerBuilder.forPort(50010)
                .addService(new TestServiceImpl())
                .build();
        server.start();
        server.awaitTermination();//阻塞直到退出
    }
```

3. 编写客户端类
```java

    private final TestRpcServiceGrpc.TestRpcServiceBlockingStub client;

    public TestClient(String host,int port) {
        ManagedChannel channel =  NettyChannelBuilder.forAddress(host, port).usePlaintext(true).build();
        client = TestRpcServiceGrpc.newBlockingStub(channel).withDeadlineAfter(60000, TimeUnit.MILLISECONDS);
    }
```

可以拿client做任何事情了。