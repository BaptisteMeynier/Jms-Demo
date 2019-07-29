package com.meynier.jms.demo.browser;

import org.apache.activemq.ActiveMQConnectionFactory;


import javax.jms.*;

import java.util.Enumeration;

import static com.meynier.jms.demo.constants.JMSContants.JMS_BROKER_URL;


public class Browser {

    private Connection connection;
    private QueueBrowser browser;

    public void create(String clientId, String queueName) throws JMSException {

        // create a Connection Factory
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(JMS_BROKER_URL);

        // create a Connection
        connection = connectionFactory.createConnection();
        connection.setClientID(clientId);

        // create a Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // create the Queue from which messages will be received
        Queue queue = session.createQueue(queueName);

        // create a MessageConsumer for receiving messages
        browser = session.createBrowser(queue);

        // start the connection in order to receive messages
        connection.start();
    }

    public void closeConnection() throws JMSException {
        connection.close();
    }

    public String read() throws JMSException {
        StringBuilder res= new StringBuilder();
        Enumeration enumeration = browser.getEnumeration();
        while (enumeration.hasMoreElements()) {
            TextMessage message = (TextMessage) enumeration.nextElement();
            res.append("Browse [").append(message.getText()).append("]");
        }
        System.out.println("Done");
        return res.toString();
    }
}
