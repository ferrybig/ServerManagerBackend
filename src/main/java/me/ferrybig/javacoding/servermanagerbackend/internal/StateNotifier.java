/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.internal;

public interface StateNotifier {

	void addListener(StateListener listener);

	void removeListener(StateListener listener);

}
