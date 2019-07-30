package com.globant.co.cp;

import java.util.Scanner;

import com.globant.co.cp.task.ThreadWorkshop;

public class App {

   public static void main(String[] args) {
      System.out.println("Globant Thread Workshop");

      System.out.println("0. Semaphore");
      System.out.println("1. Single Thread Executor");
      System.out.println("2. Single Thread Executor");
      System.out.println("3. Single Thread Executor");
      System.out.println("4. Thread Pool");
      System.out.println("5. Thread Pool");
      System.out.println("6. Atomic Object");
      System.out.println("7. Concurrent Collection");
      System.out.println("8. Latch");
      System.out.println("9. Barrier");

      System.out.print("Enter a number 0 to 9 to run the corresponding example: ");

      Scanner keyboard = new Scanner(System.in);
      int option = keyboard.nextInt();
      ThreadWorkshop workshop = new ThreadWorkshop();

      switch (option) {
         case 0:
            workshop.runSemaphoreExample();
            break;
         case 1:
            workshop.runSingleThreadExecutorExample01();
            break;
         case 2:
            workshop.runSingleThreadExecutorExample02();
            break;
         case 3:
            workshop.runSingleThreadExecutorExample03();
            break;
         case 4:
            workshop.runThreadPoolExample01();
            break;
         case 5:
            workshop.runThreadPoolExample02();
            break;
         case 6:
            workshop.runAtomicObjectExample();
            break;
         case 7:
            workshop.runConcurrentCollectionExample01();
            break;
         case 8:
            workshop.runLatchExample01();
            break;
         case 9:
            workshop.runLatchExample02();
         default:
            break;
      }
   }

}
