package com.globant.co.cp.dto;

import java.math.BigInteger;
import java.time.DayOfWeek;

public class AgendaDTO {

   private BigInteger auditorId;
   private DayOfWeek day;
   private BigInteger hour;
   private BigInteger clientId;

   public AgendaDTO(BigInteger auditorId, DayOfWeek day, BigInteger hour, BigInteger clientId) {
      this.auditorId = auditorId;
      this.day = day;
      this.hour = hour;
      this.clientId = clientId;
   }

   public void setClientId(BigInteger clientId) {
      this.clientId = clientId;
   }

   public BigInteger getClientId() {
      return clientId;
   }

   @Override
   public String toString() {
      return "AgendaDTO{" +
            "auditorId=" + auditorId +
            "_day=" + day +
            "_hour=" + hour +
            "_clientId=" + clientId +
            '}';
   }

}
