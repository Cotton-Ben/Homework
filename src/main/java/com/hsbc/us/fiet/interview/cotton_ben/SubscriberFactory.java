package com.hsbc.us.fiet.interview.cotton_ben;
/**
 * ben.cotton@rutgers.edu
 * <p>
 * A class from which to drive the high=level custody of  subscriber callbacks off the
 * EventBus
 */

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class SubscriberFactory<T> implements Runnable {
    ExecutorService te;
    EventBus eb;

    public SubscriberFactory(ExecutorService threadExecutor, EventBus theEB) {
        this.te = threadExecutor;
        this.eb = theEB;
    }

    @Override
    public void run() {
        do {
            try {
                final Object event = this.eb.getTheTopic().take();  //consume when available
                final Future<EventACK<T>> f = te.submit(new SubTask(this.eb, event));
                final EventACK<T> eAck = f.get();
                System.out.println(
                        "t=[" + Thread.currentThread() + "]"
                                + "SubscriberFactory EventACK set=[\n" + eAck + "\n]"
                );
                System.out.println(
                        "t=[" + Thread.currentThread() + "]" +
                        "SubscriberFactory BLOCKS on EventBus.take() ... HIT CTRL-C to EXIT!"
                );
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } while (Boolean.TRUE); // NOTE: manually `kill -9` the Java VM process id
    }
}