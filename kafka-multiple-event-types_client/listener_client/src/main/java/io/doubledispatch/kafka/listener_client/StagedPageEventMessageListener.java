package io.doubledispatch.kafka.listener_client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.MessageListenerContainer;

import com.lmax.disruptor.RingBuffer;

import io.doubledispatch.kafka.listener_client.event_handling.PageEventHolder;

public final class StagedPageEventMessageListener {
    
    private final static Logger logger = LoggerFactory.getLogger(StagedPageEventMessageListener.class);

    private final int _stageThreshold;
    private final LinkedBlockingQueue<PageEventHolder> _eventStage = new LinkedBlockingQueue<>();

    private final MessageListenerContainer _listenerContainer;
    private final RingBuffer<PageEventHolder> _ringbuffer;

    private volatile boolean _stopped = false;

    public StagedPageEventMessageListener(MessageListenerContainer listenerContainer, RingBuffer<PageEventHolder> ringbuffer, int stageThreshold) {
        _listenerContainer = listenerContainer;
        _ringbuffer = ringbuffer;
        _stageThreshold = stageThreshold;
        start();
    }

    public void consume(PageEventHolder pageEventHolder) {
        _eventStage.add(pageEventHolder);
    }

    private void start() {
        CompletableFuture.runAsync(() -> {
            try {
                while(!_stopped) {
                    PageEventHolder eventHolder = _eventStage.take();
                    if (_ringbuffer.hasAvailableCapacity(1) && (_eventStage.size() <= _stageThreshold)) {
                        // will not block on publish as of capacity available
                        _ringbuffer.publishEvent((event, sequence) -> {
                            event.copyFrom(eventHolder);
                        });
                    } else {
                        logger.info("pausing Kafka ListenerContainer");
                        _listenerContainer.pause();
                        // might block on publish, which is intended
                        _ringbuffer.publishEvent((event, sequence) -> {
                            event.copyFrom(eventHolder);
                        });
                        while (!_eventStage.isEmpty()) {
                            logger.info("emptying stage");
                            PageEventHolder emptyQueueEventHolder = _eventStage.poll();
                            // might block on publish, which is intended
                            _ringbuffer.publishEvent((event, sequence) -> {
                                event.copyFrom(emptyQueueEventHolder);
                            });
                        }
                        logger.info("resuming Kafka ListenerContainer");
                        _listenerContainer.resume();
                    }
                }
            } catch (InterruptedException ex) {
                _stopped = true;
            }
        });
    }

    public void stop() {
        _stopped = true;
    }

}
