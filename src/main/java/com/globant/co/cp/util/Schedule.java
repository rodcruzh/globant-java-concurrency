package com.globant.co.cp.util;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.globant.co.cp.dto.AgendaDTO;

public class Schedule {

   private static List<AgendaDTO> schedule = null;

   public static final List<DayOfWeek> WEEK = Arrays.asList(
         DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
   );

   public static List<AgendaDTO> getSchedule(boolean concurrent) {
      if (schedule != null)
         return schedule;

      schedule = concurrent ? new CopyOnWriteArrayList<>() : new ArrayList<>();

      for (DayOfWeek day : WEEK) {
         for (int hour = 8; hour < 18; hour++) {
            if (hour != 12) {
               AgendaDTO agenda = new AgendaDTO(BigInteger.ONE, day, BigInteger.valueOf(hour), null);
               schedule.add(agenda);
               System.out.println("Available time added: " + agenda);
            }
         }
      }

      return schedule;
   }

   public static void printSchedule() {
      System.out.println("Schedule: " + schedule);
   }

}
