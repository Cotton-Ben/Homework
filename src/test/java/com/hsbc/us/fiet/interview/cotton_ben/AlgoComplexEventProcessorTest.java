package com.hsbc.us.fiet.interview.cotton_ben;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class AlgoComplexEventProcessorTest<T> {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void main() {
        final EventBus theEB = new EventBusHub();
        System.out.println("Coalesce EventBus Px TICKS in Coalesece policy=LIFO (Multiple Thread)s");

        //final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        final ExecutorService multipleThreadExecutor = Executors.newFixedThreadPool(4);
        final SubscriberFactory<?> sF = new SubscriberFactory<Object>(
                multipleThreadExecutor,
                theEB
        );
        final PublisherFactory<?> pF = new PublisherFactory<Object>(
                multipleThreadExecutor,
                theEB
        ) {
            @Override
            public void run() {
                System.out.println(
                        "t=[" + Thread.currentThread() + "]" + "PublisherFactory starting ..."
                );
                try {
                    AlgoComplexEventProcessor algo = new AlgoComplexEventProcessor("");

                    EventACK<T> algoACK1, algoACK2, algoACK3, algoACK4;

                    Future<EventACK<T>> AlgoTickF1 = te.submit(
                            new PubTask(this.eb, algo.tick(100.75))
                    );
                    Future<EventACK<T>> AlgoTickF2 = te.submit(
                            new PubTask(this.eb, algo.tick(104.55))
                    );
                    Future<EventACK<T>> AlgoTickF3 = te.submit(
                            new PubTask(this.eb, algo.tick(99.05))
                    );
                    Future<EventACK<T>> AlgoTickF4 = te.submit(
                            new PubTask(this.eb, algo.tick(-1)) //algo 'commit' sentinel
                    );
                    algoACK1 = AlgoTickF1.get();
                    algoACK2 = AlgoTickF2.get();
                    algoACK3 = AlgoTickF3.get();
                    algoACK4 = AlgoTickF4.get();
                    System.out.println(
                            "t=[" + Thread.currentThread() + "]" +
                                    "PublisherFactory EventACK set=[\n"
                                    + algoACK1 + ",\n"
                                    + algoACK2 + ",\n"
                                    + algoACK3 + ",\n"
                                    + algoACK4 + "]"
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
        final FilteredEventBlockingQueue s3 = new FilteredEventBlockingQueue(10);
        final FilteredEventBlockingQueue s4 = new FilteredEventBlockingQueue(10);

        theEB.addSubscriber(s1);
        theEB.addSubscriber(s2);
        theEB.addSubscriberFiltered(StringBuilder.class, s3);
        theEB.addSubscriberFiltered(AlgoComplexEventProcessor.class, s4);
        assertTrue(theEB.getTheSubsciberList().size() == 4);


        final Thread pubFactoryThread = new Thread(pF);
        final Thread subFactoryThread = new Thread(sF);

        pubFactoryThread.start();
        subFactoryThread.start();
        assertTrue(subFactoryThread.isAlive());
        assertTrue(pubFactoryThread.isAlive());

    }
}