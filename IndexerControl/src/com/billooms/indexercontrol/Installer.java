package com.billooms.indexercontrol;

import org.openide.modules.ModuleInstall;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall {

	@Override
	public void restored() {
		// By default, do nothing.
		// Put your startup code here.
	}

	/**
	 * Before closing, make sure all hardware is shutdown.
	 * @return true=OK to exit, false=don't exit
	 */
	@Override
	public boolean closing() {
		WindowManager.getDefault().findTopComponent("ControlTopComponent").getLookup().lookup(RotationStage.class).shutDown();
		return true;
	}
}
