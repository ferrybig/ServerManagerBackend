/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.routing;

import java.util.HashMap;
import java.util.Map;
import me.ferrybig.javacoding.servermanagerbackend.api.request.Request;
import me.ferrybig.javacoding.servermanagerbackend.api.response.InstantResponse;

public class TypeHandler<T extends Request> implements Handler<T> {

	private final Map<Class<? extends T>, Handler<? extends T>> routingMap = new HashMap<>();

	public <F extends T> TypeHandler<T> addRoute(Class<? extends F> clazz, Handler<F> route) {
		routingMap.put(clazz, route);
		return this;
	}

	@SuppressWarnings("unchecked")
	private void callUnsafe(Handler handler, Client client, T request) {
		handler.handleRequest(client, request);
	}

	@Override
	public void handleRequest(Client client, T req) {
		Class<?> requestClass = req.getClass();
		do {
			Handler<? extends T> handler = routingMap.get(requestClass);
			if (handler != null) {
				callUnsafe(handler, client, req);
				return;
			}
		} while ((requestClass = requestClass.getSuperclass()) != null);
		client.getOutgoing().writeResponse(new InstantResponse(false, req, "Commmand not found in routing map"));

	}

}
