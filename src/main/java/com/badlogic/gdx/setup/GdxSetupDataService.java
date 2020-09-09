package com.badlogic.gdx.setup;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.PagedIterable;
import org.kohsuke.github.RateLimitHandler;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.badlgoic.gdx.setup.jpa.GdxExtensionDependenciesEntity;
import com.badlgoic.gdx.setup.jpa.GdxExtensionEntity;
import com.badlgoic.gdx.setup.jpa.GdxTemplateFileEntity;
import com.badlogic.gdx.setup.rest.NotFoundException;
import com.badlogic.gdx.setup.util.SimpleJSONParser;
import com.google.gson.JsonSyntaxException;

@Service
public class GdxSetupDataService {

	private static final String EXTENSION_FILE_NAME = "libgdx_extension.json";
	private static final int DATA_RECREATION_INTERVAL = 1000 * 60 * 60 * 24; // ms

	private Resource configResource = new ClassPathResource("config.json");
	private SimpleJSONParser json = new SimpleJSONParser();

	@Scheduled(fixedRate = DATA_RECREATION_INTERVAL, initialDelay = 10000)
	public void recreateCache() throws IOException {
		GitHub githubClient = new GitHubBuilder().withRateLimitHandler(RateLimitHandler.FAIL).build();
		ConfigJson cfg = getConfig();
		/*
		 * TEMPLATE FILES
		 */
		List<GdxTemplateFileEntity> templateFilesStableVersion = fetchTemplateFiles(githubClient,
				cfg.getStableGdxVersion());
		List<GdxTemplateFileEntity> templateFilesLatestVersion = fetchTemplateFiles(githubClient,
				cfg.getLatestGdxVersion());

		// TODO remove old entries in DB; save new ones

		/*
		 * EXTENSIONS
		 */
		List<GdxExtensionEntity> officialExtensions = new ArrayList<>();
		List<GdxExtensionEntity> thirdPartyExtensions = new ArrayList<>();

		for (String s : cfg.getOfficialExtensionRepos()) {
			officialExtensions
					.add(fetchExtension(githubClient, s, cfg.getStableGdxVersion(), cfg.getLatestGdxVersion()));
		}

		for (String s : cfg.getThirdPartyExtensionRepos()) {
			thirdPartyExtensions
					.add(fetchExtension(githubClient, s, cfg.getStableGdxVersion(), cfg.getLatestGdxVersion()));
		}

		// TODO remove old entries in DB; save new ones
	}

	private List<GdxTemplateFileEntity> fetchTemplateFiles(GitHub githubClient, String version) throws IOException {
		List<GdxTemplateFileEntity> files;
		GHRepository libgdxRepo = githubClient.getRepository("libgdx/libgdx");
		PagedIterable<GHRelease> releaseList = libgdxRepo.listReleases();
		GHRelease targetRelease = null;

		for (GHRelease release : releaseList) {
			if (version.equalsIgnoreCase(release.getName())) {
				targetRelease = release;
				break;
			}
		}

		if (targetRelease == null)
			throw new NotFoundException("No release with name " + version + " found");

		files = new LinkedList<>();

		// ok, we have the release - list its files
		List<GHContent> dirContent = libgdxRepo.getDirectoryContent(
				"extensions/gdx-setup/res/com/badlogic/gdx/setup/resources", targetRelease.getTagName());
		for (GHContent content : dirContent) {
			if (content.getDownloadUrl() != null) {
				try (BufferedInputStream in = new BufferedInputStream(new URL(content.getDownloadUrl()).openStream())) {
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
					int nRead;
					byte[] data = new byte[1024];
					while ((nRead = in.read(data, 0, data.length)) != -1) {
						buffer.write(data, 0, nRead);
					}

					buffer.flush();
					files.add(new GdxTemplateFileEntity(content.getName(), buffer.toByteArray()));
				} catch (IOException e) {
					// handle exception
				}
			}
		}
		return files;
	}

	public GdxExtensionEntity fetchExtension(GitHub githubClient, String repoName, String stableVersion,
			String latestVersion) throws IOException {
		GHRepository extensionRepo = githubClient.getRepository(repoName);
		GdxExtensionJson extJson = json.fromJson(
				downloadExtensionFile(extensionRepo, extensionRepo.getBranch("gdx_extension").getSHA1()),
				GdxExtensionJson.class);
		GdxExtensionDependenciesEntity stableDep = null;
		GdxExtensionDependenciesEntity latestDep = null;

		if (extJson.dependencies != null) {
			stableDep = extJson.dependencies.get(stableVersion);
			latestDep = extJson.dependencies.get(latestVersion);
		}

		GdxExtensionEntity ext = new GdxExtensionEntity(extJson.name, extJson.author, extJson.description,
				extJson.projectUrl, stableDep, latestDep);

		return ext;
	}

	public byte[] downloadExtensionFile(GHRepository extensionRepo, @Nullable String ref) throws IOException {
		GHContent extensionFileContent = extensionRepo.getFileContent(EXTENSION_FILE_NAME, ref);

		byte[] fileContent = null;
		try (BufferedInputStream in = new BufferedInputStream(
				new URL(extensionFileContent.getDownloadUrl()).openStream())) {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[1024];
			while ((nRead = in.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}

			buffer.flush();
			fileContent = buffer.toByteArray();
		} catch (IOException e) {
			// do nothing
		}

		return fileContent;
	}

	public ArrayList<GdxTemplateFileEntity> getTemplateFiles(boolean latestVersion) {
		// TODO return DB entries
		return null;
	}

	public ArrayList<GdxExtensionEntity> getOfficialExtensions() {
		// TODO return DB entries
		return null;
	}

	public ArrayList<GdxExtensionEntity> getThirdPartyExtensions() {
		// TODO return DB entries
		return null;
	}

	@Nullable
	public ConfigJson getConfig() {
		try {
			ConfigJson config = json.fromJson(Files.readAllBytes(configResource.getFile().toPath()), ConfigJson.class);
			return config;
		} catch (JsonSyntaxException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public class ConfigJson {
		private String stableGdxVersion;
		private String latestGdxVersion;

		private ArrayList<String> officialRepos = new ArrayList<>();
		private ArrayList<String> thirdPartyRepos = new ArrayList<>();

		public String getStableGdxVersion() {
			return stableGdxVersion;
		}

		public String getLatestGdxVersion() {
			return latestGdxVersion;
		}

		public ArrayList<String> getOfficialExtensionRepos() {
			return officialRepos;
		}

		public ArrayList<String> getThirdPartyExtensionRepos() {
			return thirdPartyRepos;
		}
	}

	public class GdxExtensionJson {
		private String name;
		private String author;
		private String description;
		private String projectUrl;

		private int formatVersion;

		private HashMap<String, GdxExtensionDependenciesEntity> dependencies;

		public int getFormatVersion() {
			return formatVersion;
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

		public HashMap<String, GdxExtensionDependenciesEntity> getDependencies() {
			return dependencies;
		}
	}

}
