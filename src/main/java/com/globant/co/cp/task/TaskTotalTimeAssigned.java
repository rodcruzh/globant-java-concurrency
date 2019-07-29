package com.globant.co.cp.task;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TaskTotalTimeAssigned implements Runnable {

   List<BigInteger> clientList;
   ConcurrentHashMap<BigInteger, Long> clientTimeAssigned;

   public TaskTotalTimeAssigned(List<BigInteger> clientList, ConcurrentHashMap<BigInteger, Long> clientTimeAssigned) {
      this.clientList = clientList;
      this.clientTimeAssigned = clientTimeAssigned;
   }

   @Override
   public void run() {
      Long totalTimeAssigned = 0L;

      for (BigInteger clientID : clientList) {
         totalTimeAssigned += clientTimeAssigned.get(clientID);
      }

      System.out.println("Total time assigned: " + totalTimeAssigned);
   }

}
