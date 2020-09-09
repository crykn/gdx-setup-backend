package com.badlgoic.gdx.setup.jpa;

public class GdxTemplateFileEntity {
	private String name;
	private byte[] content;

	public GdxTemplateFileEntity(String name, byte[] content) {
		this.name = name;
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public byte[] getContent() {
		return content;
	}

}
