package com.globant.co.cp.task;

import java.math.BigInteger;
import java.util.concurrent.Semaphore;

public class TaskSerialNumber implements Runnable {

   private BigInteger serialNumber;
   private Semaphore centinel;

   public TaskSerialNumber(BigInteger serialNumber, Semaphore centinel) {
      this.serialNumber = serialNumber;
      this.centinel = centinel;
   }

   @Override
   public void run() {
      try {
         centinel.acquire();
         serialNumber.add(BigInteger.ONE);
         System.out.println("Serial number increased");
         centinel.release();
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

}
