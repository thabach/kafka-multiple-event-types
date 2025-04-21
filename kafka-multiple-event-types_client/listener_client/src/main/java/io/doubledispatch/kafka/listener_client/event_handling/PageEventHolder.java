package io.doubledispatch.kafka.listener_client.event_handling;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.kafka.support.Acknowledgment;

public class PageEventHolder {
    
    private SpecificRecord _pageEvent;
    private Acknowledgment _acknowledgment;

    public PageEventHolder() {}

    public PageEventHolder(SpecificRecord pageEvent, Acknowledgment acknowledgment) {
        _pageEvent = pageEvent;
        _acknowledgment = acknowledgment;
    }

    public void copyFrom(PageEventHolder pageEventHolder) {
        _pageEvent = pageEventHolder.getPageEvent();
        _acknowledgment = pageEventHolder.getAcknowledgment();
    }

    public void setPageEvent(SpecificRecord pageEvent) {
        _pageEvent = pageEvent;
    }

    public void setAcknowldegment(Acknowledgment acknowledgment) {
        _acknowledgment = acknowledgment;
    }

    public Acknowledgment getAcknowledgment() {
        return _acknowledgment;
    }

    public SpecificRecord getPageEvent() {
        return _pageEvent;
    }

    public void reinitialize() {
        _pageEvent = null;
        _acknowledgment = null;
    }

}
