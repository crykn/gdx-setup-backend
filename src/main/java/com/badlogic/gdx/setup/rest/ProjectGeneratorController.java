package com.badlogic.gdx.setup.rest;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.badlogic.gdx.setup.ProjectGeneratorService;
import com.badlogic.gdx.setup.ProjectGeneratorService.GdxSetupSettings;
import com.badlogic.gdx.setup.ProjectGeneratorService.GeneratedProject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@RestController
@CrossOrigin
public class ProjectGeneratorController {
	private final ProjectGeneratorService service;

	public ProjectGeneratorController(ProjectGeneratorService service) {
		this.service = service;
	}

	/**
	 * Used to download the actual project files.
	 */
	@GetMapping("/download/{id}")
	public ResponseEntity<Resource> downloadZipFile(@PathVariable String id) {
		GeneratedProject requestedProject = service.getGeneratedProject(id);

		if (requestedProject == null)
			throw new NotFoundException("Project not found");

		ByteArrayResource bar = new ByteArrayResource(requestedProject.zippedContent);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "libgdx_project" + ".zip" + "\"")
				.body(bar);
	}

	/**
	 * Triggers the generation of project files and caches them.
	 */
	@GetMapping("/generate")
	public GeneratorResponse generateProject(@RequestParam boolean latestGdxVersion,
			@RequestParam(defaultValue = "false") boolean withHtml
	// add everything needed here...
	) {
		GdxSetupSettings projectData = new GdxSetupSettings();
		projectData.useLatestGdxVersion = latestGdxVersion;
		projectData.withHtml = withHtml;
		// ...

		GeneratorResponse response = new GeneratorResponse();
		try {
			String zipFileId = service.generateAndZipGdxProject(projectData);
			response.downloadUrl = zipFileId;
			response.warnings = projectData.warnings.toArray(new String[0]);
		} catch (Throwable t) {
			response.errorMessage = t.getMessage();
		}

		return response;
	}

	@JsonInclude(Include.NON_NULL)
	public static class GeneratorResponse {
		public String downloadUrl;
		public String errorMessage;
		public String[] warnings;
	}
}
