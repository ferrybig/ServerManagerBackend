/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.routing.routes;

import me.ferrybig.javacoding.servermanagerbackend.api.request.KillStreamRequest;
import me.ferrybig.javacoding.servermanagerbackend.routing.Client;
import me.ferrybig.javacoding.servermanagerbackend.routing.Handler;

public class KillStreamHandler implements Handler<KillStreamRequest> {

	@Override
	public void handleRequest(Client client, KillStreamRequest req) {
		client.getOutgoing().cancelStream(req);
	}

}
