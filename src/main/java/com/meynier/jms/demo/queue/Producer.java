package com.meynier.jms.demo.queue;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

import java.util.Map;

import static com.meynier.jms.demo.constants.JMSContants.JMS_BROKER_URL;


public class Producer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);

    private String clientId;
    private Connection connection;
    private Session session;
    private MessageProducer messageProducer;

    public void create(String clientId, String queueName) throws JMSException {
        this.clientId = clientId;

        // create a Connection Factory
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(JMS_BROKER_URL);

        // create a Connection
        connection = connectionFactory.createConnection();
        connection.setClientID(clientId);

        // create a Session
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // create the Queue to which messages will be sent
        Queue queue = session.createQueue(queueName);

        // create a MessageProducer for sending messages
        messageProducer = session.createProducer(queue);
    }

    public void closeConnection() throws JMSException {
        connection.close();
    }

    public void sendName(String firstName, String lastName) throws JMSException {
        String text = firstName + " " + lastName;

        // create a JMS TextMessage
        TextMessage textMessage = session.createTextMessage(text);
        // send the message to the topic destination
        messageProducer.send(textMessage);

        LOGGER.debug(clientId + ": sent message with text='{}'", text);
    }

    public void sendNameWithExpiration(String firstName, String lastName, long timeToLive) throws JMSException {
        String text = firstName + " " + lastName;

        // create a JMS TextMessage
        TextMessage textMessage = session.createTextMessage(text);
        textMessage.setJMSExpiration(timeToLive);
        // send the message to the topic destination
        messageProducer.send(textMessage,DeliveryMode.PERSISTENT,5,timeToLive);

        LOGGER.debug(clientId + ": sent message with text='{}'", text);
    }

    public void sendNameWithHeader(String firstName, String lastName, Map<String, Object> headers) throws JMSException {
        // create a JMS TextMessage
        String text = firstName + " " + lastName;
        TextMessage textMessage = session.createTextMessage(text);

        headers.forEach((key, value) -> {
            try {
                if (value instanceof Boolean) {
                    textMessage.setBooleanProperty(key, (Boolean) value);
                }
                if (value instanceof Integer) {
                    textMessage.setIntProperty(key, (Integer) value);
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });

        messageProducer.send(textMessage);

        LOGGER.debug(clientId + ": sent message with text='{}'", text);
    }


    public void sendNameWithCorrelationId(String firstName, String lastName, String correlationId) throws JMSException {
        String text = firstName + " " + lastName;

        // create a JMS TextMessage
        TextMessage textMessage = session.createTextMessage(text);
        textMessage.setJMSCorrelationID(correlationId);

        // send the message to the topic destination
        messageProducer.send(textMessage);

        LOGGER.debug(clientId + ": sent message with text='{}'", text);
    }

    public void sendNameWithPriority(String firstName, String lastName, int priority) throws JMSException {
        String text = firstName + " " + lastName;

        // create a JMS TextMessage
        TextMessage textMessage = session.createTextMessage(text);

        // send the message to the topic destination
        messageProducer.send(textMessage, DeliveryMode.PERSISTENT, priority, 0);

        LOGGER.debug(clientId + ": sent message with text='{}'", text);
    }

    public void sendNameForAGroups(String firstName, String lastName, String group) throws JMSException {
        String text = firstName + " " + lastName;

        // create a JMS TextMessage
        TextMessage textMessage = session.createTextMessage(text);
        textMessage.setStringProperty("JMSXGroupID", group);
        messageProducer.send(textMessage);

        LOGGER.debug(clientId + ": sent message with text='{}'", text);
    }

    public void sendNameForAGroupsAndClose(String firstName, String lastName, String group) throws JMSException {
        String text = firstName + " " + lastName;

        // create a JMS TextMessage
        TextMessage textMessage = session.createTextMessage(text);
        textMessage.setStringProperty("JMSXGroupID", group);
        textMessage.setIntProperty("JMSXGroupSeq", -1);
        messageProducer.send(textMessage);

        LOGGER.debug(clientId + ": sent message with text='{}'", text);
    }

}
