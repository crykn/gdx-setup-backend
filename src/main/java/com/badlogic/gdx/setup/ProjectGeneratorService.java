package com.badlogic.gdx.setup;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ProjectGeneratorService {

	private static final int KEEP_GENERATED_PROJECTS_TIME = 1000 * 60 * 15; // ms
	private static final int MAX_GEN_CACHE_SIZE = 5;

	@Autowired
	private GdxSetupDataService dataCacheService;
	private ConcurrentHashMap<String, GeneratedProject> generatedFiles = new ConcurrentHashMap<>();

	public String generateAndZipGdxProject(GdxSetupSettings projectSettings) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zipOutputStream = new ZipOutputStream(baos);

		new GdxProject().generateProject(projectSettings, dataCacheService.getTemplateFiles(projectSettings.useLatestGdxVersion), dataCacheService.getOfficialExtensions(),
				dataCacheService.getThirdPartyExtensions(), zipOutputStream);

		// clearGenerationCache();

		String uuid = UUID.randomUUID().toString();
		generatedFiles.put(uuid, new GeneratedProject(baos.toByteArray()));

		return uuid;
	}

	@Scheduled(fixedRate = 1000 * 60 * 5, initialDelay = 10_000)
	public void clearGenerationCache() {
		List<String> uuids = new ArrayList<>(generatedFiles.keySet());

		long timeNow = System.currentTimeMillis();
		long oldestEntryTime = timeNow;
		String oldestEntryUuid = null;

		// remove everything older than 15 minutes
		for (String uuid : uuids) {
			GeneratedProject project = generatedFiles.get(uuid);

			if (project != null) {
				if (timeNow - project.timestamp > KEEP_GENERATED_PROJECTS_TIME)
					generatedFiles.remove(uuid);
				else if (project.timestamp < oldestEntryTime
						&& timeNow - project.timestamp > KEEP_GENERATED_PROJECTS_TIME) {
					oldestEntryUuid = uuid;
					oldestEntryTime = project.timestamp;
				}
			}
		}

		// never more than 5 cached zip files at a time
		if (generatedFiles.size() > MAX_GEN_CACHE_SIZE && oldestEntryUuid != null) {
			generatedFiles.remove(oldestEntryUuid);
		}
	}

	public GeneratedProject getGeneratedProject(String id) {
		return generatedFiles.get(id);
	}

	public static class GeneratedProject {
		public final byte[] zippedContent;
		public final long timestamp;

		public GeneratedProject(byte[] zipFile) {
			this.zippedContent = zipFile;
			timestamp = System.currentTimeMillis();
		}
	}

	public static class GdxSetupSettings {
		public boolean useLatestGdxVersion;
		public boolean withAndroid;
		public boolean withIos;
		public boolean withHtml;
		public boolean withDesktop;

		public List<String> warnings = new LinkedList<>();
	}

}
