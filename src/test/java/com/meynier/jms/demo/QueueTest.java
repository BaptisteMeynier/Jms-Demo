package com.meynier.jms.demo;

import com.meynier.jms.demo.queue.Consumer;
import com.meynier.jms.demo.queue.Producer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.JMSException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class QueueTest {

    private static final String QUEUE_NAME ="greetingQueue";

    private static Producer producer;

    private static Consumer consumer1,consumer2,consumer3;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        producer = new Producer();
        producer.create("producer-clientID-1",QUEUE_NAME);
        consumer1 = new Consumer();
        consumer1.create("consumer-clientID-1",QUEUE_NAME);
        consumer2 = new Consumer();
        consumer2.create("consumer-clientID-2",QUEUE_NAME);

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        producer.closeConnection();
        consumer1.closeConnection();
        consumer2.closeConnection();
    }

    @Test
    public void testGetGreeting() {
        try {
            producer.sendName("Peregrin", "Took");

            String greeting1 = consumer1.getGreeting(1000);
            assertEquals("Hello Peregrin Took!", greeting1);

            String greeting2 = consumer1.getGreeting(1000);
            assertEquals("no greeting", greeting2);

        } catch (JMSException e) {
            fail("a JMS Exception occurred");
        }
    }

    @Test
    public void testGetMultipleGreeting() {
        try {
            for(int i =0; i<10;i++) {
                producer.sendName("Peregrin", "Took =>"+i);
            }
            for(int i =0; i<10;i++) {
                consumer1.getGreeting(1000);
            }

        } catch (JMSException e) {
            fail("a JMS Exception occurred");
        }
    }

    @Test
    public void testSimultaneusGreeting() {

        try {
            for(int i =0; i<20;i++) {
                producer.sendName("Peregrin", "Took =>"+i);
            }

            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            ScheduledExecutorService executor2 = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> requestQueue(consumer1), 1, 1, TimeUnit.SECONDS);
            executor2.scheduleAtFixedRate(() -> requestQueue(consumer2), 1, 1, TimeUnit.SECONDS);

            Thread.sleep(15000);
            executor.shutdown();
            executor2.shutdown();
        } catch (JMSException e) {
            fail("a JMS Exception occurred");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSimultaneusGreetingWithSameClientId() throws JMSException {
        consumer3 = new Consumer();
        consumer3.create("consumer-clientID-1",QUEUE_NAME);
        try {
            for(int i =0; i<20;i++) {
                producer.sendName("Peregrin Simultaneus", "Took =>"+i);
            }

            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            ScheduledExecutorService executor2 = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> requestQueue(consumer1), 1, 1, TimeUnit.SECONDS);
            executor2.scheduleAtFixedRate(() -> requestQueue(consumer3), 1, 1, TimeUnit.SECONDS);

            Thread.sleep(15000);
        } catch (JMSException e) {
            fail("a JMS Exception occurred");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            consumer3.closeConnection();
        }
    }

    @Test
    public void testSimultaneusProducerGreetingWithSameProducer() {

        try {
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            ScheduledExecutorService executor2 = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> produceQueue(producer), 1, 1, TimeUnit.SECONDS);
            executor2.scheduleAtFixedRate(() -> produceQueue(producer), 1, 1, TimeUnit.SECONDS);

            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testSimultaneusProducerGreetingWithSameClientId() throws JMSException {
        Producer  producer2 = new Producer();
        try {
            producer2.create("producer-clientID-1",QUEUE_NAME);
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            ScheduledExecutorService executor2 = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> produceQueue(producer), 1, 1, TimeUnit.SECONDS);
            executor2.scheduleAtFixedRate(() -> produceQueue(producer2), 1, 1, TimeUnit.SECONDS);

            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JMSException e) {
            e.printStackTrace();
        }finally {
            producer2.closeConnection();
        }
    }

    private void produceQueue(Producer producer) {
        try {
            producer.sendName("Peregrin Simultaneus", "Took");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void requestQueue(Consumer consumer) {
        try {
            String greeting = consumer.getGreeting(250);
            System.out.println(Thread.currentThread().getName() + "; message:" + greeting);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
