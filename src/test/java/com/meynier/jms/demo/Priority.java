package com.meynier.jms.demo;

import com.meynier.jms.demo.queue.Consumer;
import com.meynier.jms.demo.queue.Producer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.JMSException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * DOES NOT WORK !
 */
public class Priority {

    private static String QUEUE_NAME = "greetingPriorityQueue";
    private static Producer producer;

    private static Consumer consumer;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        producer = new Producer();
        producer.create("producer-priority-clientID-1", QUEUE_NAME);
        consumer = new Consumer();
        consumer.create("consumer-priority-clientID-1", QUEUE_NAME,true);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        producer.closeConnection();
        consumer.closeConnection();

    }

    @Test
    public void testGetGreetingWithPriority() {
        try {

            producer.sendNameWithPriority("Priority", "3", 3);
            producer.sendNameWithPriority("Priority", "8", 8);
            producer.sendNameWithPriority("Priority", "6", 6);
            producer.sendNameWithPriority("Priority", "4", 4);
            producer.sendNameWithPriority("Priority", "7", 7);
            producer.sendNameWithPriority("Priority", "2", 2);
            producer.sendNameWithPriority("Priority", "5", 5);
            producer.sendNameWithPriority("Priority", "1", 1);
            producer.sendNameWithPriority("Priority", "9", 9);

            String greeting = consumer.getGreeting(1000);
            assertEquals("Hello Priority 9!", greeting);
            greeting = consumer.getGreeting(1000);
            assertEquals("Hello Priority 8!", greeting);
            greeting = consumer.getGreeting(1000);
            assertEquals("Hello Priority 7!", greeting);
            greeting = consumer.getGreeting(1000);
            assertEquals("Hello Priority 6!", greeting);
            greeting = consumer.getGreeting(1000);
            assertEquals("Hello Priority 5!", greeting);
            greeting = consumer.getGreeting(1000);
            assertEquals("Hello Priority 4!", greeting);
            greeting = consumer.getGreeting(1000);
            assertEquals("Hello Priority 3!", greeting);
            consumer.getGreeting(1000);
            consumer.getGreeting(1000);
            consumer.getGreeting(1000);
            consumer.getGreeting(1000);
            consumer.getGreeting(1000);
            consumer.getGreeting(1000);
        } catch (JMSException e) {
            fail("a JMS Exception occurred");
        }
    }

    @Test
    public void testGetGreetingSimple() throws InterruptedException {
        try {

            //Lower
            producer.sendNameWithPriority("Priority", "1", 1);
            producer.sendNameWithPriority("Priority", "4", 4);
            producer.sendNameWithPriority("Priority", "4", 4);
            //Higher
            producer.sendNameWithPriority("Priority", "7", 7);
            producer.sendNameWithPriority("Priority", "8", 8);


            Thread.sleep(1000);
            String greeting = consumer.getGreeting(1000);
            assertEquals("Hello Priority 8!", greeting);
            greeting = consumer.getGreeting(1000);
            assertEquals("Hello Priority 7!", greeting);
            greeting = consumer.getGreeting(1000);
            assertEquals("Hello Priority 4!", greeting);
            greeting = consumer.getGreeting(1000);
            assertEquals("Hello Priority 4!", greeting);
            greeting = consumer.getGreeting(1000);
            assertEquals("Hello Priority 1!", greeting);


        } catch (JMSException e) {
            fail("a JMS Exception occurred");
        }
    }


}
