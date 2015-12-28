package de.fhwedel.coinflipping.tls.network;

import java.io.BufferedOutputStream;

import java.io.IOException;

import java.io.PrintWriter;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

public class TLSServer {
	/** the server socket */
	private SSLServerSocket tlsServerSocket;

	/** the sockets of the clients */
	private HashMap<Integer, SSLSocket> clients;

	/** the clients the server communicates with */
	private HashMap<Integer, CommunicationServer> comWithClients;

	/** the player **/
	private TLSNetwork networkComm;

	/**
	 * Constructor.
	 */
	public TLSServer(TLSNetwork nc) {
		super();
		clients = new HashMap<Integer, SSLSocket>();
		comWithClients = new HashMap<Integer, CommunicationServer>();
		this.networkComm = nc;
	}

	/**
	 * Starting the server on a given port. Trusted certificates are used to
	 * verify clients.
	 * 
	 * This method returns right away. Waiting for clients happens in another
	 * thread.
	 * 
	 * @param port
	 *            the port the server is started on.
	 * @param sslContext
	 *            the SSL Context
	 */
	public void start(int port, SSLContext sslContext) {
		try {
			SSLServerSocketFactory sslserversocketfactory = sslContext.getServerSocketFactory();
			this.tlsServerSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(port);

			this.tlsServerSocket.setNeedClientAuth(true);
			// this.log("Server listening on port " + port);

			final ServerThread runnable = new ServerThread(this);
			final Thread waitingForClient = new Thread(runnable, "waitingForClientsToConnect");
			waitingForClient.start();
		} catch (IOException e) {
			// this.log("Error: Unable to start server");
			e.printStackTrace();
		}
	}

	/**
	 * Stops the server and all open connections.
	 */
	public void stop() {
		try {
			final Collection<SSLSocket> allSockets = this.clients.values();
			final Iterator<SSLSocket> i = allSockets.iterator();
			while (i.hasNext()) {
				final SSLSocket c = i.next();
				c.close();
			}
			this.tlsServerSocket.close();
			// this.log("Server stopped");
		} catch (IOException e) {
			System.out.println("Error closing the server.");
		}
	}

	/**
	 * Returns the server socket.
	 * 
	 * @return the server socket
	 */
	public SSLServerSocket getServerSocket() {
		return this.tlsServerSocket;
	}

	/**
	 * A new client has connected .
	 * 
	 * @param s
	 *            socket of the client
	 * @param input
	 *            Anmeldenachricht des Clients.
	 */
	public synchronized void newClient(SSLSocket s) {
		// future implementation: waiting for more clients:
		/*
		 * final ServerThread runnable = new ServerThread(this); final Thread
		 * waitingForClient = new Thread(runnable,
		 * "waitingForClientsToConnect"); waitingForClient.start();
		 */

		// identifier of the client
		Integer clientId = 1;
		while (clients.get(clientId) != null) {
			clientId++;
		}
		clients.put(clientId, s);

		// this.log("Client registered with id: " + clientId);

		this.networkComm.newPlayerConnected(clientId);

		// waiting for communication with the client
		final CommunicationServer communication = new CommunicationServer(this, s, clientId);
		this.comWithClients.put(clientId, communication);
		final Thread waitingForCommunication = new Thread(communication, "communicationWithClient" + clientId);
		waitingForCommunication.start();
	}

	/**
	 * A client left.
	 * 
	 * @param clientId
	 *            the id of the client
	 */
	public void clientLeft(Integer clientID) {
		try {
			clients.get(clientID).close();
		} catch (IOException e) {
			System.out.println("IOException trying to close socket of client " + clientID);
		}
		clients.remove(clientID);
		// this.log("Client " + clientID + " left");
	}

	/**
	 * Sends a single message to a client.
	 * 
	 * @param c
	 *            Socket of the client
	 * @param message
	 *            the message
	 * @throws IOException
	 *             if something goes wrong.
	 */
	private synchronized void sendMessageToClient(Socket s, String message) throws IOException {
		final BufferedOutputStream out = new BufferedOutputStream(s.getOutputStream());
		final PrintWriter printer = new PrintWriter(out);
		printer.println(message);
		printer.flush();
	}

	/**
	 * A message is received.
	 * 
	 * @param message
	 *            the message
	 */
	public synchronized void received(String message) {
		// this.log("Received: " + message);
		this.networkComm.receivedMessage(message);

	}

	/**
	 * Sending a message to all registered clients.
	 * 
	 * @param the
	 *            message
	 */
	public synchronized void send(String message) {
		// this.log("Sending: " + message);
		final Collection<SSLSocket> allSockets = this.clients.values();
		final Iterator<SSLSocket> i = allSockets.iterator();
		while (i.hasNext()) {
			final SSLSocket c = i.next();
			try {
				sendMessageToClient(c, message);
			} catch (IOException e) {
				// this.log("Error sending message");
			}
		}
	}

	/**
	 * Returns the public key of the peer we connected to.
	 * 
	 * @return the public key
	 * @throws SSLPeerUnverifiedException
	 *             if the peer wasn't verified
	 */
	public synchronized PublicKey getOtherPubKey() throws SSLPeerUnverifiedException {
		final Collection<SSLSocket> allSockets = this.clients.values();
		if (allSockets.size() == 1) {
			final Iterator<SSLSocket> i = allSockets.iterator();
			while (i.hasNext()) {
				final SSLSocket c = i.next();
				SSLSession session = c.getSession();
				java.security.cert.Certificate cert = session.getPeerCertificates()[0];
				return cert.getPublicKey();
			}
		}
		return null;
	}
}
