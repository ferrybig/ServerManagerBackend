/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.routing;

import java.util.Objects;

public class Client {

	private final IncomingChannel incoming;
	private final OutboundChannel outgoing;

	public Client(IncomingChannel incoming, OutboundChannel outgoing) {
		this.incoming = incoming;
		this.outgoing = outgoing;
	}

	public IncomingChannel getIncoming() {
		return incoming;
	}

	public OutboundChannel getOutgoing() {
		return outgoing;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 19 * hash + Objects.hashCode(this.incoming);
		hash = 19 * hash + Objects.hashCode(this.outgoing);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Client other = (Client) obj;
		if (!Objects.equals(this.incoming, other.incoming)) {
			return false;
		}
		if (!Objects.equals(this.outgoing, other.outgoing)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Client{" + "incoming=" + incoming + ", outgoing=" + outgoing + '}';
	}

}
