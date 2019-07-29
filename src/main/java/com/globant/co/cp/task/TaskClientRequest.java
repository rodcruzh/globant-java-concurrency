package com.globant.co.cp.task;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.globant.co.cp.dto.AgendaDTO;

public class TaskClientRequest implements Runnable {

   private List<AgendaDTO> schedule;
   private Semaphore availableTime;
   private Semaphore mutex;
   private BigInteger clientId;

   public TaskClientRequest(List<AgendaDTO> schedule, Semaphore availableTime, Semaphore mutex, BigInteger clientId) {
      this.schedule = schedule;
      this.availableTime = availableTime;
      this.mutex = mutex;
      this.clientId = clientId;
   }

   @Override
   public void run() {
      while (true) {
         try {
            availableTime.acquire();
            mutex.acquire();
            AgendaDTO agenda = schedule.get(availableTime.availablePermits());
            agenda.setClientId(clientId);
            System.out.println("Client assigned: " + agenda);
            mutex.release();
            Thread.sleep(2000);
         } catch (InterruptedException e) {
            System.out.println("Client " + clientId + " was interrupted");
         }
      }
   }

}
