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
public class ActionRequest extends Request {

	@Expose
	public String action;
	@Expose
	public String arguments;

	public ActionRequest() {
		super(Request.Type.ACTION);
	}
}