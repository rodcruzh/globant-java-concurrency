package com.globant.co.cp.task;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import com.globant.co.cp.dto.AgendaDTO;

public class LatchTaskTimeAssigned implements Runnable {

   private List<AgendaDTO> schedule;
   private BigInteger clientID;
   private CountDownLatch latch;
   private ConcurrentHashMap<BigInteger, Long> clientTimeAssigned;

   public LatchTaskTimeAssigned(List<AgendaDTO> schedule, BigInteger clientID, CountDownLatch latch, ConcurrentHashMap<BigInteger, Long> clientTimeAssigned) {
      this.schedule = schedule;
      this.clientID = clientID;
      this.latch = latch;
      this.clientTimeAssigned = clientTimeAssigned;
   }

   @Override
   public void run() {
      Long timeAssigned = schedule.stream().filter(agendaDTO -> !Objects.isNull(agendaDTO.getClientId())
            && agendaDTO.getClientId().equals(clientID)).count();
      clientTimeAssigned.put(clientID, timeAssigned);
      latch.countDown();
   }

}
