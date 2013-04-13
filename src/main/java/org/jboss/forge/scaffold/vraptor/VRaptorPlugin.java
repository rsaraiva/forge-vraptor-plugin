package org.jboss.forge.scaffold.vraptor;

import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.PipeIn;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.SetupCommand;

/**
 * @author <a href="mailto:rubens.saraiva@gmail.com">Rubens Saraiva</a>
 */
@Alias("vraptor")
@RequiresProject
@RequiresFacet(DependencyFacet.class)
public class VRaptorPlugin implements Plugin {

	@Inject
	private Project project;

	@Inject
	private DependencyInstaller dependencyInstaller;
	
	@SetupCommand
	public void setup() {
	}

	@DefaultCommand
	public void defaultCommand(@PipeIn String in, PipeOut out) {
		out.println("Type 'scaffold setup --scaffoldType vraptor' to setup vraptor in your project");
	}
}
