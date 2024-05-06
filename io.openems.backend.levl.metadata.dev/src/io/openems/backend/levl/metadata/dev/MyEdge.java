package io.openems.backend.levl.metadata.dev;

import io.openems.backend.common.metadata.Edge;

public class MyEdge extends Edge {

	private final String apikey;
	private final String setupPassword;

	public MyEdge(LevlMetadataDummy parent, String id, String apikey, String setupPassword, String comment, String version,
                  String producttype) {
		super(parent, id, comment, version, producttype, null);
		this.apikey = apikey;
		this.setupPassword = setupPassword;
	}

	public String getApikey() {
		return this.apikey;
	}

	public String getSetupPassword() {
		return this.setupPassword;
	}
}