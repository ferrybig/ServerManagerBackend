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
public interface ConfigKey<T> {
	boolean allowsEmptyValue();
	String getDescription();
	ConfigFormat<? super T> getFormat();
}
