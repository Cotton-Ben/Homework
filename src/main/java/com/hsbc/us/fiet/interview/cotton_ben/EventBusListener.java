package com.hsbc.us.fiet.interview.cotton_ben;
/**
 *  ben.cotton@rutgers.edu
 *
 *  Inspired by javax.jms.MessageListener, this interface designed to accommodate
 *  subscribers's explicit call back processing join-points (with an HSBC interview
 *  exercise constraint) to core JDK 1.8 deployments (no JEE, JMS included).
 *
 *  NOTE: as a refactoring, academic exercise: this interface does *not* provide an 'Event' parameter in
 *  the method signature.
 */
import java.util.EventListener;

public interface EventBusListener extends EventListener {

    void onEventCallback();

}
