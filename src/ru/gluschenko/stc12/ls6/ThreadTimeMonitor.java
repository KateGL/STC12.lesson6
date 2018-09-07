package ru.gluschenko.stc12.ls6;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class ThreadTimeMonitor extends Thread {
    /***
     * Класс-контроллер вызова потоков     *
     * Отсчитывает полные секунды от начала своего старта, и выставляет каждому потоку флаг для запуска работы
     * Можно на один интервал навешивать несколько потоков
     * Соответственно шаг вызова потоков не может быть меньше секунды
     * Время считаем не c помощью sleep, а смотрим разницу между началом старта и текущим временем
     */
    //спим меньше секунды, тк не доверяем sleep :)
    private static int sleepInterval = 200;
    // время работы контроллера
    private int workSecInterval = 1;
    //время старта контроллера
    private LocalDateTime startTime = null;
    //стартовать ли потоки в нулевую секунду
    private boolean startAtZeroSecond = false;
    // номер предыдущей полной секунды
    private int secFullPrev = 0;

    //множество воркеры и мониторы
    Object monitor5 = new Object();
    ThreadWorkerWait work5 = new ThreadWorkerWait("Worker 5sec",5, this.monitor5);
    Object monitor7 = new Object();
    ThreadWorkerWait work7 = new ThreadWorkerWait("Worker 7sec",7, this.monitor7);



    ThreadTimeMonitor(int workingTimeSec, boolean startAtZeroSecond){
        this.workSecInterval = workingTimeSec;
        this.startAtZeroSecond = startAtZeroSecond;
    }


    public double getTimeDiff(LocalDateTime currTime){
        /***
         * сколько прошло секунд с начала работы
         */
        if (this.startTime == null) {
            return 0;
        }
        return ChronoUnit.NANOS.between(this.startTime, currTime)/ 1_000_000_000.0;
    }

    @Override
    public void run() {
        this.startTime = LocalDateTime.now();

        //инициализируем воркеры и, если требуется, стартуем в первый раз
        this.work5.start();
        this.work7.start();
        if(this.startAtZeroSecond) {
            synchronized (this.monitor5) {
                this.monitor5.notifyAll();
            }

            synchronized (this.monitor7){
                this.monitor7.notifyAll();
            }
        }


        //основной цикл обработки
        //тут мы проскакиваем мимо последней секунды, если она нужна, то можно какую-то погрешность задать
        while (!interrupted() && this.getTimeDiff(LocalDateTime.now()) <= this.workSecInterval) {
            //System.out.println("ThreadTimeController sec "+this.getTimeDiff(LocalDateTime.now())+" remaining");
            this.callWorkers();

            try {
                //надо дать и фонтану отдохнуть
                this.sleep(sleepInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        System.out.println("ThreadTimeController sec "+this.getTimeDiff(LocalDateTime.now())+" remaining");
        System.out.println("ThreadTimeController is finished");
    }

    private void callWorkers(){
        //вычисляем прошла ли полная секунда c последнего вызова и ее номер
        LocalDateTime nowTime = LocalDateTime.now();
        int fullSecond =  (int)this.getTimeDiff(nowTime);
        //System.out.println("diff:"+this.getTimeDiff(nowTime)+" prev:"+this.secFullPrev+" full:"+fullSecond);

        if (fullSecond <= this.secFullPrev) {
            //полная секунда еще не прошла, отдыхаем
            return;
        }
        //если прошла, то смотрим надо ли вызывать потоки
        System.out.println("fullSecond:"+fullSecond);
        this.secFullPrev = fullSecond;
        if(this.secFullPrev % 5 == 0) {
            synchronized (this.monitor5) {
                this.monitor5.notifyAll();
            }
        }

        if(this.secFullPrev % 7 == 0) {
            synchronized (this.monitor7) {
                this.monitor7.notifyAll();
            }
        }


    }

}
