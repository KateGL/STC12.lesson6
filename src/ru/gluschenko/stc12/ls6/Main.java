package ru.gluschenko.stc12.ls6;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        ThreadTimeController controller = new ThreadTimeController(20, true);
        //controller.appendWorker(new ThreadWorker("Thread 2 sec"),2);
        //controller.appendWorker(new ThreadWorker("Thread 5 sec"),5);
        //controller.appendWorker(new ThreadWorker("Thread 2_2 sec"),2);

        controller.appendWorker(new ThreadWorker("Thread 7 sec"),7);
        controller.appendWorker(new ThreadWorker("Thread 5 sec"),5);

        controller.start();
        controller.join();
        System.out.println("Завершение основного потока");

    }
}
