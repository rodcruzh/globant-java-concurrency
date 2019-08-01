package com.globant.co.cp;

import java.util.Scanner;

import com.globant.co.cp.task.ThreadWorkshop;

public class App {

   public static void main(String[] args) {
      System.out.println("Globant Acamica Thread Workshop");
      App app = new App();
      String option = app.chooseOption();
      app.runThreadWorkshop(option);
   }

   private String chooseOption() {
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

      System.out.print("Enter a number 1 to 10 to run the corresponding example: ");

      Scanner keyboard = new Scanner(System.in);
      return keyboard.next();
   }

   private void runThreadWorkshop(String option) {
      ThreadWorkshop workshop = new ThreadWorkshop();

      switch (option) {
         case "1":
            workshop.runSemaphoreExample();
            break;
         case "2":
            workshop.runSingleThreadExecutorExample01();
            break;
         case "3":
            workshop.runSingleThreadExecutorExample02();
            break;
         case "4":
            workshop.runSingleThreadExecutorExample03();
            break;
         case "5":
            workshop.runThreadPoolExample01();
            break;
         case "6":
            workshop.runThreadPoolExample02();
            break;
         case "7":
            workshop.runAtomicObjectExample();
            break;
         case "8":
            workshop.runConcurrentCollectionExample01();
            break;
         case "9":
            workshop.runLatchExample01();
            break;
         case "10":
            workshop.runLatchExample02();
            break;
         default:
            break;
      }
   }

}