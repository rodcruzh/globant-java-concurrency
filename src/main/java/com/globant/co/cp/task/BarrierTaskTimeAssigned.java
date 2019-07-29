package com.globant.co.cp.task;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

import com.globant.co.cp.dto.AgendaDTO;

public class BarrierTaskTimeAssigned implements Runnable {

   private List<AgendaDTO> schedule;
   private BigInteger clientID;
   private CyclicBarrier barrier;
   private ConcurrentHashMap<BigInteger, Long> clientTimeAssigned;

   public BarrierTaskTimeAssigned(List<AgendaDTO> schedule, BigInteger clientID, CyclicBarrier barrier, ConcurrentHashMap<BigInteger, Long> clientTimeAssigned) {
      this.schedule = schedule;
      this.clientID = clientID;
      this.barrier = barrier;
      this.clientTimeAssigned = clientTimeAssigned;
   }

   @Override
   public void run() {
      Long timeAssigned = schedule.stream().filter(agendaDTO -> !Objects.isNull(agendaDTO.getClientId())
            && agendaDTO.getClientId().equals(clientID)).count();
      clientTimeAssigned.put(clientID, timeAssigned);
      try {
         barrier.await();
      } catch (InterruptedException | BrokenBarrierException e) {
         if (barrier.isBroken()) {
            System.out.println("The barrier was broken");
         }
      }
   }

}
