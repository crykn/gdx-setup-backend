package com.badlgoic.gdx.setup.jpa;

import java.util.ArrayList;

/**
 * Depicts the dependencies needed for a specific version of a
 * {@linkplain GdxExtensionEntity libGDX extension}.
 */
public class GdxExtensionDependenciesEntity {
	private ArrayList<String> incompatiblePlatforms = new ArrayList<>();

	private ArrayList<String> core = new ArrayList<>();
	private ArrayList<String> desktop = new ArrayList<>();
	private ArrayList<String> gwt = new ArrayList<>();
	private ArrayList<String> android = new ArrayList<>();
	private ArrayList<String> iOS = new ArrayList<>();

	private ArrayList<String> gwtInherits = new ArrayList<>();

	public ArrayList<String> getIncompatiblePlatforms() {
		return incompatiblePlatforms;
	}

	public ArrayList<String> getCoreDependencies() {
		return core;
	}

	public ArrayList<String> getDesktopDependencies() {
		return desktop;
	}

	public ArrayList<String> getGwtDependencies() {
		return gwt;
	}

	public ArrayList<String> getAndroidDependencies() {
		return android;
	}

	public ArrayList<String> getIosDependencies() {
		return iOS;
	}

	public ArrayList<String> getGwtInherits() {
		return gwtInherits;
	}

}
