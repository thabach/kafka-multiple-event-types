package io.doubledispatch.kafka.listener_client;

import org.apache.avro.specific.SpecificRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.lmax.disruptor.RingBuffer;

import io.doubledispatch.kafka.listener_client.event_handling.PageEventHolder;

@Component
public class PageEventMessageListener {

    private final static Logger logger = LoggerFactory.getLogger(PageEventMessageListener.class);

    private final RingBuffer<PageEventHolder> _ringbuffer;
    private final KafkaListenerEndpointRegistry _registry;
    private final String _topicName;

    private StagedPageEventMessageListener _stagedPageEventHandler;
    private final static int STAGE_THRESHOLD = 4;

    public PageEventMessageListener(RingBuffer<PageEventHolder> ringbuffer, KafkaListenerEndpointRegistry registry, @Value("${topic.avro}") String topicName) {
        _ringbuffer = ringbuffer;
        _registry = registry;
        _topicName = topicName;
    }
    
    @KafkaListener(id = "${topic.avro}", topics = "${topic.avro}")
    public void consume(@Payload SpecificRecord message, Acknowledgment acknowledgement) {
        if (_stagedPageEventHandler == null) {
            _stagedPageEventHandler = new StagedPageEventMessageListener(_registry.getListenerContainer(_topicName), _ringbuffer, STAGE_THRESHOLD);
        }
        _stagedPageEventHandler.consume(new PageEventHolder(message, acknowledgement));
    }

}
