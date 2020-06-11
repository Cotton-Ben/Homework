package com.hsbc.us.fiet.interview.cotton_ben;

/**
 * ben.cotton@rutgers.edu
 * <p>
 * A j.u.c.Callable place holder to facilitate the HSBC exercise to subscribe to EventBus publications.
 * <p>
 * Note: this impl usable in both Single and Multiple variants of ThreadPoolExecutor invocation. provides an
 * EventAck reference to invocation's j.u.c.Future expectation.
 */
public class SubTask<T> implements java.util.concurrent.Callable<EventACK> {
    EventBus eb;
    Object event;

    public SubTask(EventBus _eb, Object _e) {
        this.eb = _eb;
        this.event = _e;
    }

    @Override
    public EventACK call() throws Exception {
        System.out.println(
                "t=[" + Thread.currentThread() + "]"
              + " SubTask Callable<EventACK> triggering subsriber consumption *****"
        );
        eb.triggerSubscriberCallbacks(this.event);
        System.out.println(
                "t=[" + Thread.currentThread() + "]"
              + " SubTask Callable<EventACK> triggered subsriber consumption *****"
        );
        if (this.event instanceof EventACK) {
            System.out.println(
                    "t=[" + Thread.currentThread() + "]"
                  + " SubTask Callable<EventACK> calling event.acknowledgeEvent() *****"
            );
            EventACK eACK = ((EventACK) this.event).acknowledgeEvent();
            System.out.println(
                    "t=[" + Thread.currentThread() + "]"
                  + " SubTask Callable<EventACK> called event.acknowledgeEvent() *****"
            );
            return eACK;
        } else {
            return new EventACK<T>() {
                @Override
                public Class producedACK(EventBus theEB) {
                    return null;
                } //stub out ACK/NACK impl

                @Override
                public Class consumedACK(EventBus theEB) {
                    return null;
                } //stub out ACK/NACK impl

                @Override
                public EventACK acknowledgeEvent() {
                    return null;
                }

            };
        }

    }

}
