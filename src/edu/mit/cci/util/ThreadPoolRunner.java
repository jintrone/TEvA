package edu.mit.cci.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPoolRunner {
    public static final long serialVersionUID = 1L;

    // Our simulation queue and task pool
    private BlockingQueue<Runnable> tasks = null;
    private ThreadPoolExecutor threadPool = null;
    private int activeJobs = 0;

    private final Lock lock = new ReentrantLock();

    /**
     * The size of the queue of runnables will be this many times larger than
     * the number of threads.
     */
    private static final int queueSizeMultiplier = 10;

    public ThreadPoolRunner(int threads) {
        if (tasks == null) {

            tasks = new ArrayBlockingQueue<Runnable>(threads
                    * queueSizeMultiplier);

            threadPool = new ThreadPoolExecutor(threads, threads,
                    Long.MAX_VALUE, TimeUnit.NANOSECONDS, tasks);

        }
    }


    public synchronized void runModel(Runnable model) {
        // Wait for space to open up in the queue
        while (tasks.remainingCapacity() < 1) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        ThreadPoolHelper helper = new ThreadPoolHelper(model);

        threadPool.submit(helper);

    }

    public void finish() {
		/*
		 * Check every 0.1 seconds to see if the task queue is empty. Print
		 * status messages for debug purposes.
		 */

        while (activeJobs != 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("All Simulations Finished.");
        threadPool.shutdown();
        threadPool = null;

    }

    private class ThreadPoolHelper implements Runnable {

        Runnable model;

        ThreadPoolHelper(final Runnable model) {
            this.model = model;
        }

        @Override
        public void run() {
            lock.lock();
            activeJobs++;
            lock.unlock();

            model.run();

            lock.lock();
            activeJobs--;
            lock.unlock();
        }

    }

}