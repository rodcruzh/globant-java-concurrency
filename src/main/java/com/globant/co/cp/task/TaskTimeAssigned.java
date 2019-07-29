package com.globant.co.cp.task;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

import com.globant.co.cp.dto.AgendaDTO;

public class TaskTimeAssigned implements Runnable {

   private List<AgendaDTO> schedule;
   private BigInteger clientID;

   public TaskTimeAssigned(List<AgendaDTO> schedule, BigInteger clientID) {
      this.schedule = schedule;
      this.clientID = clientID;
   }

   @Override
   public void run() {
      System.out.println("Total hours per week for client " + clientID + ": " +
            schedule.stream().filter(agendaDTO ->
                  !Objects.isNull(agendaDTO.getClientId()) && agendaDTO.getClientId().equals(clientID)).count());
   }

}
