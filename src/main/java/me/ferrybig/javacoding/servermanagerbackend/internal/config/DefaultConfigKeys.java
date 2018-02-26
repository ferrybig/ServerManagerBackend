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

	final ConfigKey<String> WORKING_DIRECTORY = new DefaultConfigKey<>(
		"default", "working_directory", false, ConfigFormat.SERVER_FILE, "Working directory for the server");

	final ConfigKey<List<String>> START_COMMAND = new DefaultConfigKey<>(
		"default", "start_command", false, ConfigFormat.STRING_LIST, "Start command for the server");

	final ConfigKey<List<String>> SHUTDOWN_COMMAND = new DefaultConfigKey<>(
		"default", "shutdown_command", false, ConfigFormat.STRING_LIST, "Start command for the server");

	final ConfigKey<String> DISPLAY_NAME = new DefaultConfigKey<>(
		"default", "display_name", false, ConfigFormat.STRING, "Friendly name for the server");

	final ConfigKey<Integer> VIRTUAL_HOST_PORT = new DefaultConfigKey<>(
		"default", "server_port", false, ConfigFormat.INTEGER, "Port the server shuld be running on");

	final ConfigKey<String> VIRTUAL_HOST_IP = new DefaultConfigKey<>(
		"default", "server_ip", false, ConfigFormat.IP_ADDRESS, "Ip address the server should be running on");
}
