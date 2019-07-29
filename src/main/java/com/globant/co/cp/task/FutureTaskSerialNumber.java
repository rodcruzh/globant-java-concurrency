package com.globant.co.cp.task;

import java.math.BigInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

public class FutureTaskSerialNumber implements Callable<Long> {

   private AtomicLong serialNumber;

   public FutureTaskSerialNumber(AtomicLong serialNumber) {
      this.serialNumber = serialNumber;
   }

   @Override
   public Long call() {
      Long currentSerial = serialNumber.addAndGet(1);
      System.out.println("Thread " + Thread.currentThread().getName() + " increase serial to " + currentSerial);
      return currentSerial;
   }

}
