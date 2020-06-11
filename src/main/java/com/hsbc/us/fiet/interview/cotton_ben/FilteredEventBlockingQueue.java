package com.hsbc.us.fiet.interview.cotton_ben;

/**
 * Ben.Cotton@rutgers.edu
 *
 * This coding sample uses inheritance, often using composition is preferred.
 *
 *  A FEBQ is an extension of ArrayBlockingQueue that provides an impl of EventBusListener to
 *  accommodate the onEventCallback() interests of EventBus subscribers.
 *
 *  An 'eventPattern' field is provided to facilitate the filtering of subscribers that only want to listen
 *  for the publication of events from a certain Class of event producers.
 *
 * @param <T>
 */

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;

public class FilteredEventBlockingQueue<T> extends ArrayBlockingQueue implements EventBusListener {
    Class<?> eventPattern;
    Object eventObject;

    public FilteredEventBlockingQueue(int capacity) {
        super(capacity);
    }

    public Object getEventObject() {
        return eventObject;
    }

    public void setEventObject(Object eventObject) {
        this.eventObject = eventObject;
    }

    public Class<?> getEventPattern() {
        return eventPattern;
    }

    public void setEventPattern(Class<?> eventPattern) {
        this.eventPattern = eventPattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilteredEventBlockingQueue<?> that = (FilteredEventBlockingQueue<?>) o;
        return Objects.equals(eventPattern, that.eventPattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventPattern);
    }

    @Override
    public void onEventCallback() {
        System.out.println("t=[" + Thread.currentThread() + "] "
                + "subscriber.onEvent() CALLBACK: "
                + "subscriber=[" + System.identityHashCode(this) + "] "
                + "event=[" + this.getEventObject().toString() + "]"
                );
    }
}
