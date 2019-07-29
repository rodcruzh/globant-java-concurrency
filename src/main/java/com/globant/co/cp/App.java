package com.globant.co.cp;

import java.util.Scanner;

import com.globant.co.cp.task.ThreadWorkshop;

public class App {

   public static void main(String[] args) {
      System.out.println("Globant Thread Workshop");
      Scanner keyboard = new Scanner(System.in);
      int option = keyboard.nextInt();
      ThreadWorkshop workshop = new ThreadWorkshop();
      switch (option) {
         case 1:
            workshop.runSemaphoreExample();
            break;
         case 2:
            workshop.runSingleThreadExecutorExample01();
            break;
         case 3:
            workshop.runSingleThreadExecutorExample02();
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
            workshop.runLatchExample01();
      }
   }

}
