/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.scaffold.vraptor;

import javax.inject.Inject;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;

@Alias("facet.vraptor")
@RequiresFacet(DependencyFacet.class)
@RequiresProject
public class VRaptorFacet extends BaseFacet {

    @Inject
    private DependencyInstaller dependencyInstaller;

    private static final Dependency VRAPTOR_DEPENDENCY = DependencyBuilder.create("br.com.caelum:vraptor");

    @Override
    public boolean install() {
        dependencyInstaller.install(getProject(), VRAPTOR_DEPENDENCY);
        return true;
    }

    @Override
    public boolean isInstalled() {
        return dependencyInstaller.isInstalled(getProject(), VRAPTOR_DEPENDENCY);
    }

}
