package utils;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import server.RunnableClientHandler;

public class PriorityJobScheduler {
	private ExecutorService priorityJobPoolExecutor;
    private ExecutorService priorityJobScheduler 
      = Executors.newSingleThreadExecutor();
    private PriorityBlockingQueue<RunnableClientHandler> priorityQueue;
    
    public PriorityJobScheduler(Integer poolSize, Integer queueSize) {
        priorityJobPoolExecutor = Executors.newFixedThreadPool(poolSize);
        priorityQueue = new PriorityBlockingQueue<RunnableClientHandler>(
          queueSize, Comparator.comparing(RunnableClientHandler::getPriority));
        priorityJobScheduler.execute(() -> {
            while (true) {
                try {
                    priorityJobPoolExecutor.execute(priorityQueue.take());
                } catch (InterruptedException e) {
                    // exception needs special handling
                    break;
                }
            }
        });
    }
    
    public void scheduleJob(RunnableClientHandler job) {
        priorityQueue.add(job);
    }
}
