package com.badlgoic.gdx.setup.jpa;

import javax.annotation.Nullable;

/**
 * Depicts a single libGDX extension.
 */
public class GdxExtensionEntity {

	private String name;
	private String author;
	private String description;
	private String projectUrl;

	@Nullable
	private GdxExtensionStateEntity dependenciesStableVersion;
	@Nullable
	private GdxExtensionStateEntity dependenciesLatestVersion;

	public GdxExtensionEntity() {
		// empty default constructor
	}

	public GdxExtensionEntity(String name, String author, String description, String projectUrl,
			@Nullable GdxExtensionStateEntity dependenciesStableVersion,
			@Nullable GdxExtensionStateEntity dependenciesLatestVersion) {
		this.name = name;
		this.author = author;
		this.description = description;
		this.projectUrl = projectUrl;
		this.dependenciesStableVersion = dependenciesStableVersion;
		this.dependenciesLatestVersion = dependenciesLatestVersion;
	}

	public String getName() {
		return name;
	}

	public String getAuthor() {
		return author;
	}

	public String getDescription() {
		return description;
	}

	public String getProjectUrl() {
		return projectUrl;
	}

	@Nullable
	public GdxExtensionStateEntity getDependenciesStableVersion() {
		return dependenciesStableVersion;
	}

	@Nullable
	public GdxExtensionStateEntity getDependenciesLatestVersion() {
		return dependenciesLatestVersion;
	}
	
	public boolean isSupportingStableVersion() {
		return dependenciesStableVersion != null;
	}
	
	public boolean isSupportingLatestVersion() {
		return dependenciesLatestVersion != null;
	}

}
