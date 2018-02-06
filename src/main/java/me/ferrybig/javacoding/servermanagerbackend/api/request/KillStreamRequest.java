/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.api.request;

import com.google.gson.annotations.Expose;

/**
 *
 * @author Fernando van Loenhout
 */
public class KillStreamRequest extends Request {

	@Expose
	public int id;

	public KillStreamRequest() {
		super(Type.KILL_STREAM);
	}

}
