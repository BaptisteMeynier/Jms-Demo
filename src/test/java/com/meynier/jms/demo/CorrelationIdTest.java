package com.meynier.jms.demo;

import com.meynier.jms.demo.queue.Consumer;
import com.meynier.jms.demo.queue.Producer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.JMSException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CorrelationIdTest {
    private static Producer producer1,producer2;

    private static Consumer consumer1,consumer2;

    private static String correlationId= UUID.randomUUID().toString();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        producer1 = new Producer();
        producer1.create("producer-correlationId-clientID-1", "BookQueue");
        producer2 = new Producer();
        producer2.create("producer-correlationId-clientID-2", "AuthorQueue");
        consumer1 = new Consumer();
        consumer1.create("consumer-correlationId-clientID-1", "BookQueue", "JMSCorrelationID = '"+correlationId+"'");
        consumer2 = new Consumer();
        consumer2.create("consumer-correlationId-clientID-2", "AuthorQueue","JMSCorrelationID = '"+correlationId+"'");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        producer1.closeConnection();
        producer2.closeConnection();
        consumer1.closeConnection();
        consumer2.closeConnection();
    }

    @Test
    public void testFetchNews() {
        try {
            producer1.sendName("un", "autre");
            producer1.sendName("encore un", "autre");
            producer1.sendNameWithCorrelationId("Le", "Livre", correlationId);
            producer2.sendNameWithCorrelationId("Zola", "Emile", correlationId);

            String read = consumer1.getGreeting(500);
            System.out.println(read);
            assertEquals("Hello Le Livre!", read);
            read = consumer2.getGreeting(500);
            System.out.println(read);
            assertEquals("Hello Zola Emile!", read);
        } catch (JMSException e) {
            fail("a JMS Exception occurred");
        }
    }
}
