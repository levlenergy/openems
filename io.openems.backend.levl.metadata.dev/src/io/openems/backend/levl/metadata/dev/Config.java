package io.openems.backend.levl.metadata.dev;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(//
		name = "Levl.Metadata.Dev", //
		description = "Configures the Metadata Dev provider, edge0-edge5 are configured")
@interface Config {

	@AttributeDefinition(name = "Edge-ID template", description = "Template for Edge-IDs, defaults to 'edge%d'")
	String edgeIdTemplate() default "edge%d";

	@AttributeDefinition(name = "Max Edge-ID", description = "Default predefines Edge-IDs from 'edge0' to 'edge10'")
	int edgeIdMax() default 10;

	String webconsole_configurationFactory_nameHint() default "Levl Metadata Dev";

}
