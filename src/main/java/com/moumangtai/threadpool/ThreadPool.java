package com.moumangtai.threadpool;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 线程池的实现
 */

public class ThreadPool {

    // 任务队列
    private BlockingQueue tasks;

    // 存放核心线程的容器
    private Set<Worker> workers = new HashSet<>();

    // 核心线程池的数量
    private int coreThreadSize;

    // 任务队列长度
    private int queueSize;

    // 超时等待时长
    private long timeout;

    // 超时等待时长单位
    private TimeUnit timeUnit;

    // 拒绝策略（采用接口）
    private RejectPolicy rejectPolicy;


    public ThreadPool(int coreThreadSize, int queueSize, long timeout, TimeUnit timeUnit,RejectPolicy rejectPolicy) {
        this.coreThreadSize = coreThreadSize;
        this.queueSize = queueSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.tasks = new BlockingQueue(queueSize);
        this.rejectPolicy = rejectPolicy;
    }

    // 执行线程方法
    public void execute(Runnable task){
        synchronized (workers){
            if(workers.size()==coreThreadSize){
                System.out.println("线程池已经满了，加入任务队列");
                // 1.如果线程数量达到阂值，则放到任务队列中(可以采取多种添加策略）
                tasks.tryPush(rejectPolicy,task);
            } else {
                // 2.如果没有达到，则创建新的线程去执行任务
                System.out.println("线程池未满，新建核心线程");
                Worker worker = new Worker(task);
                workers.add(worker);
                worker.start();
            }
        }
    }

    // 线程对象
    class Worker extends Thread{

        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            //1.如果有任务则执行
            //2.如果没有任务则到队列中取
            while (task != null || (task = tasks.get(timeout,timeUnit)) != null){
                try {
                    System.out.println("执行任务"+task);
                    task.run();
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    task = null;
                }
            }
            synchronized (workers){
                System.out.println("线程移除");
                workers.remove(this);
            }
        }
    }
}
