package edu.sjsu.cs259.raft.service.gen;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.23.0)",
    comments = "Source: raft.proto")
public final class RaftServerGrpc {

  private RaftServerGrpc() {}

  public static final String SERVICE_NAME = "raft.RaftServer";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<RequestVoteRequest,
          RequestVoteResponse> getRequestVoteMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestVote",
      requestType = RequestVoteRequest.class,
      responseType = RequestVoteResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<RequestVoteRequest,
          RequestVoteResponse> getRequestVoteMethod() {
    io.grpc.MethodDescriptor<RequestVoteRequest, RequestVoteResponse> getRequestVoteMethod;
    if ((getRequestVoteMethod = RaftServerGrpc.getRequestVoteMethod) == null) {
      synchronized (RaftServerGrpc.class) {
        if ((getRequestVoteMethod = RaftServerGrpc.getRequestVoteMethod) == null) {
          RaftServerGrpc.getRequestVoteMethod = getRequestVoteMethod =
              io.grpc.MethodDescriptor.<RequestVoteRequest, RequestVoteResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RequestVote"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  RequestVoteRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  RequestVoteResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RaftServerMethodDescriptorSupplier("RequestVote"))
              .build();
        }
      }
    }
    return getRequestVoteMethod;
  }

  private static volatile io.grpc.MethodDescriptor<AppendEntriesRequest,
          AppendEntriesResponse> getAppendEntriesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AppendEntries",
      requestType = AppendEntriesRequest.class,
      responseType = AppendEntriesResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<AppendEntriesRequest,
          AppendEntriesResponse> getAppendEntriesMethod() {
    io.grpc.MethodDescriptor<AppendEntriesRequest, AppendEntriesResponse> getAppendEntriesMethod;
    if ((getAppendEntriesMethod = RaftServerGrpc.getAppendEntriesMethod) == null) {
      synchronized (RaftServerGrpc.class) {
        if ((getAppendEntriesMethod = RaftServerGrpc.getAppendEntriesMethod) == null) {
          RaftServerGrpc.getAppendEntriesMethod = getAppendEntriesMethod =
              io.grpc.MethodDescriptor.<AppendEntriesRequest, AppendEntriesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AppendEntries"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  AppendEntriesRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  AppendEntriesResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RaftServerMethodDescriptorSupplier("AppendEntries"))
              .build();
        }
      }
    }
    return getAppendEntriesMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ClientAppendRequest,
          ClientAppendResponse> getClientAppendMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ClientAppend",
      requestType = ClientAppendRequest.class,
      responseType = ClientAppendResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ClientAppendRequest,
          ClientAppendResponse> getClientAppendMethod() {
    io.grpc.MethodDescriptor<ClientAppendRequest, ClientAppendResponse> getClientAppendMethod;
    if ((getClientAppendMethod = RaftServerGrpc.getClientAppendMethod) == null) {
      synchronized (RaftServerGrpc.class) {
        if ((getClientAppendMethod = RaftServerGrpc.getClientAppendMethod) == null) {
          RaftServerGrpc.getClientAppendMethod = getClientAppendMethod =
              io.grpc.MethodDescriptor.<ClientAppendRequest, ClientAppendResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ClientAppend"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ClientAppendRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ClientAppendResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RaftServerMethodDescriptorSupplier("ClientAppend"))
              .build();
        }
      }
    }
    return getClientAppendMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ClientRequestIndexRequest,
          ClientRequestIndexResponse> getClientRequestIndexMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ClientRequestIndex",
      requestType = ClientRequestIndexRequest.class,
      responseType = ClientRequestIndexResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ClientRequestIndexRequest,
          ClientRequestIndexResponse> getClientRequestIndexMethod() {
    io.grpc.MethodDescriptor<ClientRequestIndexRequest, ClientRequestIndexResponse> getClientRequestIndexMethod;
    if ((getClientRequestIndexMethod = RaftServerGrpc.getClientRequestIndexMethod) == null) {
      synchronized (RaftServerGrpc.class) {
        if ((getClientRequestIndexMethod = RaftServerGrpc.getClientRequestIndexMethod) == null) {
          RaftServerGrpc.getClientRequestIndexMethod = getClientRequestIndexMethod =
              io.grpc.MethodDescriptor.<ClientRequestIndexRequest, ClientRequestIndexResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ClientRequestIndex"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ClientRequestIndexRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ClientRequestIndexResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RaftServerMethodDescriptorSupplier("ClientRequestIndex"))
              .build();
        }
      }
    }
    return getClientRequestIndexMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RaftServerStub newStub(io.grpc.Channel channel) {
    return new RaftServerStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RaftServerBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new RaftServerBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RaftServerFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new RaftServerFutureStub(channel);
  }

  /**
   */
  public static abstract class RaftServerImplBase implements io.grpc.BindableService {

    /**
     */
    public void requestVote(RequestVoteRequest request,
                            io.grpc.stub.StreamObserver<RequestVoteResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestVoteMethod(), responseObserver);
    }

    /**
     */
    public void appendEntries(AppendEntriesRequest request,
                              io.grpc.stub.StreamObserver<AppendEntriesResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getAppendEntriesMethod(), responseObserver);
    }

    /**
     * <pre>
     * these next two RPCs are sent from client to the leader
     * </pre>
     */
    public void clientAppend(ClientAppendRequest request,
                             io.grpc.stub.StreamObserver<ClientAppendResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getClientAppendMethod(), responseObserver);
    }

    /**
     */
    public void clientRequestIndex(ClientRequestIndexRequest request,
                                   io.grpc.stub.StreamObserver<ClientRequestIndexResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getClientRequestIndexMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRequestVoteMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                      RequestVoteRequest,
                      RequestVoteResponse>(
                  this, METHODID_REQUEST_VOTE)))
          .addMethod(
            getAppendEntriesMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                      AppendEntriesRequest,
                      AppendEntriesResponse>(
                  this, METHODID_APPEND_ENTRIES)))
          .addMethod(
            getClientAppendMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                      ClientAppendRequest,
                      ClientAppendResponse>(
                  this, METHODID_CLIENT_APPEND)))
          .addMethod(
            getClientRequestIndexMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                      ClientRequestIndexRequest,
                      ClientRequestIndexResponse>(
                  this, METHODID_CLIENT_REQUEST_INDEX)))
          .build();
    }
  }

  /**
   */
  public static final class RaftServerStub extends io.grpc.stub.AbstractStub<RaftServerStub> {
    private RaftServerStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RaftServerStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RaftServerStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RaftServerStub(channel, callOptions);
    }

    /**
     */
    public void requestVote(RequestVoteRequest request,
                            io.grpc.stub.StreamObserver<RequestVoteResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestVoteMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void appendEntries(AppendEntriesRequest request,
                              io.grpc.stub.StreamObserver<AppendEntriesResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getAppendEntriesMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * these next two RPCs are sent from client to the leader
     * </pre>
     */
    public void clientAppend(ClientAppendRequest request,
                             io.grpc.stub.StreamObserver<ClientAppendResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getClientAppendMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void clientRequestIndex(ClientRequestIndexRequest request,
                                   io.grpc.stub.StreamObserver<ClientRequestIndexResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getClientRequestIndexMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class RaftServerBlockingStub extends io.grpc.stub.AbstractStub<RaftServerBlockingStub> {
    private RaftServerBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RaftServerBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RaftServerBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RaftServerBlockingStub(channel, callOptions);
    }

    /**
     */
    public RequestVoteResponse requestVote(RequestVoteRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestVoteMethod(), getCallOptions(), request);
    }

    /**
     */
    public AppendEntriesResponse appendEntries(AppendEntriesRequest request) {
      return blockingUnaryCall(
          getChannel(), getAppendEntriesMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * these next two RPCs are sent from client to the leader
     * </pre>
     */
    public ClientAppendResponse clientAppend(ClientAppendRequest request) {
      return blockingUnaryCall(
          getChannel(), getClientAppendMethod(), getCallOptions(), request);
    }

    /**
     */
    public ClientRequestIndexResponse clientRequestIndex(ClientRequestIndexRequest request) {
      return blockingUnaryCall(
          getChannel(), getClientRequestIndexMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class RaftServerFutureStub extends io.grpc.stub.AbstractStub<RaftServerFutureStub> {
    private RaftServerFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RaftServerFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RaftServerFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RaftServerFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<RequestVoteResponse> requestVote(
        RequestVoteRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestVoteMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<AppendEntriesResponse> appendEntries(
        AppendEntriesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getAppendEntriesMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * these next two RPCs are sent from client to the leader
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ClientAppendResponse> clientAppend(
        ClientAppendRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getClientAppendMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ClientRequestIndexResponse> clientRequestIndex(
        ClientRequestIndexRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getClientRequestIndexMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REQUEST_VOTE = 0;
  private static final int METHODID_APPEND_ENTRIES = 1;
  private static final int METHODID_CLIENT_APPEND = 2;
  private static final int METHODID_CLIENT_REQUEST_INDEX = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final RaftServerImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(RaftServerImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REQUEST_VOTE:
          serviceImpl.requestVote((RequestVoteRequest) request,
              (io.grpc.stub.StreamObserver<RequestVoteResponse>) responseObserver);
          break;
        case METHODID_APPEND_ENTRIES:
          serviceImpl.appendEntries((AppendEntriesRequest) request,
              (io.grpc.stub.StreamObserver<AppendEntriesResponse>) responseObserver);
          break;
        case METHODID_CLIENT_APPEND:
          serviceImpl.clientAppend((ClientAppendRequest) request,
              (io.grpc.stub.StreamObserver<ClientAppendResponse>) responseObserver);
          break;
        case METHODID_CLIENT_REQUEST_INDEX:
          serviceImpl.clientRequestIndex((ClientRequestIndexRequest) request,
              (io.grpc.stub.StreamObserver<ClientRequestIndexResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class RaftServerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RaftServerBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return Raft.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RaftServer");
    }
  }

  private static final class RaftServerFileDescriptorSupplier
      extends RaftServerBaseDescriptorSupplier {
    RaftServerFileDescriptorSupplier() {}
  }

  private static final class RaftServerMethodDescriptorSupplier
      extends RaftServerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    RaftServerMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (RaftServerGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RaftServerFileDescriptorSupplier())
              .addMethod(getRequestVoteMethod())
              .addMethod(getAppendEntriesMethod())
              .addMethod(getClientAppendMethod())
              .addMethod(getClientRequestIndexMethod())
              .build();
        }
      }
    }
    return result;
  }
}
