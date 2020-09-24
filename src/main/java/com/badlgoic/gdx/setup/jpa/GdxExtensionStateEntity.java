package com.badlgoic.gdx.setup.jpa;

import java.util.ArrayList;

/**
 * Depicts the dependencies needed for a specific version of a
 * {@linkplain GdxExtensionEntity libGDX extension}.
 */
public class GdxExtensionStateEntity {
	private ArrayList<String> incompatiblePlatforms = new ArrayList<>();

	private ArrayList<String> coreDependencies = new ArrayList<>();
	private ArrayList<String> desktopDependencies = new ArrayList<>();
	private ArrayList<String> gwtDependencies = new ArrayList<>();
	private ArrayList<String> androidDependencies = new ArrayList<>();
	private ArrayList<String> iOSDependencies = new ArrayList<>();

	private ArrayList<String> gwtInherits = new ArrayList<>();
	
	private ArrayList<String> androidPermissions = new ArrayList<>();

	public ArrayList<String> getIncompatiblePlatforms() {
		return incompatiblePlatforms;
	}

	public ArrayList<String> getCoreDependencies() {
		return coreDependencies;
	}

	public ArrayList<String> getDesktopDependencies() {
		return desktopDependencies;
	}

	public ArrayList<String> getGwtDependencies() {
		return gwtDependencies;
	}

	public ArrayList<String> getAndroidDependencies() {
		return androidDependencies;
	}

	public ArrayList<String> getIosDependencies() {
		return iOSDependencies;
	}

	public ArrayList<String> getGwtInherits() {
		return gwtInherits;
	}
	
	public ArrayList<String> getAndroidPermissions() {
		return androidPermissions;
	}

}
