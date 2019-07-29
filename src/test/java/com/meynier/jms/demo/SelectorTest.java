package com.meynier.jms.demo;

import com.meynier.jms.demo.queue.Consumer;
import com.meynier.jms.demo.queue.Producer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.JMSException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SelectorTest {

    private static String QUEUE_NAME= "greetingSelectorQueue";
    private static Producer producer;

    private static Consumer consumer;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        producer = new Producer();
        producer.create("producer-selector-clientID-1",QUEUE_NAME);
        consumer = new Consumer();
        consumer.create("consumer-selector-clientID-1",QUEUE_NAME,"IS_PDG = TRUE");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        producer.closeConnection();
        consumer.closeConnection();

    }

    @Test
    public void testGetGreeting() {
        try {
            Map<String, Object> header = new HashMap<>();
            header.put("IS_PDG",Boolean.TRUE);
            producer.sendName("Robert", "JohnDo");
            producer.sendName("Marouani", "Achref");
            producer.sendName("Meynier", "Baptiste");
            producer.sendNameWithHeader("Hippolyte", "John-son",header);
            producer.sendName("Chesnoy", "Bernard");

            String greeting1 = consumer.getGreeting(1000);
            assertEquals("Hello Hippolyte John-son!", greeting1);

            String greeting2 = consumer.getGreeting(1000);
            assertEquals("no greeting", greeting2);

        } catch (JMSException e) {
            fail("a JMS Exception occurred");
        }
    }
}
