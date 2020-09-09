package com.badlogic.gdx.setup.rest;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.badlgoic.gdx.setup.jpa.GdxExtensionEntity;
import com.badlogic.gdx.setup.GdxSetupDataService;
import com.badlogic.gdx.setup.GdxSetupDataService.ConfigJson;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@RestController
@CrossOrigin
public class GdxSetupDataController {
	private final GdxSetupDataService service;

	public GdxSetupDataController(GdxSetupDataService service) {
		this.service = service;
	}

	/**
	 * Serves a list of all settings & data needed to setup the frontend.
	 */
	@GetMapping("/data")
	public SetupConfigResponse generateProject() {
		SetupConfigResponse response = new SetupConfigResponse();

		ConfigJson cfg = service.getConfig();
		response.latestGdxVersion = cfg.getLatestGdxVersion();
		response.stableGdxVersion = cfg.getStableGdxVersion();
		response.officialExtensions = service.getOfficialExtensions();
		response.thirdPartyExtensions = service.getThirdPartyExtensions();

		return response;
	}

	@JsonInclude(Include.NON_NULL)
	public static class SetupConfigResponse {
		public String latestGdxVersion;
		public String stableGdxVersion;
		public ArrayList<GdxExtensionEntity> officialExtensions;
		public ArrayList<GdxExtensionEntity> thirdPartyExtensions;

		public String errorMessage;
	}
}
