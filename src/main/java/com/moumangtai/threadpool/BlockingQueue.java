package com.moumangtai.threadpool;


import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 阻塞队列
 */
public class BlockingQueue {

    // 任务队列
    private Deque<Runnable> tasks = new ArrayDeque<>();

    // 锁
    private ReentrantLock lock = new ReentrantLock();

    private Condition emptyWait = lock.newCondition();

    private Condition fullWait = lock.newCondition();

    // 队列容量
    private int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    // 获取任务（堵塞死等）
    public Runnable get() {
        lock.lock();
        try {
            while (tasks.isEmpty()) {
                try {
                    emptyWait.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            fullWait.signal();
            return tasks.pollFirst();
        } finally {
            lock.unlock();
        }
    }

    // 获取任务（超时等待）
    public Runnable get(long timeout, TimeUnit timeUnit) {
        lock.lock();
        try {
            long nanos = timeUnit.toNanos(timeout);
            while (tasks.isEmpty()) {
                try {
                    if (nanos <= 0) {
                        return null;
                    }
                    nanos = emptyWait.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Runnable runnable = tasks.pollFirst();
            fullWait.signal();
            return runnable;
        } finally {
            lock.unlock();
        }
    }

    // 添加任务（堵塞死等）
    public void push(Runnable task) {
        lock.lock();
        try {
            while (tasks.size() == capacity) {
                try {
                    fullWait.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            tasks.add(task);
            emptyWait.signal();
        } finally {
            lock.unlock();
        }
    }

    // 添加任务（堵塞死等）
    public void push(Runnable task, long timeout, TimeUnit timeUnit) {
        lock.lock();
        try {
            long nanos = timeUnit.toNanos(timeout);
            while (tasks.size() == capacity) {
                try {
                    if (nanos <= 0) {
                        return;
                    }
                    nanos = fullWait.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            tasks.add(task);
            emptyWait.signal();
        } finally {
            lock.unlock();
        }
    }

    public int size(){
        lock.lock();
        try {
            return tasks.size();
        } finally {
            lock.unlock();
        }
    }

    public void tryPush(RejectPolicy rejectPolicy,Runnable task) {
        lock.lock();
        try {
            if(tasks.size() == capacity){
                rejectPolicy.reject(this,task);
            } else {
                tasks.add(task);
                emptyWait.signal();
            }
        } finally {
            lock.unlock();
        }
    }
}
