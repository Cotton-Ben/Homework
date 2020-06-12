package com.hsbc.us.fiet.interview.cotton_ben;


import java.util.List;
import java.util.concurrent.BlockingQueue;

public interface EventBus {

    /* Feel free to replace Object with something more specific,
       but be prepared to justify it */

    void publishEvent(Object o);

    /* How would you denote the subscriber? */

    void addSubscriber(FilteredEventBlockingQueue o);

    /* Would you allow clients to filter the events they receive?
       How would the interface look like?  */

    void addSubscriberFiltered(Class<?> clazz, FilteredEventBlockingQueue<Object> o);

    /**
     *  added by Ben.Cotton@rutgers.edu
     * @param eventOn
     */

    void triggerSubscriberCallbacks(Object eventOn);


    BlockingQueue<?> getTheTopic();

    List<?> getTheSubsciberList();

}