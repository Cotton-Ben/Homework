package com.hsbc.us.fiet.interview.cotton_ben;

import java.util.Stack;

/**
 * A place-holder class at which to implement the HSBC 'extra-credit' exercise:
 * <p>
 * ***  "Extra points if you can extend the multithreaded
 * ***  version (maybe by extending the interface) so it supports
 * ***  event types where only the latest value matters (coalescing / conflation)
 * ***  – i.e. multiple market data snapshots arriving while the algo is processing an update.
 * <p>
 * NEEDED to earn XtraCredit: A test that highlights this impls 'only the latest' capabilities,
 * possibly via a Px tick being updated during an isolation sensitive 'calc'
 */

public class AlgoComplexEventProcessor implements EventACK {

    String filterKeyToken;
    long mostRecentTick;
    double px;
    Stack<Double> pxStack;

    public AlgoComplexEventProcessor(String _filterKeyToken) {
        this.px = 102.65;
        this.pxStack = new Stack<>();
        this.filterKeyToken = _filterKeyToken;
        this.mostRecentTick = System.currentTimeMillis();
    }

    @Override
    public EventACK acknowledgeEvent() {
        this.px = pxStack.empty() ? this.px : pxStack.pop();  //commit to the most recent px
        double stackedPx = this.px;
        do {
            System.out.println(
                    "t=[" + Thread.currentThread() + "] " +
                    "AlgoComplexEventProcessor." +
                    "acknowledgeEvent() of stackedPx=[" + stackedPx + "] consumption.");
        } while (!pxStack.empty() && (stackedPx = pxStack.pop()) > 0);
        if (this.px > 0.00) {
            System.out.println(
                    "t=[" + Thread.currentThread() + "] " +
                    "AlgoComplexEventProcessor instance=[" + this.toString() +"] "  +
                    "acknowledgeEvent() of Tick Px=[" + this.px + "] consumption.");
        } else {
            System.out.println(
                    "t=[" + Thread.currentThread() + "] " +
                    "AlgoComplexEventProcessor instance=[" + this.toString() +"] "  +
                    "acknowledgeEvent() of a algo has FINISHED consumption!");
        }
        return this;
    }

    public AlgoComplexEventProcessor tick(final double _px) throws InterruptedException {
        this.mostRecentTick = System.currentTimeMillis();
        this.filterKeyToken = "ORIGINAL_PX_TICK("+((_px >= 0) ? _px : this.px) + ")";

        if (_px >= 0.00) {
            pxStack.push(_px);
            System.out.println(
                    "t=[" + Thread.currentThread() + "] " +
                    "AlgoComplexEventProcessor.tick("+_px+") sees PUBLISHED Tick Px=[" + _px + "] "
            );
        } else {
            System.out.println(
                    "t=[" + Thread.currentThread() + "] " +
                    "AlgoComplexEventProcessor.tick("+_px+") winding down publishing Px for an algo.");
        }
        return (this);
    }

    @Override
    public String toString() {
        return "AlgoComplexEventProcessor{" +
                "filterKeyToken='" + filterKeyToken + '\'' +
                ", mostRecentTick=" + mostRecentTick +
                ", px=" + px +
                ", pxStack=" + pxStack +
                '}';
    }

    @Override
    public Class<?> producedACK(EventBus _pub) {
        return this.getClass();//stub out ACK/NACK impl
    }

    @Override
    public Class<?> consumedACK(EventBus _sub) {
        return this.getClass(); //stub out ACK/NACK impl
    }

}
