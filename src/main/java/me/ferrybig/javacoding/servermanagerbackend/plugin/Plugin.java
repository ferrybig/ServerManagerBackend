/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.plugin;

/**
 *
 * @author Fernando
 */
public interface Plugin {

	boolean isEnabled();

	void onLoad();

	void onUnload();

	void onEnabled();

	void onDisable();

	void setEnabled(boolean newState);

	PluginConfiguration getPluginConfiguration();
}
