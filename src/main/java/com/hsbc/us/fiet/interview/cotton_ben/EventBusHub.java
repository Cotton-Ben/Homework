package com.hsbc.us.fiet.interview.cotton_ben;

/**
 * ben.cotton@rutgers,edu
 *
 * An EventBus impl that contains an event bridge (thread-safe) reference'theTopic'
 * via which both event Objects can be published on to, and  event objects can be consumed off of.
 *
 * An immutable (i.e. thread-safe)  list reference 'theSubsL' represents
 * the  M subscribers in a 1-M pub/sub architecture.
 *
 *
 */

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBusHub implements EventBus {

    public final List<FilteredEventBlockingQueue<Object>> theSubsL = new CopyOnWriteArrayList<>();
    public final FilteredEventBlockingQueue<Object> theTopic =
            new FilteredEventBlockingQueue<>(10);

    /**
     * method to implement the production of an event Object on to the EventBus 'theTopic' reference.
     *
     * @param eventOn
     */
    @Override
    public void publishEvent(Object eventOn) {
        final Class clazz = eventOn.getClass();
        System.out.println(
                "t=[" + Thread.currentThread() + "]"
              + "EventBusHub published teventID=["+System.identityHashCode(eventOn)
              + "] event=[" + eventOn + "]"
        );

        this.getTheTopic().offer(eventOn);  //produce

        if (eventOn instanceof  EventACK) {
          Class<?> c =  ((EventACK) eventOn).producedACK(this);
        }
    }

    @Override
    public void triggerSubscriberCallbacks(Object eventOn) {
        // write the consumed  event on to *each* subscriber
        for (FilteredEventBlockingQueue<Object> q : theSubsL) {
            q.setEventObject(eventOn);
            if (!q.offer(eventOn)) {
                System.out.print(
                        "t=[" + Thread.currentThread() + "]"
                       +"EventBusHub triggering Subscriber callbacks on event failed."
                );
            }
        }
        // now process the consumed event off of *each* subscribers onEventCallback() execution
        for (FilteredEventBlockingQueue<Object> consumingSub : theSubsL) {
            final Object eventOff = consumingSub.poll();
            final Class eventClazz = eventOff.getClass();
            final Class filterClazz = consumingSub.getEventPattern();
            if (filterClazz == null ||  filterClazz == eventClazz) {
                System.out.println(
                        "t=[" + Thread.currentThread() + "]"
                                + "EventBusHub subscriberL.poll() CONSUMED "
                                + "eventID=[" +System.identityHashCode(eventOff)+ "]"
                                + "subscriberL=[" + System.identityHashCode(consumingSub) + "] "
                                + "event=[" + eventOff.toString() + "]"
                );

                ((EventBusListener) consumingSub).onEventCallback();

                if (eventOff instanceof  EventACK) {
                    Class<?> c = ((EventACK) eventOff).consumedACK(this);
                }
            } else {
                System.out.println(
                        "t=[" + Thread.currentThread() + "]"
                                + "EventBusHub ignoring subscriberL.onEvent() callback: "
                                + "subscriber=[" + System.identityHashCode(consumingSub) + "] "
                                + "NOT INTERESTED "
                                + "(seeking ["+consumingSub.getEventPattern()+"] "
                                + "but sees ["+eventClazz+"] ) "
                                + "event=[" + eventOff.toString() + "]"
                );
            }
        }
    }

    @Override
    public void addSubscriber(FilteredEventBlockingQueue _subscriber) {
        _subscriber.setEventPattern(null);
        theSubsL.add(_subscriber);
    }

    @Override
    public void addSubscriberFiltered(Class<?> clazz, FilteredEventBlockingQueue<Object> _sub) {
        _sub.setEventPattern(clazz);
        theSubsL.add(_sub);
    }

    @Override
    public FilteredEventBlockingQueue<Object> getTheTopic() {

        return theTopic;
    }

    @Override
    public List<?> getTheSubsciberList() {

        return theSubsL;
    }
}
