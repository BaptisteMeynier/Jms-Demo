package com.meynier.jms.demo.listener;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

public class MyExceptionListener implements ExceptionListener {
    @Override
    public void onException(JMSException exception) {
        System.out.println("ExceptionListener called with error: " + exception.getMessage());
    }
}
