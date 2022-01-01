package com.moumangtai.threadpool;

/**
 * 拒绝策略接口
 */
public interface RejectPolicy {

    /**
     * 拒绝方法
     * @param tasks
     * @param task
     */
    void reject(BlockingQueue tasks,Runnable task);
}
