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
package bitronix.tm.mock.resource.messaging;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import bitronix.tm.mock.resource.MockXAResource;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.Topic;
import jakarta.jms.XAConnection;
import jakarta.jms.XAConnectionFactory;
import jakarta.jms.XAJMSContext;
import jakarta.jms.XASession;

/**
 *
 * @author lorban
 */
public class MockXAConnectionFactory implements XAConnectionFactory {

    private static JMSException staticCloseXAConnectionException;
    private static JMSException staticCreateXAConnectionException;
    private static int connectionsToFail =0;
    private static List<XAConnection> connections = new ArrayList<>();

    public XAConnection createXAConnection() throws JMSException {
        if (staticCreateXAConnectionException != null)
            throw staticCreateXAConnectionException;

        if (connectionsToFail > 0 && connectionsToFail == connections.size()) {
                throw new JMSException("Max number of connections reached");
        }

    	Answer xaSessionAnswer = new Answer<XASession>() {
    		public XASession answer(InvocationOnMock invocation)throws Throwable {
    			XASession mockXASession = mock(XASession.class);
    			MessageProducer messageProducer = mock(MessageProducer.class);
    	    	when(mockXASession.createProducer((Destination) any())).thenReturn(messageProducer);
    	    	MessageConsumer messageConsumer = mock(MessageConsumer.class);
    	    	when(mockXASession.createConsumer((Destination) any())).thenReturn(messageConsumer);
    	    	when(mockXASession.createConsumer((Destination) any(), anyString())).thenReturn(messageConsumer);
    	    	when(mockXASession.createConsumer((Destination) any(), anyString(), anyBoolean())).thenReturn(messageConsumer);
    	    	Queue queue = mock(Queue.class);
    	    	when(mockXASession.createQueue(anyString())).thenReturn(queue);
    	    	Topic topic = mock(Topic.class);
    	    	when(mockXASession.createTopic(anyString())).thenReturn(topic);
    	    	MockXAResource mockXAResource = new MockXAResource(null);
    			when(mockXASession.getXAResource()).thenReturn(mockXAResource);    			
    	    	Answer<Session> sessionAnswer = new Answer<Session>() {
    				public Session answer(InvocationOnMock invocation) throws Throwable {
    					Session session = mock(Session.class);
    					MessageProducer producer = mock(MessageProducer.class);
    					when(session.createProducer((Destination) any())).thenReturn(producer);
    					return session;
    				}
    	    	};
    			when(mockXASession.getSession()).thenAnswer(sessionAnswer);
    			
    			return mockXASession;
    		}
    	};

    	XAConnection mockXAConnection = mock(XAConnection.class);
    	when(mockXAConnection.createXASession()).thenAnswer(xaSessionAnswer);
    	when(mockXAConnection.createSession(anyBoolean(), anyInt())).thenAnswer(xaSessionAnswer);
        if (staticCloseXAConnectionException != null)
            doThrow(staticCloseXAConnectionException).when(mockXAConnection).close();

        if (connectionsToFail > 0) {
                connections.add(mockXAConnection);
	}
        return mockXAConnection;
    }

    public XAConnection createXAConnection(String jndiName, String jndiName1) throws JMSException {
        return createXAConnection();
    }

	@Override
	public XAJMSContext createXAContext()
	{
		throw new RuntimeException("Method not supported");
	}

	@Override
	public XAJMSContext createXAContext(String userName, String password)
	{
		throw new RuntimeException("Method not supported");
	}

	public static void setStaticCloseXAConnectionException(JMSException e) {
        staticCloseXAConnectionException = e;
    }

    public static void setStaticCreateXAConnectionException(JMSException e) {
        staticCreateXAConnectionException = e;
    }

        public static void setStaticConnectionsToFail(int failAfterConnections) {
                connectionsToFail = failAfterConnections;
	}

	public static void resetStatus() {
    	        connections= new ArrayList<>();
	}
	public static List<XAConnection> getConnections() {
    	        return connections;
	}

}
