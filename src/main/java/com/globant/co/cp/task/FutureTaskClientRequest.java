package com.globant.co.cp.task;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.Callable;

import com.globant.co.cp.dto.AgendaDTO;

public class FutureTaskClientRequest implements Callable<Long> {

   private List<AgendaDTO> schedule;
   private BigInteger clientID;

   public FutureTaskClientRequest(List<AgendaDTO> schedule, BigInteger clientId) {
      this.schedule = schedule;
      this.clientID = clientId;
   }

   @Override
   public Long call() {
      schedule.stream()
            .filter(agendaDTO -> agendaDTO.getClientId() == null)
            .iterator()
            .forEachRemaining(agendaDTO -> {
               agendaDTO.setClientId(clientID);
               System.out.println("Client " + clientID + " assigned: " + agendaDTO);
            });
      Long timeAssigned = schedule.stream()
            .filter(agendaDTO -> agendaDTO.getClientId().equals(clientID))
            .count();
      System.out.println("Time assigned for client " + clientID + ": " + timeAssigned);
      return timeAssigned;
   }

}
