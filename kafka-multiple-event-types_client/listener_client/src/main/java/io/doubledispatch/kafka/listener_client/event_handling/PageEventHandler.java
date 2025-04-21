package io.doubledispatch.kafka.listener_client.event_handling;

import org.apache.avro.specific.SpecificRecord;

import com.lmax.disruptor.EventHandler;

import io.doubledispatch.kafka.multiple_event_types.avro.PageView;
import io.doubledispatch.kafka.multiple_event_types.avro.Purchase;

public class PageEventHandler implements EventHandler<PageEventHolder> {

    @Override
    public void onEvent(PageEventHolder event, long sequence, boolean endOfBatch) throws Exception {
        SpecificRecord record = event.getPageEvent();
        if (record instanceof PageView) {
            System.out.printf("[Avro] Found a PageView event %s %n", record);
        } else if (record instanceof Purchase) {
            System.out.printf("[Avro] Found a Purchase event %s %n", record);
        } else {
            System.out.printf(String.format("Unrecognized type %s %n", record.getSchema().getFullName()));
        }
        Thread.sleep(30_000);
        event.getAcknowledgment().acknowledge();; 
    } 

}
