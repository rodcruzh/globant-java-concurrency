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

   public static void runSingleThreadExecutorExample03() {
      System.out.println("\nSingle Thread Executor Example 03");
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

   public void runThreadPoolExample01() {
      System.out.println("\nThread Pool Example 01");
      List<AgendaDTO> schedule = Schedule.getSchedule(false);
      ThreadPoolExecutor fixedExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
      fixedExecutor.execute(new TaskTimeAssigned(schedule, BigInteger.ONE));
      fixedExecutor.execute(new TaskTimeAssigned(schedule, BigInteger.TEN));
      fixedExecutor.shutdown();
   }

   public void runThreadPoolExample02() {
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
   }

   public void runAtomicObjectExample() {
      System.out.println("\nAtomic Example");
      final int TASKS = 5;
      final AtomicLong serialNumber = new AtomicLong(10L);
      final List<Future<Long>> serialList = new ArrayList<>();
      ThreadPoolExecutor fixedExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(TASKS);
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
