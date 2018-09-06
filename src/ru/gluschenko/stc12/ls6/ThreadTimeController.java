package ru.gluschenko.stc12.ls6;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class ThreadTimeController extends Thread {
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

    //множество воркеров, можно навесить на один интервал несколько потоков
    private Hashtable<Integer, ArrayList<ThreadWorker>> scheduler = new Hashtable<>();

    ThreadTimeController(int workingTimeSec, boolean startAtZeroSecond){
        this.workSecInterval = workingTimeSec;
        this.startAtZeroSecond = startAtZeroSecond;
    }

    void appendWorker(ThreadWorker worker, int secStartInterval){
        /**
         * поток и интервал запуска
         */
        ArrayList<ThreadWorker> list = this.scheduler.get(secStartInterval);
        if (list == null){
            list = new ArrayList<ThreadWorker>();
        }
        list.add(worker);
        this.scheduler.put(secStartInterval, list);

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
        Enumeration<Integer> seconds = this.scheduler.keys();
        while (seconds.hasMoreElements()){
            Integer secNumber = seconds.nextElement();
            ArrayList<ThreadWorker> list = this.scheduler.get(secNumber);
            for(ThreadWorker wr: list){
                //System.out.println("wr:"+wr);
                wr.start();
                if(this.startAtZeroSecond){
                    //System.out.println("wr canDo:"+wr);
                    wr.setCanDo(true);
                }
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
        Enumeration<Integer> seconds = this.scheduler.keys();

        while (seconds.hasMoreElements()){
            Integer secNumber = seconds.nextElement();

            if(this.secFullPrev % secNumber == 0){
                ArrayList<ThreadWorker> list = this.scheduler.get(secNumber);
                //System.out.println("list:"+list);
                for(ThreadWorker wr: list){
                    wr.setCanDo(true);
                }
            }
        }
    }

}
