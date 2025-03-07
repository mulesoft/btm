/*
 * Bitronix Transaction Manager
 *
 * Copyright (c) 2010, Bitronix Software.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA 02110-1301 USA
 */
package bitronix.tm.resource.messaging;

import bitronix.tm.resource.common.TransactionContextHelper;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageListener;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;

/**
 * {@link MessageConsumer} wrapper that adds XA enlistment semantics.
 *
 * @author lorban
 */
public class MessageConsumerWrapper implements MessageConsumer {

    private final MessageConsumer messageConsumer;
    protected final DualSessionWrapper session;
    private final PoolingConnectionFactory poolingConnectionFactory;

    public MessageConsumerWrapper(MessageConsumer messageConsumer, DualSessionWrapper session, PoolingConnectionFactory poolingConnectionFactory) {
        this.messageConsumer = messageConsumer;
        this.session = session;
        this.poolingConnectionFactory = poolingConnectionFactory;
    }

    public MessageConsumer getMessageConsumer() {
        return messageConsumer;
    }

    /**
     * Enlist this session into the current transaction if automaticEnlistingEnabled = true for this resource.
     * If no transaction is running then this method does nothing.
     * @throws JMSException
     */
    protected void enlistResource() throws JMSException {
        if (poolingConnectionFactory.getAutomaticEnlistingEnabled()) {
            session.getSession(); // make sure the session is created before enlisting it
            try {
                TransactionContextHelper.enlistInCurrentTransaction(session);
            } catch (SystemException ex) {
                throw (JMSException) new JMSException("error enlisting " + this).initCause(ex);
            } catch (RollbackException ex) {
                throw (JMSException) new JMSException("error enlisting " + this).initCause(ex);
            }
        } // if getAutomaticEnlistingEnabled
    }

    @Override
    public String toString() {
        return "a MessageConsumerWrapper of " + session;
    }

    /* MessageProducer with special XA semantics implementation */

    @Override
    public Message receive() throws JMSException {
        enlistResource();
        return getMessageConsumer().receive();
    }

    @Override
    public Message receive(long timeout) throws JMSException {
        enlistResource();
        return getMessageConsumer().receive(timeout);
    }

    @Override
    public Message receiveNoWait() throws JMSException {
        enlistResource();
        return getMessageConsumer().receiveNoWait();
    }

    @Override
    public void close() throws JMSException {
        getMessageConsumer().close();
    }

    /* dumb wrapping of MessageProducer methods */

    @Override
    public String getMessageSelector() throws JMSException {
        return getMessageConsumer().getMessageSelector();
    }

    @Override
    public MessageListener getMessageListener() throws JMSException {
        return getMessageConsumer().getMessageListener();
    }

    @Override
    public void setMessageListener(MessageListener listener) throws JMSException {
        getMessageConsumer().setMessageListener(listener);
    }

}
