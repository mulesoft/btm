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
package bitronix.tm.resource.messaging.lrc;


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
import jakarta.jms.XASession;

/**
 * XAConnection implementation for a non-XA JMS resource emulating XA with Last Resource Commit.
 *
 * @author lorban
 */
public class LrcXAConnection implements XAConnection {

    private final Connection nonXaConnection;

    public LrcXAConnection(Connection connection) {
        this.nonXaConnection = connection;
    }

    @Override
    public XASession createXASession() throws JMSException {
        return new LrcXASession(nonXaConnection.createSession(true, Session.AUTO_ACKNOWLEDGE));
    }

    @Override
    public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {
        throw new JMSException(LrcXAConnection.class.getName() + " can only respond to createXASession()");
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
    public ConnectionConsumer createConnectionConsumer(Destination destination, String messageSelector, ServerSessionPool serverSessionPool, int maxMessages) throws JMSException {
        return nonXaConnection.createConnectionConsumer(destination, messageSelector, serverSessionPool, maxMessages);
    }

    @Override
    public ConnectionConsumer createSharedConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException
    {
        throw new RuntimeException("Method not supported");
    }

    @Override
    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool serverSessionPool, int maxMessages) throws JMSException {
        return nonXaConnection.createDurableConnectionConsumer(topic, subscriptionName, messageSelector, serverSessionPool, maxMessages);
    }

    @Override
    public ConnectionConsumer createSharedDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException
    {
        throw new RuntimeException("Method not supported");
    }

    @Override
    public String getClientID() throws JMSException {
        return nonXaConnection.getClientID();
    }

    @Override
    public void setClientID(String clientID) throws JMSException {
        nonXaConnection.setClientID(clientID);
    }

    @Override
    public ConnectionMetaData getMetaData() throws JMSException {
        return nonXaConnection.getMetaData();
    }

    @Override
    public ExceptionListener getExceptionListener() throws JMSException {
        return nonXaConnection.getExceptionListener();
    }

    @Override
    public void setExceptionListener(ExceptionListener exceptionListener) throws JMSException {
        nonXaConnection.setExceptionListener(exceptionListener);
    }

    @Override
    public void start() throws JMSException {
        nonXaConnection.start();
    }

    @Override
    public void stop() throws JMSException {
        nonXaConnection.stop();
    }

    @Override
    public void close() throws JMSException {
        nonXaConnection.close();
    }

    @Override
    public String toString() {
        return "a JMS LrcXAConnection on " + nonXaConnection;
    }
}
