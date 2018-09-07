package ru.gluschenko.stc12.ls6;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        //testWithoutWait();
        testWithtWait();

    }

    public static void testWithtWait() throws InterruptedException{
        System.out.println("Реализация с монитором");
        ThreadTimeMonitor controller = new ThreadTimeMonitor(20, true);
        controller.start();
        controller.join();
        System.out.println("Завершение основного потока");
        controller.interrupt();
    }

    public static void testWithoutWait() throws InterruptedException{
        System.out.println("Реализация без монитора");
        ThreadTimeController controller = new ThreadTimeController(20, true);
        //controller.appendWorker(new ThreadWorker("Thread 2 sec"),2);
        //controller.appendWorker(new ThreadWorker("Thread 5 sec"),5);
        //controller.appendWorker(new ThreadWorker("Thread 2_2 sec"),2);

        controller.appendWorker(new ThreadWorker("Thread 7 sec"),7);
        controller.appendWorker(new ThreadWorker("Thread 5 sec"),5);

        controller.start();
        controller.join();
        System.out.println("Завершение основного потока");
        controller.interrupt();

    }
}
