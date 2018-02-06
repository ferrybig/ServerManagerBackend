/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.internal;

import java.util.Collections;
import java.util.List;

public class ServerConfig {

	private final List<String> commandLine;
	private final String directory;

	public ServerConfig() {
		this(Collections.emptyList(), "");
	}

	public ServerConfig(List<String> commandLine, String directory) {
		this.commandLine = commandLine;
		this.directory = directory;
	}

	public List<String> getCommandLine() {
		return commandLine;
	}

	public String getDirectory() {
		return directory;
	}

	@Override
	public String toString() {
		return "ServerConfig{" + "commandLine=" + commandLine + ", directory=" + directory + '}';
	}

}
