package com.hsbc.us.fiet.interview.cotton_ben;
/**
 *  ben.cotton@rutgers.edu
 *
 *  A class from which to drive the high=level custody of  publications on to the
 *  EventBus
 *
 */
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class PublisherFactory<T> implements Runnable {
    ExecutorService te;
    protected EventBus eb;

    public PublisherFactory(ExecutorService threadExecutor, EventBus theEB) {
        this.te = threadExecutor;
        this.eb = theEB;
    }

    @Override
    public void run() {
        /**
         *  ben.cotton@rutgers.edu
         *
         *  For this exercise, a Thread accommodating placeholder for
         *  our set of @Test s to render Pub events
         */
    }


}

