package com.hsbc.us.fiet.interview.cotton_ben.demo;

/**
 *   ben.cotton@rutgers.edu
 *   2020 June 7
 *
 A reference impl for demonstrating a valid, sound and complete delivery of  the EventBus challenge =

 https://github.com/Cotton-Ben/HSBC ... an HSBC FI eTrading DEV team exercise for interview candidates


 This impl satisfies part One of the HSBC exercise: Pub and Sub events processed by same j.l.Thread

 */

import com.hsbc.us.fiet.interview.cotton_ben.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HSBC_EXERCISE_1<T> {

    public static void main(String[] args) {

        final EventBus theEB = new EventBusHub();
        System.out.println("Hello HSBC FIET DEV exercise ONE (1)! theEB=[" + theEB + "]");

        final ExecutorService te = Executors.newSingleThreadExecutor();
        final SubscriberFactory<?> sF = new SubscriberFactory<Object>(te, theEB);
        final PublisherFactory<?> pF = new PublisherFactory<Object>(te, theEB) {
            @Override
            public void run() {
                try {
                    //note: not typing in 'final' keyword
                    Future<EventACK<?>> f1 = te.submit(
                            new PubTask(eb, "hi")
                    );
                    Future<EventACK<?>> f2 = te.submit(
                            new PubTask(eb, new StringBuffer("-hello"))
                    );
                    Future<EventACK<?>> f3 = te.submit(
                            new PubTask(eb, new StringBuilder("+hello"))
                    );

                    AlgoComplexEventProcessor algo = new AlgoComplexEventProcessor("PX");

                    EventACK<?> e1 = f1.get();
                    EventACK<?> e2 = f2.get();
                    EventACK<?> e3 = f3.get();
                    EventACK<?> algoACK1, algoACK2, algoACK3, algoACK4;

                    Future<EventACK<?>> AlgoTickF1 = te.submit(
                            new PubTask(this.eb, algo.tick(100.75))
                    );
                    Future<EventACK<?>> AlgoTickF2 = te.submit(
                            new PubTask(this.eb, algo.tick(104.55))
                    );
                    Future<EventACK<?>> AlgoTickF3 = te.submit(
                            new PubTask(this.eb, algo.tick(99.05))
                    );
                    Future<EventACK<?>> AlgoTickF4 = te.submit(
                            new PubTask(this.eb, algo.tick(-1)) //algo 'commit' sentinel
                    );
                    algoACK1 = AlgoTickF1.get();
                    algoACK2 = AlgoTickF2.get();
                    algoACK3 = AlgoTickF3.get();
                    algoACK4 = AlgoTickF4.get();
                    System.out.println(
                            "t=[" + Thread.currentThread() + "]" +
                                    "PublisherFactory EventACK set=[\n"
                                    + e1 + ",\n"
                                    + e2 + ",\n"
                                    + e3 + ",\n"
                                    + algoACK1 + ",\n"
                                    + algoACK2 + ",\n"
                                    + algoACK3 + ",\n"
                                    + algoACK4 + "]"
                    );
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };

        //forgive the raw usage of these collection references' constructions (w/o interface) ...
        //would normally refactor, and inject via a factory Spring join-point

        final FilteredEventBlockingQueue s1 = new FilteredEventBlockingQueue(10);
        final FilteredEventBlockingQueue s2 = new FilteredEventBlockingQueue(10);
        final FilteredEventBlockingQueue s3 = new FilteredEventBlockingQueue(10);
        final FilteredEventBlockingQueue s4 = new FilteredEventBlockingQueue(10);

        theEB.addSubscriber(s1);
        theEB.addSubscriber(s2);
        theEB.addSubscriberFiltered(StringBuilder.class, s3);
        theEB.addSubscriberFiltered(AlgoComplexEventProcessor.class, s4);

        final Thread pubFactoryThread = new Thread(pF);
        final Thread subFactoryThread = new Thread(sF);

        pubFactoryThread.start(); //NOTE: runs a small 'factory' of events
        subFactoryThread.start(); //NOTE: runs for 1 hour 'listening' for events
    }

}

