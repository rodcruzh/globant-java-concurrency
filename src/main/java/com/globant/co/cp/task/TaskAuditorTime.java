package com.globant.co.cp.task;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.globant.co.cp.dto.AgendaDTO;

public class TaskAuditorTime implements Runnable {

   private final List<DayOfWeek> week = Arrays.asList(
         DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
   );

   private List<AgendaDTO> schedule;
   private Semaphore availableTime;
   private Semaphore mutex;

   public TaskAuditorTime(List<AgendaDTO> schedule, Semaphore availableTime, Semaphore mutex) {
      this.schedule = schedule;
      this.availableTime = availableTime;
      this.mutex = mutex;
   }

   @Override
   public void run() {
      try {
         for (DayOfWeek day : week) {
            for (int hour = 8; hour < 18; hour++) {
               if (hour != 12) {
                  mutex.acquire();
                  AgendaDTO agenda = new AgendaDTO(BigInteger.ONE, day, BigInteger.valueOf(hour), null);
                  schedule.add(agenda);
                  System.out.println("Available time added: " + agenda);
                  mutex.release();
                  availableTime.release();
               }
            }
         }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
   }

}
