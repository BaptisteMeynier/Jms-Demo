package com.meynier.jms.demo;

import com.meynier.jms.demo.queue.Consumer;
import com.meynier.jms.demo.queue.Producer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GroupsTest {
    private static Producer producer;

    private static Consumer consumer1;
    private static Consumer consumer2;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        producer = new Producer();
        producer.create("producer-group-clientID-1", "greetingGroupQueue");
        consumer1 = new Consumer();
        consumer1.create("consumer-group-clientID-1", "greetingGroupQueue");
        consumer2 = new Consumer();
        consumer2.create("consumer-group-clientID-2", "greetingGroupQueue");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        producer.closeConnection();
        consumer1.closeConnection();
        consumer2.closeConnection();
    }

    @Test
    public void shouldConsumeMessageParallele() throws JMSException, InterruptedException {

        String consumer1Label = "consumer1";
        String consumer2Label = "consumer2";
        final Map<String, Integer> counterMap = new Hashtable<>(2);
        counterMap.put(consumer1Label, 0);
        counterMap.put(consumer2Label, 0);
        for (int i = 0; i < 5; i++) {
            producer.sendNameForAGroups("My", "Group", "MyGroup1");
        }


        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService executor2 = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(() -> requestQueue(consumer1, consumer1Label, counterMap), 0, 1, TimeUnit.SECONDS);
        executor2.scheduleAtFixedRate(() -> requestQueue(consumer2, consumer2Label, counterMap), 2, 1, TimeUnit.SECONDS);
        Thread.sleep(10000);
        executor.shutdown();
        executor2.shutdown();

        Assert.assertTrue(counterMap.get(consumer1Label)==5);
        Assert.assertTrue(counterMap.get(consumer2Label)==0);

    }

    private void requestQueue(Consumer consumer, String label, Map<String, Integer> counterMap) {
        try {
            TextMessage message = consumer.getGreetingTextMessage(500);
            if (Objects.nonNull(message.getText())) {
                synchronized (Object.class) {
                    Integer count = counterMap.get(label);
                    counterMap.put(label, ++count);
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
