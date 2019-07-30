package com.globant.co.cp.task;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.globant.co.cp.dto.AgendaDTO;
import com.globant.co.cp.util.Schedule;

public class ThreadWorkshop {

   public void runSemaphoreExample() {
      System.out.println("\nSemaphore Example");
      System.out.println("Overview: An Agenda is set for an Auditor ID 1, and concurrent tasks synchronized by semaphores" +
            "\nassign time to Client ID 1 and Client ID 10 according to decreasing time available up to 45 hour a week." +
            "\nTherefore, Auditor is assigned time available for each Client that sums at most 45 hours.");

      List<AgendaDTO> schedule = new ArrayList<>();
      Semaphore availableTime = new Semaphore(0);
      Semaphore mutex = new Semaphore(1);

      Thread auditor = new Thread(new TaskAuditorTime(schedule, availableTime, mutex));
      Thread client1 = new Thread(new TaskClientRequest(schedule, availableTime, mutex, BigInteger.ONE));
      Thread client2 = new Thread(new TaskClientRequest(schedule, availableTime, mutex, BigInteger.TEN));

      auditor.start();
      client1.start();
      client2.start();

      try {
         auditor.join();

         while (availableTime.availablePermits() > 0) {
            Thread.sleep(5000);
         }
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

      client1.interrupt();
      client2.interrupt();

      System.out.println("Schedule: " + schedule);
   }

   public void runSingleThreadExecutorExample01() {
      System.out.println("\nSingle Thread Executor Example 01");
      System.out.println("Overview: An Agenda is set for Auditor ID 1 via an Executor Service that works with" +
            "\na Single Thread.");

      ExecutorService singleExecutor = Executors.newSingleThreadExecutor();

      try {
         singleExecutor.execute(() -> {
            for (DayOfWeek day : Schedule.WEEK) {
               for (int hour = 8; hour < 18; hour++) {
                  if (hour != 12) {
                     AgendaDTO agenda = new AgendaDTO(BigInteger.ONE, day, BigInteger.valueOf(hour), null);
                     System.out.println("Available time added: " + agenda);
                  }
               }
            }
         });
      } finally {
         singleExecutor.shutdown();
      }
   }

   public void runSingleThreadExecutorExample02() {
      System.out.println("\nSingle Thread Executor Example 02");
      System.out.println("Overview: A Single Thread Executor is assigned with two tasks to run that run one at time" +
            "\nin spite of a Thread sleep of 5 seconds in the former one. That happens due to a Thread availability" +
            "\nof one with is able to run just one task at time.");

      ExecutorService singleExecutor = Executors.newSingleThreadExecutor();

      try {
         singleExecutor.execute(() -> {
            try {
               Thread.sleep(5000);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
            System.out.println("I ran first!");
         });
         singleExecutor.execute(() -> System.out.println("I ran after!"));
      } finally {
         singleExecutor.shutdown();
      }
   }

   public void runSingleThreadExecutorExample03() {
      System.out.println("\nSingle Thread Executor Example 03");
      System.out.println("Overview: A Single Thread Executor runs a task than shows a message 5 seconds after it is started," +
            "\nmeanwhile it is checked the end of the task and a message is printed pointing out that actually it is not ended.");

      ExecutorService singleExecutor = Executors.newSingleThreadExecutor();

      try {
         singleExecutor.execute(() -> {
            try {
               Thread.sleep(5000);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
            System.out.println("I ran after!");
         });
      } finally {
         singleExecutor.shutdown();
      }

      try {
         singleExecutor.awaitTermination(2L, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

      if (!singleExecutor.isTerminated()) {
         System.out.println("I ran after's not terminated!");
      }
   }

   // TODO: Assign client time
   public void runThreadPoolExample01() {
      System.out.println("\nThread Pool Example 01");
      System.out.println("Overview: An Agenda is set for an Auditor ID 1, and a Fixed Thread Pool of two threads" +
            "\nassign time available for Client ID 1 and 10 in a parallel way. Therefore, Auditor is assigned time" +
            "\navailable for each Client that sums at most 45 hours.");

      List<AgendaDTO> schedule = Schedule.getSchedule(false);
      ThreadPoolExecutor fixedExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
      fixedExecutor.execute(new TaskTimeAssigned(schedule, BigInteger.ONE));
      fixedExecutor.execute(new TaskTimeAssigned(schedule, BigInteger.TEN));
      fixedExecutor.shutdown();
   }

   // TODO: Assign client time
   public void runThreadPoolExample02() {
      System.out.println("\nThread Pool Example 02");
      System.out.println("Overview: An Agenda is set for an Auditor ID 1, and a Fixed Thread Pool of two threads" +
            "\nassign time available for Client ID 1 and 10 in a parallel way through future tasks. Therefore, Auditor" +
            "\nis assigned time available for each Client that sums at most 45 hours.");

      List<AgendaDTO> schedule = Schedule.getSchedule(false);
      ThreadPoolExecutor fixedExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
      Future<Long> totalTime01 = fixedExecutor.submit(new FutureTaskTimeAssigned(schedule, BigInteger.ONE));
      Future<Long> totalTime10 = fixedExecutor.submit(new FutureTaskTimeAssigned(schedule, BigInteger.TEN));

      try {
         System.out.println("Client " + BigInteger.ONE + ": " + totalTime01.get());
         System.out.println("Client " + BigInteger.TEN + ": " + totalTime10.get());
      } catch (InterruptedException | ExecutionException e) {
         e.printStackTrace();
      }

      fixedExecutor.shutdown();
      Schedule.printSchedule();
   }

   public void runAtomicObjectExample() {
      System.out.println("\nAtomic Example");
      System.out.println("Overview: A Serial Number is increased by five Thread Pool given a increase value task. The" +
            "\ngiven value is an Atomic Long Cbject that manage concurrent task.");

      final int TASKS = 5;
      final AtomicLong serialNumber = new AtomicLong(10L);
      final List<Future<Long>> serialList = new ArrayList<>();
      ThreadPoolExecutor fixedExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(TASKS);

      System.out.println("Serial Number first value: " + serialNumber.get());

      for (int i = 0; i < TASKS; i++) {
         serialList.add(fixedExecutor.submit(new FutureTaskSerialNumber(serialNumber)));
      }

      fixedExecutor.shutdown();

      try {
         Thread.sleep(5000L);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

      serialList.forEach(s -> {
         try {
            System.out.println("Final serial: " + s.get());
         } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
         }
      });

      System.out.println("Very final serial: " + serialNumber.get());
   }

   public void runConcurrentCollectionExample01() {
      System.out.println("\nConcurrent Collection Example");
      System.out.println("Overview: An Agenda is set for an Auditor ID 1, and a Cached Thread Pool of random threads" +
            "\nassign time available for Client ID 0, ID 1 and ID 10 in a parallel way through future tasks. Therefore," +
            "\nAuditor is assigned time available for each Client that sums at most 45 hours. The Agenda is stored in a" +
            "\nConcurrent Collection.");

      List<AgendaDTO> schedule = Schedule.getSchedule(true);
      List<BigInteger> clientList = Arrays.asList(BigInteger.ZERO, BigInteger.ONE, BigInteger.TEN);
      List<Future<Long>> allClientsAssigned = new ArrayList<>();
      ThreadPoolExecutor cachedThreadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();

      for (BigInteger clientID : clientList) {
         allClientsAssigned.add(cachedThreadPool.submit(new FutureTaskClientRequest(schedule, clientID)));
      }

      Long totalTimeAssigned = 0L;

      for (Future<Long> assigned : allClientsAssigned) {
         try {
            totalTimeAssigned += assigned.get();
         } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
         }
      }

      System.out.println("Total time assigned: " + totalTimeAssigned);
   }

   public void runLatchExample01() {
      System.out.println("\nLatch Example");
      System.out.println("Overview: An Agenda is set for an Auditor ID 1, and a Cached Thread Pool of random threads" +
            "\nassign time available for Client ID 0, ID 1 and ID 10 in a parallel way through future tasks. Therefore," +
            "\nAuditor is assigned time available for each Client that sums at most 45 hours. The Agenda is stored in a" +
            "\nConcurrent Collection. A Latch is used to await for results for the three Clients in order to print their" +
            "\ntotal assigned time at the end of a Fixed Executor run.");

      List<AgendaDTO> schedule = Schedule.getSchedule(true);
      List<BigInteger> clientList = Arrays.asList(BigInteger.ZERO, BigInteger.ONE, BigInteger.TEN);
      ThreadPoolExecutor fixedExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
      List<Future<Long>> allClientsAssigned = new ArrayList<>();
      ThreadPoolExecutor cachedThreadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();

      for (BigInteger clientID : clientList) {
         allClientsAssigned.add(cachedThreadPool.submit(new FutureTaskClientRequest(schedule, clientID)));
      }

      try {
         cachedThreadPool.awaitTermination(5, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

      cachedThreadPool.shutdown();

      CountDownLatch latch = new CountDownLatch(3);
      ConcurrentHashMap<BigInteger, Long> clientTimeAssigned = new ConcurrentHashMap<>();

      for (BigInteger clientID : clientList) {
         fixedExecutor.execute(new LatchTaskTimeAssigned(schedule, clientID, latch, clientTimeAssigned));
      }

      try {
         latch.await(3, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

      Long totalTimeAssigned = 0L;

      for (BigInteger clientID : clientList) {
         totalTimeAssigned += clientTimeAssigned.get(clientID);
      }

      fixedExecutor.shutdown();

      System.out.println("Total time assigned: " + totalTimeAssigned);
   }

   public void runLatchExample02() {
      System.out.println("\nBarrier Example");
      System.out.println("Overview: An Agenda is set for an Auditor ID 1, and a Cached Thread Pool of random threads" +
            "\nassign time available for Client ID 0, ID 1 and ID 10 in a parallel way through future tasks. Therefore," +
            "\nAuditor is assigned time available for each Client that sums at most 45 hours. The Agenda is stored in a" +
            "\nConcurrent Collection. A Barrier is used to await for results for the three Clients in order to print their" +
            "\ntotal assigned time at the end of a Fixed Executor run.");

      List<AgendaDTO> schedule = Schedule.getSchedule(true);
      List<BigInteger> clientList = Arrays.asList(BigInteger.ZERO, BigInteger.ONE, BigInteger.TEN);
      ThreadPoolExecutor fixedExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
      List<Future<Long>> allClientsAssigned = new ArrayList<>();
      ThreadPoolExecutor cachedThreadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();

      for (BigInteger clientID : clientList) {
         allClientsAssigned.add(cachedThreadPool.submit(new FutureTaskClientRequest(schedule, clientID)));
      }

      try {
         cachedThreadPool.awaitTermination(5, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

      cachedThreadPool.shutdown();

      ConcurrentHashMap<BigInteger, Long> clientTimeAssigned = new ConcurrentHashMap<>();
      CyclicBarrier barrier = new CyclicBarrier(3, new TaskTotalTimeAssigned(clientList, clientTimeAssigned));

      for (BigInteger clientID : clientList) {
         fixedExecutor.execute(new BarrierTaskTimeAssigned(schedule, clientID, barrier, clientTimeAssigned));
      }

      try {
         TimeUnit.MILLISECONDS.sleep(3000L);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

      fixedExecutor.shutdown();
   }

}
