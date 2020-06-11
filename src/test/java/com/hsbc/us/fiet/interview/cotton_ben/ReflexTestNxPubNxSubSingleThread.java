package com.hsbc.us.fiet.interview.cotton_ben;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class ReflexTestNxPubNxSubSingleThread<T> {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void main() {
        final EventBus theEB = new EventBusHub();
        System.out.println("ReflexTest Nxpub<-->Nxsub, singleThread theEB=[" + theEB + "]");

        //final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        final ExecutorService te = Executors.newSingleThreadExecutor(); // newFixedThreadPool(1);
        final SubscriberFactory<?> sF = new SubscriberFactory<Object>(
                te,
                theEB
        );
        final PublisherFactory<?> pF = new PublisherFactory<Object>(
                te,
                theEB
        ) {
            @Override
            public void run() {
                System.out.println("t=[" + Thread.currentThread() + "]" + "PublisherFactory starting ...");
                try {
                    //note: not typing in 'final' keyword
                    Future<EventACK<T>> f1 = te.submit(
                            new PubTask(this.eb, "hi")
                    );
                    Future<EventACK<T>> f2 = te.submit(
                            new PubTask(this.eb, "hi")
                    );

                    assertTrue(!f1.isDone());
                    assertTrue(!f2.isDone());
                    EventACK<T> e1 = f1.get();
                    EventACK<T> e2 = f2.get();

                    System.out.println(
                            "t=[" + Thread.currentThread() + "]" +
                                    "PublisherFactory EventACK set=[\n"
                                    + e1 + ",\n"
                                    + "]"
                    );
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                System.out.println("t=[" + Thread.currentThread() + "]" + "PublisherFactory ending ...");
            }
        };

        //forgive the raw usage of these collection references construction (w/o interface) ...
        //would normally refactor, and inject via a factory Spring join-point
        final FilteredEventBlockingQueue s1 = new FilteredEventBlockingQueue(10);
        final FilteredEventBlockingQueue s2 = new FilteredEventBlockingQueue(10);

        theEB.addSubscriber(s1);
        theEB.addSubscriber(s2);

        assertTrue(theEB.getTheSubsciberList().size() == 2);

        final Thread pubFactoryThread = new Thread(pF);
        final Thread subFactoryThread = new Thread(sF);

        pubFactoryThread.start();
        subFactoryThread.start();
        assertTrue(subFactoryThread.isAlive());
        assertTrue(pubFactoryThread.isAlive());

    }
}

