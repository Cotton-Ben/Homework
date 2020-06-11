package com.hsbc.us.fiet.interview.cotton_ben;

/**
 *  ben.cotton@rutgers.edu
 *
 *  EventACK is an interface that both PRODUCED/CONSUMED standard 'ACK' notifications,
 *
 *  AND
 *
 *  provides a costume ackowledgeEvent() method for subscribers
 *  (like 'AlgoComplexEventProcessor'}
 *  that wish to reflect an interest in specialized
 *  processing upon receipt of a specific event
 */

public interface EventACK<T> {

    Class<T> producedACK(EventBus theEB);

    Class<T> consumedACK(EventBus theEB);

    EventACK acknowledgeEvent();
}
