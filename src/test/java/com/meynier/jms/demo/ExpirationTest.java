package com.meynier.jms.demo;

import com.meynier.jms.demo.queue.Consumer;
import com.meynier.jms.demo.queue.Producer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.JMSException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ExpirationTest {

    private static String QUEUE_NAME="greetingExpirationQueue";

    private static Producer producer;

    private static Consumer consumer;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        producer = new Producer();
        producer.create("producer-expiration-clientID-1", QUEUE_NAME);
        consumer = new Consumer();
        consumer.create("consumer-expiration-clientID-1", QUEUE_NAME);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        producer.closeConnection();
        consumer.closeConnection();

    }

    @Test
    public void testGetGreetingInTime() {
        try {
            producer.sendNameWithExpiration("Robert", "JohnDo", 5000);

            String greeting = consumer.getGreeting(1000);
            assertEquals("Hello Robert JohnDo!", greeting);


        } catch (JMSException e) {
            fail("a JMS Exception occurred");
        }
    }

    @Test
    public void testGetGreeting() {
        try {
            producer.sendNameWithExpiration("Robert", "JohnDo", 1000);
            Thread.sleep(3000);
            String greeting = consumer.getGreeting(1000);
            assertEquals("no greeting", greeting);

        } catch (JMSException | InterruptedException e) {
            fail("a JMS Exception occurred");
        }
    }
}
