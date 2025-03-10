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

import jakarta.jms.Connection;
import jakarta.jms.ConnectionConsumer;
import jakarta.jms.ConnectionMetaData;
import jakarta.jms.Destination;
import jakarta.jms.ExceptionListener;
import jakarta.jms.JMSException;
import jakarta.jms.ServerSessionPool;
import jakarta.jms.Session;
import jakarta.jms.Topic;
import jakarta.jms.XAConnection;

/**
 * Disposable Connection handle.
 *
 * @author lorban
 */
public class MessagingConnectionHandle implements Connection {

    private final XAConnection xaConnection;
    private final MessagingPooledConnection pooledConnection;
    private volatile boolean closed = false;

    public MessagingConnectionHandle(MessagingPooledConnection pooledConnection, XAConnection xaConnection) {
        this.pooledConnection = pooledConnection;
        this.xaConnection = xaConnection;
    }

    public XAConnection getXAConnection() throws JMSException {
        if (xaConnection == null)
            throw new JMSException("XA connection handle has been closed");
        return xaConnection;
    }

    public MessagingPooledConnection getPooledConnection() {
        return pooledConnection;
    }

    public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {
        return pooledConnection.createSession(transacted, acknowledgeMode);
    }

    @Override
    public Session createSession(int sessionMode) throws JMSException
    {
        throw new RuntimeException("Method not supported");
    }

    @Override
    public Session createSession() throws JMSException
    {
        throw new RuntimeException("Method not supported");
    }

    @Override
    public void close() throws JMSException {
        if (closed)
            return;

        closed = true;
        pooledConnection.release();
    }

    public String toString() {
        return "a JmsConnectionHandle of " + pooledConnection;
    }


    /* Connection implementation */

    public String getClientID() throws JMSException {
        return getXAConnection().getClientID();
    }

    public void setClientID(String jndiName) throws JMSException {
        getXAConnection().setClientID(jndiName);
    }

    public ConnectionMetaData getMetaData() throws JMSException {
        return getXAConnection().getMetaData();
    }

    public ExceptionListener getExceptionListener() throws JMSException {
        return getXAConnection().getExceptionListener();
    }

    public void setExceptionListener(ExceptionListener listener) throws JMSException {
        getXAConnection().setExceptionListener(listener);
    }

    public void start() throws JMSException {
        getXAConnection().start();
    }

    public void stop() throws JMSException {
        getXAConnection().stop();
    }

    public ConnectionConsumer createConnectionConsumer(Destination destination, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        return getXAConnection().createConnectionConsumer(destination, messageSelector, sessionPool, maxMessages);
    }

    @Override
    public ConnectionConsumer createSharedConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException
    {
        throw new RuntimeException("Method not supported");
    }

    @Override
    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        return getXAConnection().createDurableConnectionConsumer(topic, subscriptionName, messageSelector, sessionPool, maxMessages);
    }

    @Override
    public ConnectionConsumer createSharedDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException
    {
        throw new RuntimeException("Method not supported");
    }

}
