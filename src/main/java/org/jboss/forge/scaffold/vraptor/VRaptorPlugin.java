package org.jboss.forge.scaffold.vraptor;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.ShellMessages;
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
@RequiresFacet(VRaptorFacet.class)
public class VRaptorPlugin implements Plugin {

    @Inject
    private Project project;

    @Inject
    private Event<InstallFacets> request;

    @SetupCommand
    public void setup(PipeOut out) {
        if (!project.hasFacet(VRaptorFacet.class)) {
            request.fire(new InstallFacets(VRaptorFacet.class));
        }

        if (project.hasFacet(VRaptorFacet.class)) {
            ShellMessages.info(out, "VRaptor libraries were succesfully installed");
        } else {
            ShellMessages.warn(out, "VRaptor libraries were not installed");
        }
    }

    @DefaultCommand
    public void defaultCommand(@PipeIn String in, PipeOut out) {
        out.println("Type 'scaffold setup --scaffoldType vraptor' to setup vraptor in your project");
    }
}
