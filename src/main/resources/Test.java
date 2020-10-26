 private void doCreate(KeyValueServiceFutureStub stub, AtomicReference<Throwable> error)
      throws InterruptedException {
	 CreateRequest.newBuilder()
     .setKey(key)
     .setValue(randomBytes(MEAN_VALUE_SIZE))
     .build()
    ListenableFuture<CreateResponse> res = stub.create(
        );
    res.addListener(() ->  {
      rpcCount.incrementAndGet();
      limiter.release();
    }, MoreExecutors.directExecutor());
    Futures.addCallback(res, new FutureCallback<CreateResponse>() {
      @Override
      public void onSuccess(CreateResponse result) {
        if (!result.equals(CreateResponse.getDefaultInstance())) {
          error.compareAndSet(null, new RuntimeException("Invalid response"));
        }
        synchronized (knownKeys) {
          knownKeys.add(key);
        }
      }

      @Override
      public void onFailure(Throwable t) {
        Status status = Status.fromThrowable(t);
        if (status.getCode() == Code.ALREADY_EXISTS) {
          synchronized (knownKeys) {
            knownKeys.remove(key);
          }
          logger.log(Level.INFO, "Key already existed", t);
        } else {
          error.compareAndSet(null, t);
        }
      }
    });
  }