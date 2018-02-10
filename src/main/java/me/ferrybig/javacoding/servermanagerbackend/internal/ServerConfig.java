/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.internal;

import com.google.gson.GsonBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ServerConfig {

	private final List<String> commandLine;
	private final String directory;
	private final List<String> shutdownCommands;

	public ServerConfig() {
		this(Collections.emptyList(), "", Arrays.asList("stop"));
	}

	public ServerConfig(List<String> commandLine, String directory, List<String> shutdownCommands) {
		this.commandLine = commandLine;
		this.directory = directory;
		this.shutdownCommands = shutdownCommands;
	}

	public List<String> getCommandLine() {
		return commandLine;
	}

	public String getDirectory() {
		return directory;
	}

	public List<String> getShutdownCommands() {
		return shutdownCommands;
	}

	@Override
	public String toString() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(this);
	}

}
