package com.badlogic.gdx.setup;

import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.badlgoic.gdx.setup.jpa.GdxExtensionEntity;
import com.badlgoic.gdx.setup.jpa.GdxTemplateFileEntity;
import com.badlogic.gdx.setup.ProjectGeneratorService.GdxSetupSettings;

public class GdxProject {

	public void generateProject(GdxSetupSettings projectData, List<GdxTemplateFileEntity> files,
			List<GdxExtensionEntity> officialExtensions, List<GdxExtensionEntity> thirdPartyExtensions,
			ZipOutputStream zipOutputStream) throws Exception {
		// 60 calls per hour is allowed... we have 10 here, but without subdirectories.
		// It is needed to cache, or pull in the contents for released versions.

		for (GdxTemplateFileEntity file : files) {
			zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
			zipOutputStream.write(file.getContent());
			zipOutputStream.closeEntry();
		}

		// this is generated completely dynamical
		zipOutputStream.putNextEntry(new ZipEntry("build.gradle"));
		zipOutputStream.write("FROM MRSTAHLFELGE WITH LOVE".getBytes());
		zipOutputStream.closeEntry();

		zipOutputStream.close();
	}

}
