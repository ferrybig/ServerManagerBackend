/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend;


public interface ByteListener {

	public void onIncomingBytes(byte[] bytes, int start, int end);

}
