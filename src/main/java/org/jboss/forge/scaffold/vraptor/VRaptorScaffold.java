package org.jboss.forge.scaffold.vraptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ExcludedDependencyBuilder;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.scaffold.AccessStrategy;
import org.jboss.forge.scaffold.ScaffoldProvider;
import org.jboss.forge.scaffold.TemplateStrategy;
import org.jboss.forge.scaffold.util.ScaffoldUtil;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.seam.render.TemplateCompiler;
import org.jboss.seam.render.spi.TemplateResolver;
import org.jboss.seam.render.template.CompiledTemplateResource;
import org.jboss.seam.render.template.resolver.ClassLoaderTemplateResolver;

@Alias("vraptor")
@Help("VRaptor scaffolding")
@RequiresFacet({
    WebResourceFacet.class,
    DependencyFacet.class
})
public class VRaptorScaffold extends BaseFacet implements ScaffoldProvider {
    
    //
    // Private statics
    //
    
    private static final Dependency VRAPTOR_DEPENDENCY = DependencyBuilder.create("br.com.caelum:vraptor:3.4.1");
    private static final Dependency HIBERNATE_DEPENDENCY = DependencyBuilder.create("org.hibernate:hibernate-entitymanager:4.1.10.Final");
    private static final Dependency VRAPTOR_HIBERNATE_PLUGIN_DEPENDENCY = DependencyBuilder.create("br.com.caelum.vraptor:vraptor-plugin-hibernate4:1.0.0");
    private static final Dependency H2_DEPENDENCY = DependencyBuilder.create("com.h2database:h2:1.3.161");
    private static final Dependency JSTL_DEPENDENCY = DependencyBuilder.create("javax.servlet:jstl:1.2");

    private static final String ERROR_TEMPLATE = "scaffold/vraptor/error.jsp";
    private static final String FOOTER_TEMPLATE = "scaffold/vraptor/footer.jsp";
    private static final String HEADER_TEMPLATE = "scaffold/vraptor/header.jsp";
    private static final String WEB_XML_TEMPLATE = "scaffold/vraptor/web.xml";
    private static final String HIBERNATE_CFG_TEMPLATE = "scaffold/vraptor/hibernate.cfg.xml";
    private static final String INDEX_TEMPLATE = "scaffold/vraptor/index.jsp";
    private static final String INDEX_CONTROLLER_TEMPLATE = "scaffold/vraptor/IndexController.jv";
    
    //
    // Protected members (nothing is private, to help subclassing)
    //
    
    protected final ShellPrompt prompt;
    protected final TemplateCompiler compiler;
    protected final Event<InstallFacets> install;
    protected TemplateResolver<ClassLoader> resolver;
    protected CompiledTemplateResource errorTemplate;
    protected CompiledTemplateResource footerTemplate;
    protected CompiledTemplateResource headerTemplate;
    protected CompiledTemplateResource indexTemplate;
    protected CompiledTemplateResource webXMLTemplate;
    protected CompiledTemplateResource hibernateCfgTemplate;
    protected CompiledTemplateResource indexControllerTemplate;
    private Configuration config;

    //
    // Constructor
    //
    @Inject
    public VRaptorScaffold(final Configuration config,
            final ShellPrompt prompt,
            final TemplateCompiler compiler,
            final Event<InstallFacets> install) {

        this.config = config;
        this.prompt = prompt;
        this.compiler = compiler;
        this.install = install;

        this.resolver = new ClassLoaderTemplateResolver(VRaptorScaffold.class.getClassLoader());

        if (this.compiler != null) {
            this.compiler.getTemplateResolverFactory().addResolver(this.resolver);
        }
    }

    @Override
    public boolean install() {
        return true;
    }

    @Override
    public boolean isInstalled() {
        return true;
    }

    protected HashMap<Object, Object> getTemplateContext(String targetDir, final Resource<?> template) {
        HashMap<Object, Object> context;
        context = new HashMap<Object, Object>();
        context.put("template", template);
        context.put("templateStrategy", getTemplateStrategy());
        context.put("targetDir", targetDir);
        return context;
    }

    protected void loadTemplates() {
        if (this.errorTemplate == null) {
            this.errorTemplate = this.compiler.compile(ERROR_TEMPLATE);
        }
        if (this.footerTemplate == null) {
            this.footerTemplate = this.compiler.compile(FOOTER_TEMPLATE);
        }
        if (this.headerTemplate == null) {
            this.headerTemplate = this.compiler.compile(HEADER_TEMPLATE);
        }
        if (this.indexTemplate == null) {
            this.indexTemplate = this.compiler.compile(INDEX_TEMPLATE);
        }
        if (this.webXMLTemplate == null) {
            this.webXMLTemplate = compiler.compile(WEB_XML_TEMPLATE);
        }
        if (this.hibernateCfgTemplate == null) {
            this.hibernateCfgTemplate = compiler.compile(HIBERNATE_CFG_TEMPLATE);
        }
        if (this.indexControllerTemplate == null) {
            this.indexControllerTemplate = compiler.compile(INDEX_CONTROLLER_TEMPLATE);
        }
    }

    @Override
    public List<Resource<?>> generateIndex(String targetDir, Resource<?> template, boolean overwrite) {

        List<Resource<?>> result = new ArrayList<Resource<?>>();
        WebResourceFacet web = project.getFacet(WebResourceFacet.class);

        loadTemplates();

        HashMap<Object, Object> context = getTemplateContext(targetDir, template);

        // Basic pages

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("error.jsp"),
                this.errorTemplate.render(context), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("footer.jsp"),
                this.footerTemplate.render(context), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("header.jsp"),
                this.headerTemplate.render(context), overwrite));
        
        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/WEB-INF/jsp/index/index.jsp"),
                this.indexTemplate.render(context), overwrite));

        // Static resources

        final String STATIC_RESOURCES_DIR = "/scaffold/vraptor";

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/add.png"),
                getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/add.png"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/bootstrap.css"),
                getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/bootstrap.css"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/false.png"),
                getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/false.png"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/favicon.ico"),
                getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/favicon.ico"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/forge-logo.png"),
                getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/forge-logo.png"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/forge-style.css"),
                getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/forge-style.css"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/jboss-community.png"),
                getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/jboss-community.png"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/remove.png"),
                getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/remove.png"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/search.png"),
                getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/search.png"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/true.png"),
                getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/true.png"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/vraptor-logo.png"),
                getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/vraptor-logo.png"), overwrite));

        // web.xml

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("WEB-INF/web.xml"),
                this.webXMLTemplate.render(context), overwrite));
        
        // hibernate.cfg.xml
        
        ResourceFacet resourceFacet = project.getFacet(ResourceFacet.class);
        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, resourceFacet.getResource("hibernate.cfg.xml"),
                this.hibernateCfgTemplate.render(context), overwrite));

        return result;
    }

    protected Node removeConflictingErrorPages(final ServletFacet servlet) {
        Node webXML = XMLParser.parse(servlet.getConfigFile().getResourceInputStream());
        Node root = webXML.getRoot();
        List<Node> errorPages = root.get("error-page");

        for (String code : Arrays.asList("404", "500")) {
            for (Node errorPage : errorPages) {
                if (code.equals(errorPage.getSingle("error-code").getText())
                        && this.prompt.promptBoolean("Your web.xml already contains an error page for " + code + " status codes, replace it?")) {
                    root.removeChild(errorPage);
                }
            }
        }
        return webXML;
    }
    
    private List<Resource<?>> generateIndexController(String targetDir, Resource<?> template, boolean overwrite) {
        
        List<Resource<?>> result = new ArrayList<Resource<?>>();
        HashMap<Object, Object> context = getTemplateContext(targetDir, template);

        try {
            JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

            JavaClass indexController = JavaParser.parse(JavaClass.class, this.indexControllerTemplate.render(context));
            java.saveJavaSource(indexController);
            result.add(ScaffoldUtil.createOrOverwrite(this.prompt, java.getJavaResource(indexController),
                    indexController.toString(), overwrite));

        } catch (Exception e) {
            throw new RuntimeException("Error generating VRaptor scaffolding: IndexController", e);
        }
        
        return result;
    }
    
    private void addMavenDependencies() {
        
        DependencyFacet deps = project.getFacet(DependencyFacet.class);
        
        VRAPTOR_HIBERNATE_PLUGIN_DEPENDENCY.getExcludedDependencies()
              .add(DependencyBuilder.create("org.hibernate:hibernate-annotations:"));
        VRAPTOR_HIBERNATE_PLUGIN_DEPENDENCY.getExcludedDependencies()
              .add(DependencyBuilder.create("org.hibernate:hibernate-entitymanager:"));
        
        deps.addDirectDependency(VRAPTOR_HIBERNATE_PLUGIN_DEPENDENCY);
        deps.addDirectDependency(VRAPTOR_DEPENDENCY);
        deps.addDirectDependency(H2_DEPENDENCY);
    }
    
    @Override
    public List<Resource<?>> setup(String targetDir, Resource<?> template, boolean overwrite) {
        
        addMavenDependencies();
        
        List<Resource<?>> resources = new ArrayList<Resource<?>>();
        
        resources.addAll(generateIndex(targetDir, template, overwrite));
        resources.addAll(generateIndexController(targetDir, template, overwrite));
        
        return resources;
    }

    @Override
    public TemplateStrategy getTemplateStrategy() {
        return new VRaptorTemplateStrategy(this.project);
    }

    @Override
    public AccessStrategy getAccessStrategy() {
        return null;
    }

    @Override
    public List<Resource<?>> generateTemplates(String targetDir, boolean overwrite) {
        throw new UnsupportedOperationException("generateTemplates - Not supported yet.");
    }

    @Override
    public List<Resource<?>> generateFromEntity(String targetDir, Resource<?> template, JavaClass entity, boolean overwrite) {
        throw new UnsupportedOperationException("generateFromEntity - Not supported yet.");
    }

    @Override
    public List<Resource<?>> getGeneratedResources(String targetDir) {
        throw new UnsupportedOperationException("getGeneratedResources - Not supported yet.");
    }
}
