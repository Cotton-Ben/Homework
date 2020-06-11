package com.hsbc.us.fiet.interview.cotton_ben;
/**
 *  ben.cotton@rutgers.edu
 *
 *  A j.u.c.Callable place holder facilitate the HSBC exercise to publish j.l.Object 'events' on
 *  to an EventBus.
 *
 *  Note: this impl usable in both Single and Multiple variants of ThreadPoolExecutor invocation. provides an
 *  EventAck reference to invocation's j.u.c.Future expectation.
 */
public class PubTask<T> implements java.util.concurrent.Callable<EventACK> {
    EventBus eb;
    Object event;

    public PubTask(EventBus _eb, Object o) {
        this.eb = _eb;
        this.event = o;
    }

    @Override
    public EventACK call() throws Exception {
        System.out.println(
                "t=[" + Thread.currentThread() + "]"
                        + " PubTask Callable<EventACK> working ****"
        );
        this.eb.publishEvent(event);
        System.out.println(
                "t=[" + Thread.currentThread() + "]"
                        + " PubTask Callable<EventACK> done ****"
        );
        return new EventACK<T>() {
            @Override
            public Class producedACK(EventBus theEB) { return null; } //stub out ACK/NACK impl

            @Override
            public Class consumedACK(EventBus theEB) { return null; } //stub out ACK/NACK impl

            @Override
            public EventACK acknowledgeEvent() {
                return null;
            }
        };
    }
}
