package com.meynier.jms.demo;


import com.meynier.jms.demo.listener.ConsumerMessageListener;
import com.meynier.jms.demo.listener.MyExceptionListener;
import com.meynier.jms.demo.queue.Consumer;
import com.meynier.jms.demo.queue.Producer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.JMSException;

import static org.junit.Assert.fail;

public class ListenerTest {

    private static Producer producer;
    private static Consumer consumer;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        producer = new Producer();
        producer.create("producer-listener-clientID-1", "greetingListenerQueue");
        consumer = new Consumer();
        consumer.create("consumer-listener-clientID-1", "greetingListenerQueue", new ConsumerMessageListener("GreetingListener") ,new MyExceptionListener());
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        producer.closeConnection();
        consumer.closeConnection();

    }

    @Test
    public void testGetGreetingInTime() throws InterruptedException {
        try {
            producer.sendName("Robert", "JohnDo");
            producer.sendName("John-Son", "Hip");
            producer.sendName("Marouani", "Achref");
            producer.sendName("Meynier", "Baptiste1");
            producer.sendName("Meynier", "Baptiste2");
            producer.sendName("Meynier", "Baptiste3");
            producer.sendName("Meynier", "Baptiste4");
            producer.sendName("Meynier", "Baptiste5");
            Thread.sleep(5000);
        } catch (JMSException e) {
            fail("a JMS Exception occurred");
        }
    }


}
