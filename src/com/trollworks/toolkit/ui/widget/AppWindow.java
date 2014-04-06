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

package com.trollworks.toolkit.ui.widget;

import com.trollworks.toolkit.collections.FilteredIterator;
import com.trollworks.toolkit.ui.Path;
import com.trollworks.toolkit.ui.layout.FlexRow;
import com.trollworks.toolkit.ui.menu.StdMenuBar;
import com.trollworks.toolkit.ui.menu.edit.Undoable;
import com.trollworks.toolkit.ui.menu.file.FileProxy;
import com.trollworks.toolkit.ui.menu.window.WindowMenu;
import com.trollworks.toolkit.ui.preferences.MenuKeyPreferences;
import com.trollworks.toolkit.ui.print.PageOrientation;
import com.trollworks.toolkit.ui.print.PrintManager;
import com.trollworks.toolkit.ui.undo.StdUndoManager;
import com.trollworks.toolkit.utility.units.LengthUnits;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JToolBar;

/** Provides a base OS-level window. */
public class AppWindow extends BaseWindow implements Comparable<AppWindow>, Undoable {
	private static BufferedImage				DEFAULT_WINDOW_ICON	= null;
	private static final ArrayList<AppWindow>	WINDOW_LIST			= new ArrayList<>();
	private BufferedImage						mWindowIcon;
	private PrintManager						mPrintManager;
	private StdUndoManager						mUndoManager;
	private BufferedImage						mTitleIcon;
	private boolean								mIsPrinting;

	/** @return The top-most window. */
	public static AppWindow getTopWindow() {
		if (!WINDOW_LIST.isEmpty()) {
			return WINDOW_LIST.get(0);
		}
		return null;
	}

	/**
	 * Creates a new {@link AppWindow}.
	 *
	 * @param title The window title. May be <code>null</code>.
	 * @param largeIcon The 32x32 window icon. OK to pass in a 16x16 icon here.
	 * @param smallIcon The 16x16 window icon.
	 */
	public AppWindow(String title, BufferedImage largeIcon, BufferedImage smallIcon) {
		this(title, largeIcon, smallIcon, null, false);
	}

	/**
	 * Creates a new {@link AppWindow}.
	 *
	 * @param title The title of the window.
	 * @param largeIcon The 32x32 window icon. OK to pass in a 16x16 icon here.
	 * @param smallIcon The 16x16 window icon.
	 * @param gc The graphics configuration to use.
	 * @param undecorated Whether to create an undecorated window, without menus.
	 */
	public AppWindow(String title, BufferedImage largeIcon, BufferedImage smallIcon, GraphicsConfiguration gc, boolean undecorated) {
		super(title, gc);
		if (undecorated) {
			setUndecorated(true);
		}
		if (!undecorated) {
			MenuKeyPreferences.loadFromPreferences();
			setJMenuBar(new StdMenuBar());
		}
		if (largeIcon == null) {
			largeIcon = DEFAULT_WINDOW_ICON;
		}
		if (largeIcon != null) {
			setTitleIcon(largeIcon);
		}

		if (smallIcon == null) {
			smallIcon = largeIcon;
		}
		if (smallIcon != null) {
			setMenuIcon(smallIcon);
		}
		mUndoManager = new StdUndoManager();
		enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);
		WINDOW_LIST.add(this);
	}

	/** Call to create the toolbar for this window. */
	protected final void createToolBar() {
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		FlexRow row = new FlexRow();
		row.setInsets(new Insets(2, 5, 2, 5));
		createToolBarContents(toolbar, row);
		row.apply(toolbar);
		add(toolbar, BorderLayout.NORTH);
	}

	/**
	 * Called to create the toolbar contents for this window.
	 *
	 * @param toolbar The {@link JToolBar} to add items to.
	 * @param row The {@link FlexRow} layout to add items to.
	 */
	protected void createToolBarContents(JToolBar toolbar, FlexRow row) {
		// Does nothing by default.
	}

	/** @return The default window icon. */
	public static BufferedImage getDefaultWindowIcon() {
		return DEFAULT_WINDOW_ICON;
	}

	/** @param image The new default window icon. */
	public static void setDefaultWindowIcon(BufferedImage image) {
		DEFAULT_WINDOW_ICON = image;
	}

	/** @param icon The icon representing this window. */
	public void setTitleIcon(BufferedImage icon) {
		if (icon != mTitleIcon) {
			mTitleIcon = icon;
			setIconImage(mTitleIcon);
		}
	}

	/** @return The icon representing this window. */
	public BufferedImage getTitleIcon() {
		return mTitleIcon;
	}

	/** @return The menu icon representing this window. */
	public BufferedImage getMenuIcon() {
		return mWindowIcon;
	}

	/** @param icon The menu icon representing this window. */
	public void setMenuIcon(BufferedImage icon) {
		mWindowIcon = icon;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			WindowMenu.update();
		}
	}

	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		if (isVisible()) {
			WindowMenu.update();
		}
	}

	@Override
	public void toFront() {
		if (!isClosed()) {
			Component focus;

			if (getExtendedState() == ICONIFIED) {
				setExtendedState(NORMAL);
			}
			super.toFront();
			if (!isActive() || !isFocused()) {
				focus = getMostRecentFocusOwner();
				if (focus != null) {
					focus.requestFocus();
				} else {
					requestFocus();
				}
			}
		}
	}

	/** @return The {@link PrintManager} for this window. */
	public final PrintManager getPrintManager() {
		if (mPrintManager == null) {
			mPrintManager = createPageSettings();
		}
		return mPrintManager;
	}

	/** @param printManager The {@link PrintManager} to use. */
	public void setPrintManager(PrintManager printManager) {
		mPrintManager = printManager;
	}

	/** @return The default page settings for this window. May return <code>null</code>. */
	@SuppressWarnings("static-method")
	protected PrintManager createPageSettings() {
		try {
			return new PrintManager(PageOrientation.PORTRAIT, 0.5, LengthUnits.INCHES);
		} catch (Exception exception) {
			return null;
		}
	}

	/** Called after the page setup has changed. */
	public void adjustToPageSetupChanges() {
		// Does nothing by default.
	}

	/** @return <code>true</code> if the window is currently printing. */
	public boolean isPrinting() {
		return mIsPrinting;
	}

	/** @param printing <code>true</code> if the window is currently printing. */
	public void setPrinting(boolean printing) {
		mIsPrinting = printing;
	}

	/** @param event The {@link MouseWheelEvent}. */
	protected void processMouseWheelEventSuper(MouseWheelEvent event) {
		super.processMouseWheelEvent(event);
	}

	@Override
	public void dispose() {
		if (!isClosed()) {
			WINDOW_LIST.remove(this);
			if (WINDOW_LIST.isEmpty()) {
				for (PaletteWindow window : getWindows(PaletteWindow.class)) {
					window.setAppWindow(null);
				}
			}
		}
		super.dispose();
		WindowMenu.update();
	}

	/**
	 * @param windowClass The window class to return.
	 * @param <T> The window type.
	 * @return The current visible windows, in order from top to bottom.
	 */
	public static <T extends AppWindow> ArrayList<T> getActiveWindows(Class<T> windowClass) {
		ArrayList<T> list = new ArrayList<>();
		for (T window : new FilteredIterator<>(WINDOW_LIST, windowClass)) {
			if (window.isShowing()) {
				list.add(window);
			}
		}
		return list;
	}

	@Override
	public void windowGainedFocus(WindowEvent event) {
		if (event.getWindow() == this) {
			WINDOW_LIST.remove(this);
			WINDOW_LIST.add(0, this);
			for (PaletteWindow window : getWindows(PaletteWindow.class)) {
				window.setAppWindow(this);
			}
		}
		super.windowGainedFocus(event);
	}

	/** @return The window's {@link StdUndoManager}. */
	@Override
	public StdUndoManager getUndoManager() {
		return mUndoManager;
	}

	/**
	 * @param file The backing file to look for.
	 * @return The {@link AppWindow} associated with the specified backing file.
	 */
	public static AppWindow findWindow(File file) {
		String fullPath = Path.getFullPath(file);
		for (AppWindow window : AppWindow.getAllWindows()) {
			if (window instanceof FileProxy) {
				File wFile = ((FileProxy) window).getBackingFile();
				if (wFile != null) {
					if (Path.getFullPath(wFile).equals(fullPath)) {
						return window;
					}
				}
			}
		}
		return null;
	}

	/** @return A list of all {@link AppWindow}s created by this application. */
	public static ArrayList<AppWindow> getAllWindows() {
		return getWindows(AppWindow.class);
	}

	/** @return The title to be used for this window in the window menu. */
	protected String getTitleForWindowMenu() {
		return getTitle();
	}

	@Override
	public int compareTo(AppWindow other) {
		if (this != other) {
			String title = getTitleForWindowMenu();
			String otherTitle = other.getTitleForWindowMenu();
			if (title != null) {
				if (otherTitle == null) {
					return 1;
				}
				return title.compareTo(otherTitle);
			}
			if (otherTitle != null) {
				return -1;
			}
		}
		return 0;
	}
}