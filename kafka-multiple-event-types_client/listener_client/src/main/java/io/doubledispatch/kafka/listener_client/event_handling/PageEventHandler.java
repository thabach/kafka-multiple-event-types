package io.doubledispatch.kafka.listener_client.event_handling;

import org.apache.avro.specific.SpecificRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventHandler;

import io.doubledispatch.kafka.multiple_event_types.avro.PageView;
import io.doubledispatch.kafka.multiple_event_types.avro.Purchase;

public class PageEventHandler implements EventHandler<PageEventHolder> {

    private final static Logger logger = LoggerFactory.getLogger(PageEventHandler.class);

    @Override
    public void onEvent(PageEventHolder event, long sequence, boolean endOfBatch) throws Exception {
        SpecificRecord record = event.getPageEvent();
        if (record instanceof PageView) {
            logger.info("[Avro] Found a PageView event %s %n", record);
        } else if (record instanceof Purchase) {
            logger.info("[Avro] Found a Purchase event %s %n", record);
        } else {
            logger.info(String.format("Unrecognized type %s %n", record.getSchema().getFullName()));
        }
        Thread.sleep(30_000);
        event.getAcknowledgment().acknowledge();; 
    } 

}
