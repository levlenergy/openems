package io.openems.backend.levl.metadata.dev;

import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(//
		name = "Levl.Metadata.Dev", //
		description = "Configures the Metadata Dev provider, edge0-edge5 are configured")
@interface Config {

	String webconsole_configurationFactory_nameHint() default "Levl Metadata Dev";

}
