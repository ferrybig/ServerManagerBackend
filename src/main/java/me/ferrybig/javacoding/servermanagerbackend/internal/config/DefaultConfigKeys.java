/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.internal.config;

/**
 *
 * @author Fernando van Loenhout
 */
public class DefaultConfigKeys {
	public static final String WORKING_DIRECTORY_NAME = "working_directory";
	public static final ConfigKey<String> WORKING_DIRECTORY = new DefaultConfigKey<>(
			WORKING_DIRECTORY_NAME, false, ConfigFormat.SERVER_FILE, "Working directory for the server");
}
