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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.stubbing.Answer;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;

/**
 *
 * @author lorban
 */
public class MockConnectionFactory implements ConnectionFactory {

    @Override
    public Connection createConnection() throws JMSException {
    	Answer<Session> sessionAnswer =  invocation -> {
				Session session = mock(Session.class);
				MessageProducer producer = mock(MessageProducer.class);
				when(session.createProducer((Destination) any())).thenReturn(producer);
				return session;
			};

    	Connection connection = mock(Connection.class);
    	when(connection.createSession(anyBoolean(), anyInt())).thenAnswer(sessionAnswer);
    	return connection;
    }

    @Override
    public Connection createConnection(String jndiName, String jndiName1) throws JMSException {
        return createConnection();
    }

	@Override
	public JMSContext createContext()
	{
		throw new RuntimeException("Method not supported");
	}

	@Override
	public JMSContext createContext(String userName, String password)
	{
		throw new RuntimeException("Method not supported");
	}

	@Override
	public JMSContext createContext(String userName, String password, int sessionMode)
	{
		throw new RuntimeException("Method not supported");
	}

	@Override
	public JMSContext createContext(int sessionMode)
	{
		throw new RuntimeException("Method not supported");
	}
}