/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.api.request.server;

/**
 *
 * @author Fernando van Loenhout
 */
public class InfoRequest extends ServerRequest {

	public InfoRequest() {
		super(Type.SERVER_INFO);
	}

}
