package io.doubledispatch.kafka.listener_client.config;

import java.util.concurrent.ThreadFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import io.doubledispatch.kafka.listener_client.event_handling.PageEventHandler;
import io.doubledispatch.kafka.listener_client.event_handling.PageEventHolder;

@Configuration
public class DisruptorConfig {
    
    private static int RINGBUFFER_SIZE = 1 << 2; // 4
    private static WaitStrategy WAIT_STRATEGY = new BlockingWaitStrategy();


    @Bean
    RingBuffer<PageEventHolder> ringbuffer() {
        final ThreadFactory threadFactory = DaemonThreadFactory.INSTANCE;
        Disruptor<PageEventHolder> disruptor = new Disruptor<>(PageEventHolder::new, RINGBUFFER_SIZE, threadFactory, ProducerType.SINGLE, WAIT_STRATEGY);
        disruptor.handleEventsWith(new PageEventHandler());
        return disruptor.start();
    }

}
