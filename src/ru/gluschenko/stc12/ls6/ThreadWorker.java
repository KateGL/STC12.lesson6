package ru.gluschenko.stc12.ls6;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ThreadWorker extends Thread {
    // интервал вызова в секундах
    private static int sleepInterval = 20;
    private String name;
    private LocalDateTime prevStartTime = null;
    private boolean isCanDo = false;

    public ThreadWorker(String name){
        this.name = name;
    }


    @Override
    public void run() {
        while(!interrupted()) {

            if(this.isCanDo) {
                LocalDateTime currTime = LocalDateTime.now();
                System.out.println("Thread " + this.name + ": last started at "
                        + this.prevStartTime + ". Remaining " + this.getTimeDiff(currTime) + " ms");
                this.prevStartTime = currTime;
                this.isCanDo = false;
            }

            try {
                this.sleep(sleepInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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

    public boolean isCanDo() {
        return isCanDo;
    }

    public void setCanDo(boolean canDo) {
        isCanDo = canDo;
    }
}
