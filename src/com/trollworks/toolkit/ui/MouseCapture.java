package com.trollworks.toolkit.ui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

/**
 * Captures all mouse events in a {@link JRootPane} and redirects them to a particular component or
 * one of its children.
 */
public class MouseCapture implements MouseListener, MouseMotionListener, HierarchyListener {
	private static final Map<Component, MouseCapture>	MAP	= new HashMap<>();
	private Component									mGlassPane;
	private Component									mCaptureComponent;

	/**
	 * Starts redirecting all mouse events to the specified component or one of its children.
	 *
	 * @param component The target.
	 */
	public static void start(Component component) {
		JRootPane rootPane = SwingUtilities.getRootPane(component);
		if (rootPane != null) {
			Component glassPane = rootPane.getGlassPane();
			MouseCapture capture = new MouseCapture(glassPane, component);
			glassPane.addMouseListener(capture);
			glassPane.addMouseMotionListener(capture);
			glassPane.addHierarchyListener(capture);
			MAP.put(component, capture);
			glassPane.setVisible(true);
		}
	}

	/**
	 * Stops redirecting mouse events.
	 *
	 * @param component The target that was passed to {@link #start(Component)}.
	 */
	public static void stop(Component component) {
		MouseCapture capture = MAP.remove(component);
		if (capture != null) {
			capture.mGlassPane.removeMouseListener(capture);
			capture.mGlassPane.removeMouseMotionListener(capture);
			capture.mGlassPane.removeHierarchyListener(capture);
			capture.mGlassPane.setVisible(false);
		}
	}

	private MouseCapture(Component glassPane, Component capture) {
		mGlassPane = glassPane;
		mCaptureComponent = capture;
	}

	@Override
	public void hierarchyChanged(HierarchyEvent event) {
		if (!mCaptureComponent.isDisplayable()) {
			stop(mCaptureComponent);
		}
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		redispatchMouseEvent(event);
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		redispatchMouseEvent(event);
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		redispatchMouseEvent(event);
	}

	@Override
	public void mouseEntered(MouseEvent event) {
		redispatchMouseEvent(event);
	}

	@Override
	public void mouseExited(MouseEvent event) {
		redispatchMouseEvent(event);
	}

	@Override
	public void mousePressed(MouseEvent event) {
		redispatchMouseEvent(event);
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		redispatchMouseEvent(event);
	}

	private void redispatchMouseEvent(MouseEvent event) {
		Point glassPanePoint = event.getPoint();
		Point containerPoint = SwingUtilities.convertPoint(mGlassPane, glassPanePoint, mCaptureComponent);
		Component component = SwingUtilities.getDeepestComponentAt(mCaptureComponent, containerPoint.x, containerPoint.y);
		if (component != null) {
			Point componentPoint = SwingUtilities.convertPoint(mGlassPane, glassPanePoint, component);
			component.dispatchEvent(new MouseEvent(component, event.getID(), event.getWhen(), event.getModifiers(), componentPoint.x, componentPoint.y, event.getClickCount(), event.isPopupTrigger()));
		}
	}
}