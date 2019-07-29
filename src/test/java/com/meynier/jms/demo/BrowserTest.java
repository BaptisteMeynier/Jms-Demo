package com.meynier.jms.demo;


import com.meynier.jms.demo.browser.Browser;
import com.meynier.jms.demo.queue.Producer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.JMSException;

import static org.junit.Assert.fail;

public class BrowserTest {

    private static String QUEUE_NAME = "browserQueue";
    private static Producer producer;

    private static Browser browser;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        producer = new Producer();
        producer.create("producer-clientID-3",QUEUE_NAME);
        browser = new Browser();
        browser.create("browser-clientID-3",QUEUE_NAME);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        producer.closeConnection();
        browser.closeConnection();

    }

    @Test
    public void testFetchNews() {
        try {
            for(int i =0; i<10;i++) {
                producer.sendName("Peregrin", "Took =>"+i);
            }
            String read = browser.read();
            System.out.println(read);

        } catch (JMSException e) {
            fail("a JMS Exception occurred");
        }
    }



}
