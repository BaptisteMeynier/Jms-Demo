package com.meynier.jms.demo.queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

import java.util.Objects;

import static com.meynier.jms.demo.constants.JMSContants.JMS_BROKER_URL;


public class Consumer {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(Consumer.class);

    private static final String NO_GREETING = "no greeting";
    private static final String PRIORITY_OPTION = "?jms.messagePrioritySupported=true";

    private String clientId;
    private Connection connection;
    private MessageConsumer messageConsumer;

    public void create(String clientId, String queueName) throws JMSException {
        this.init(clientId, queueName, null, null, null,false);
    }

    public void create(String clientId, String queueName,boolean priorityEnabled) throws JMSException {
        this.init(clientId, queueName, null, null, null,priorityEnabled);
    }

    public void create(String clientId, String queueName, String selector) throws JMSException {
        this.init(clientId, queueName, selector, null, null,false);
    }

    public void create(String clientId, String queueName, MessageListener messageListener) throws JMSException {
        this.init(clientId, queueName, null, messageListener, null,false);
    }

    public void create(String clientId, String queueName, MessageListener messageListener, ExceptionListener exceptionListener) throws JMSException {
        this.init(clientId, queueName, null, messageListener, exceptionListener,false);
    }


    private void init(String clientId, String queueName, String selector, MessageListener messageListener, ExceptionListener exceptionListener, boolean priorityEnabled) throws JMSException {
        this.clientId = clientId;

        // create a Connection Factory
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(JMS_BROKER_URL + (priorityEnabled ?  PRIORITY_OPTION : ""));

        // create a Connection
        connection = connectionFactory.createConnection();
        connection.setClientID(clientId);

        // create a Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // create the Topic from which messages will be received
        Queue queue = session.createQueue(queueName);

        // create a MessageConsumer for receiving messages
        messageConsumer = Objects.nonNull(selector) ? session.createConsumer(queue, selector) : session.createConsumer(queue);

        if (Objects.nonNull(messageListener)) {
            messageConsumer.setMessageListener(messageListener);
        }

        if (Objects.nonNull(exceptionListener)) {
            connection.setExceptionListener(exceptionListener);
        }

        // start the connection in order to receive messages
        connection.start();
    }

    public void closeConnection() throws JMSException {
        connection.close();
    }

    public String getGreeting(int timeout) throws JMSException {

        String greeting = NO_GREETING;

        // read a message from the topic destination
        Message message = messageConsumer.receive(timeout);

        // check if a message was received
        if (message != null) {
            // cast the message to the correct type
            TextMessage textMessage = (TextMessage) message;

            // retrieve the message content
            String text = textMessage.getText();
            LOGGER.debug(clientId + ": received message with text='{}'", text);

            // create greeting
            greeting = "Hello " + text + "!";
        } else {
            LOGGER.debug(clientId + ": no message received");
        }

        LOGGER.info("greeting={}", greeting);
        return greeting;
    }


    public TextMessage getGreetingTextMessage(int timeout) throws JMSException {

        // read a message from the topic destination
        Message message = messageConsumer.receive(timeout);

        TextMessage textMessage = null;
        // check if a message was received
        if (message != null) {
            // cast the message to the correct type
            textMessage = (TextMessage) message;

        }
        return textMessage;
    }

}
