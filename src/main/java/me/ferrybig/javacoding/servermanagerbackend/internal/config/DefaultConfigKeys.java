/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.internal.config;

import java.util.List;

/**
 *
 * @author Fernando van Loenhout
 */
public interface DefaultConfigKeys {

	public static final ConfigKey<String> WORKING_DIRECTORY = new DefaultConfigKey<>(
		"default", "working_directory", false, ConfigFormat.SERVER_FILE, "Working directory for the server");

	public static final ConfigKey<List<String>> START_COMMAND = new DefaultConfigKey<>(
		"default", "start_command", false, ConfigFormat.STRING_LIST, "Start command for the server");

	public static final ConfigKey<List<String>> SHUTDOWN_COMMAND = new DefaultConfigKey<>(
		"default", "shutdown_command", false, ConfigFormat.STRING_LIST, "Start command for the server");
}
