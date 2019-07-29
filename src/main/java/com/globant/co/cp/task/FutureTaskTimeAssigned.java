package com.globant.co.cp.task;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import com.globant.co.cp.dto.AgendaDTO;

public class FutureTaskTimeAssigned implements Callable<Long> {

   private List<AgendaDTO> schedule;
   private BigInteger clientID;

   public FutureTaskTimeAssigned(List<AgendaDTO> schedule, BigInteger clientID) {
      this.schedule = schedule;
      this.clientID = clientID;
   }

   @Override
   public Long call() throws Exception {
      Long timeAssigned = schedule.stream().filter(agendaDTO -> !Objects.isNull(agendaDTO.getClientId())
            && agendaDTO.getClientId().equals(clientID)).count();
      System.out.println("Time assigned for client " + clientID + ": " + timeAssigned);
      return timeAssigned;
   }

}
