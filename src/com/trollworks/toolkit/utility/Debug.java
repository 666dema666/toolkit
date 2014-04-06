/*
 * Copyright (c) 1998-2014 by Richard A. Wilkes. All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * version 2.0. If a copy of the MPL was not distributed with this file, You
 * can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as defined
 * by the Mozilla Public License, version 2.0.
 */

package com.trollworks.toolkit.utility;

import com.trollworks.toolkit.io.Log;

/** Provides various debugging utilities. */
public final class Debug {
	/** Controls whether we are in 'development mode' or not. */
	public static final boolean	DEV_MODE			= false;
	/** Standard debug key for enabling diagnosis for load/save failures. */
	public static final String	DIAGNOSE_LOAD_SAVE	= "com.trollworks.toolkit.utility.Debug.DiagnoseLoadSave";	//$NON-NLS-1$

	/**
	 * Determines whether the specified key is set, looking first in the system properties and
	 * falling back to the system environment if it is not set at all in the system properties.
	 *
	 * @param key The key to check.
	 * @return <code>true</code> if the key is enabled.
	 */
	public static final boolean isKeySet(String key) {
		String value = System.getProperty(key);
		if (value == null) {
			value = System.getenv(key);
		}
		return Numbers.extractBoolean(value);
	}

	/**
	 * If load/save operation diagnosis has been asked for, dump the {@link Exception}'s stack trace
	 * to the log.
	 *
	 * @param exception The {@link Exception}.
	 */
	public static void diagnoseLoadAndSave(Exception exception) {
		if (isKeySet(DIAGNOSE_LOAD_SAVE)) {
			Log.error(exception);
		}
	}

	/**
	 * Extracts the class name, message and stack trace from the specified {@link Throwable}. The
	 * stack trace will be formatted such that Eclipse's console will make each node into a
	 * hyperlink.
	 *
	 * @param throwable The {@link Throwable} to process.
	 * @return The formatted {@link Throwable}.
	 */
	public static final String toString(Throwable throwable) {
		StringBuilder buffer = new StringBuilder();
		buffer.append(throwable.getClass().getSimpleName());
		buffer.append(": "); //$NON-NLS-1$
		buffer.append(throwable.getMessage());
		buffer.append(": "); //$NON-NLS-1$
		stackTrace(throwable, buffer);
		return buffer.toString();
	}

	/**
	 * Extracts a stack trace from the specified {@link Throwable}. The stack trace will be
	 * formatted such that Eclipse's console will make each node into a hyperlink.
	 *
	 * @param throwable The {@link Throwable} to process.
	 * @param buffer The buffer to store the result in.
	 * @return The {@link StringBuilder} that was passed in.
	 */
	public static final StringBuilder stackTrace(Throwable throwable, StringBuilder buffer) {
		return stackTrace(throwable, 0, buffer);
	}

	/**
	 * Extracts a stack trace from the specified {@link Throwable}. The stack trace will be
	 * formatted such that Eclipse's console will make each node into a hyperlink.
	 *
	 * @param throwable The {@link Throwable} to process.
	 * @param startAt The point in the stack to start processing.
	 * @param buffer The buffer to store the result in.
	 * @return The {@link StringBuilder} that was passed in.
	 */
	public static final StringBuilder stackTrace(Throwable throwable, int startAt, StringBuilder buffer) {
		StackTraceElement[] stackTrace = throwable.getStackTrace();
		for (int i = startAt; i < stackTrace.length; i++) {
			if (i > startAt) {
				buffer.append(" < "); //$NON-NLS-1$
			}
			buffer.append('(');
			buffer.append(stackTrace[i].getFileName());
			buffer.append(':');
			buffer.append(stackTrace[i].getLineNumber());
			buffer.append(')');
		}
		return buffer;
	}
}
