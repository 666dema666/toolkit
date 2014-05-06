package com.trollworks.toolkit.ui.widget.dock;

import com.trollworks.toolkit.annotation.Localize;
import com.trollworks.toolkit.ui.border.SelectiveLineBorder;
import com.trollworks.toolkit.ui.image.ToolkitImage;
import com.trollworks.toolkit.ui.layout.PrecisionLayout;
import com.trollworks.toolkit.ui.layout.PrecisionLayoutData;
import com.trollworks.toolkit.ui.widget.IconButton;
import com.trollworks.toolkit.utility.Localization;

import java.awt.LayoutManager;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/** The header for a {@link DockContainer}. */
public class DockHeader extends JPanel implements ContainerListener {
	@Localize("Maximize")
	private static String	MAXIMIZE_TOOLTIP;
	@Localize("Restore")
	private static String	RESTORE_TOOLTIP;
	private IconButton		mMaximizeRestoreButton;

	static {
		Localization.initialize();
	}

	/**
	 * Creates a new {@link DockHeader} for the specified {@link Dockable}.
	 *
	 * @param dockable The {@link Dockable} to work with.
	 */
	public DockHeader(Dockable dockable) {
		super(new PrecisionLayout().setMargins(0).setMiddleVerticalAlignment());
		setOpaque(true);
		setBackground(DockColors.BACKGROUND);
		setBorder(new CompoundBorder(new SelectiveLineBorder(DockColors.SHADOW, 0, 0, 1, 0), new EmptyBorder(2, 4, 2, 4)));
		addContainerListener(this);
		add(new DockTab(dockable), new PrecisionLayoutData().setGrabHorizontalSpace(true));
		if (dockable instanceof DockMaximizable) {
			mMaximizeRestoreButton = new IconButton(ToolkitImage.getDockMaximize(), MAXIMIZE_TOOLTIP, this::maximize);
			add(mMaximizeRestoreButton, new PrecisionLayoutData().setEndHorizontalAlignment());
		}
	}

	private void maximize() {
		DockContainer dc = (DockContainer) getParent();
		dc.getDock().maximize(dc.getDockable());
	}

	private void restore() {
		((DockContainer) getParent()).getDock().restore();
	}

	/** Called when the owning {@link DockContainer} is set to the maximized state. */
	void adjustToMaximizedState() {
		mMaximizeRestoreButton.setClickFunction(this::restore);
		mMaximizeRestoreButton.setIcon(ToolkitImage.getDockRestore());
		mMaximizeRestoreButton.setToolTipText(RESTORE_TOOLTIP);
	}

	/** Called when the owning {@link DockContainer} is restored from the maximized state. */
	void adjustToRestoredState() {
		mMaximizeRestoreButton.setClickFunction(this::maximize);
		mMaximizeRestoreButton.setIcon(ToolkitImage.getDockMaximize());
		mMaximizeRestoreButton.setToolTipText(MAXIMIZE_TOOLTIP);
	}

	@Override
	public PrecisionLayout getLayout() {
		return (PrecisionLayout) super.getLayout();
	}

	@Override
	public void setLayout(LayoutManager mgr) {
		if (mgr instanceof PrecisionLayout) {
			super.setLayout(mgr);
		} else {
			throw new IllegalArgumentException("Must use a PrecisionLayout."); //$NON-NLS-1$
		}
	}

	@Override
	public void componentAdded(ContainerEvent event) {
		getLayout().setColumns(getComponentCount());
	}

	@Override
	public void componentRemoved(ContainerEvent event) {
		getLayout().setColumns(getComponentCount());
	}

	/**
	 * Called when the 'active' state changes.
	 *
	 * @param active Whether the header should be drawn in its active state or not.
	 */
	void setActive(boolean active) {
		setBackground(active ? DockColors.ACTIVE_DOCK_HEADER_BACKGROUND : DockColors.BACKGROUND);
	}
}
