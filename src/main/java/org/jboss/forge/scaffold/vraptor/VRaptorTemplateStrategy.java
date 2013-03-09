package org.jboss.forge.scaffold.vraptor;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.scaffold.TemplateStrategy;
import org.jboss.forge.shell.util.ResourceUtil;

public class VRaptorTemplateStrategy implements TemplateStrategy {

    private static final String SCAFFOLD_FORGE_TEMPLATE = "/resources/scaffold/pageTemplate.xhtml";
    private final Project project;

    public VRaptorTemplateStrategy(final Project project) {
        this.project = project;
    }

    @Override
    public boolean compatibleWith(final Resource<?> template) {
        return true;
    }

    @Override
    public String getReferencePath(final Resource<?> template) {
        WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);
        for (DirectoryResource dir : web.getWebRootDirectories()) {
            if (ResourceUtil.isChildOf(dir, template)) {
                String relativePath = template.getFullyQualifiedName().substring(dir.getFullyQualifiedName().length());
                return relativePath;
            }
        }
        throw new IllegalArgumentException("Not a valid template resource for this scaffold.");
    }

    @Override
    public FileResource<?> getDefaultTemplate() {
        WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);
        return web.getWebResource(SCAFFOLD_FORGE_TEMPLATE);
    }
}
