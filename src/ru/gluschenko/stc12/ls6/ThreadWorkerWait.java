package ru.gluschenko.stc12.ls6;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ThreadWorkerWait extends Thread {
    private String name = null;
    private int secInterval =0;
    private Object monitor;
    private LocalDateTime prevStartTime = null;


    public ThreadWorkerWait(String name, int secInterval, Object monitor){
        this.name = name;
        this.secInterval = secInterval;
        this.monitor = monitor;
    }

    @Override
    public void run() {
        while(!interrupted()) {


            try {
                synchronized (this.monitor) {
                    this.monitor.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            LocalDateTime currTime = LocalDateTime.now();
            System.out.println("Thread " + this.name + ": last started at "
                        + this.prevStartTime + ". Remaining " + this.getTimeDiff(currTime) + " ms");
            this.prevStartTime = currTime;




        }

        System.out.println("Thread " + this.name+" is finished");
    }


    public double getTimeDiff(LocalDateTime currTime){
        if (this.prevStartTime == null) {
            return 0;
        }
        return ChronoUnit.NANOS.between(this.prevStartTime, currTime)/ 1_000_000_000.0;
    }

    public LocalDateTime getPrevStartTime() {
        return prevStartTime;
    }

}
