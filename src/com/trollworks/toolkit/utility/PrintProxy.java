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

import com.trollworks.toolkit.ui.print.PrintManager;

import java.awt.print.Printable;

/** Objects that want to be printable by the framework must implement this interface. */
public interface PrintProxy extends Printable {
	/** @return The {@link PrintManager} to use. */
	PrintManager getPrintManager();

	/** @return The title of the print job. */
	String getPrintJobTitle();

	/** Called when the page setup has changed. */
	void adjustToPageSetupChanges();

	/** @return <code>true</code> when printing is in progress. */
	boolean isPrinting();

	/** @param printing The current state to return from a call to {@link #isPrinting()}. */
	void setPrinting(boolean printing);
}
