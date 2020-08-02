import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class HelloWorldServiceTest {

    public static ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();

    public static void main(String args[]) throws InterruptedException {
        testSayHello();
    }

    public static void testSayHello() throws InterruptedException {
        /**
        HelloWorldGrpc.HelloWorldBlockingStub stub = HelloWorldGrpc.newBlockingStub(channel);
        HelloWorldObjects.HelloRequestProto request = HelloWorldObjects.HelloRequestProto.newBuilder().setName("razel").build();
        HelloWorldObjects.HelloResponseProto result = stub.sayHello(request);
        System.out.println("got a response: " +  result.getMessage())
         **/
    }
}
