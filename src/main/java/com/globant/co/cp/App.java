package com.globant.co.cp;

import java.util.Scanner;

import com.globant.co.cp.task.ThreadWorkshop;

public class App {

   public static void main(String[] args) {
      System.out.println("Globant Acamica Thread Workshop");
      App app = new App();
      try {
         app.runThreadWorkshop();
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

   private void runThreadWorkshop() throws InterruptedException {
      String option = "";

      while (option != "q") {
         System.out.println("1. Semaphore");
         System.out.println("2. Single Thread Executor");
         System.out.println("3. Single Thread Executor");
         System.out.println("4. Single Thread Executor");
         System.out.println("5. Thread Pool");
         System.out.println("6. Thread Pool");
         System.out.println("7. Atomic Object");
         System.out.println("8. Concurrent Collection");
         System.out.println("9. Latch");
         System.out.println("10. Barrier");

         System.out.print("Enter a number 1 to 10 to run the corresponding example ('q' for exit): ");

         Scanner keyboard = new Scanner(System.in);
         option = keyboard.next();
         ThreadWorkshop workshop = new ThreadWorkshop();

         switch (option) {
            case "1":
               workshop.runSemaphoreExample();
               Thread.currentThread().join();
               break;
            case "2":
               workshop.runSingleThreadExecutorExample01();
               Thread.currentThread().join();
               break;
            case "3":
               workshop.runSingleThreadExecutorExample02();
               Thread.currentThread().join();
               break;
            case "4":
               workshop.runSingleThreadExecutorExample03();
               Thread.currentThread().join();
               break;
            case "5":
               workshop.runThreadPoolExample01();
               Thread.currentThread().join();
               break;
            case "6":
               workshop.runThreadPoolExample02();
               Thread.currentThread().join();
               break;
            case "7":
               workshop.runAtomicObjectExample();
               Thread.currentThread().join();
               break;
            case "8":
               workshop.runConcurrentCollectionExample01();
               Thread.currentThread().join();
               break;
            case "9":
               workshop.runLatchExample01();
               Thread.currentThread().join();
               break;
            case "10":
               workshop.runLatchExample02();
               Thread.currentThread().join();
               break;
            default:
               Thread.currentThread().join();
               System.out.println("That option is not valid!");
               break;
         }

         System.out.println("Option finished");
      }
   }

}