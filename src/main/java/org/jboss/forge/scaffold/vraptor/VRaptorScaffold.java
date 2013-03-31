package org.jboss.forge.scaffold.vraptor;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
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
import org.jboss.forge.scaffold.vraptor.metawidget.config.ForgeConfigReader;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.util.Streams;
import org.jboss.forge.spec.javaee.PersistenceFacet;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.seam.render.TemplateCompiler;
import org.jboss.seam.render.spi.TemplateResolver;
import org.jboss.seam.render.template.CompiledTemplateResource;
import org.jboss.seam.render.template.resolver.ClassLoaderTemplateResolver;
import org.metawidget.statically.javacode.StaticJavaMetawidget;
import org.metawidget.util.CollectionUtils;
import org.metawidget.util.simple.StringUtils;

@Alias("vraptor")
@Help("VRaptor scaffolding")
@RequiresFacet({WebResourceFacet.class, DependencyFacet.class, PersistenceFacet.class})
public class VRaptorScaffold extends BaseFacet implements ScaffoldProvider {

    //
    // Private statics
    //
    private static final Dependency VRAPTOR_DEPENDENCY = DependencyBuilder.create("br.com.caelum:vraptor:3.4.1");
    private static final Dependency HSQLDB_DEPENDENCY = DependencyBuilder.create("hsqldb:hsqldb:1.8.0.10");
    private static final Dependency HIBERNATE_DEPENDENCY = DependencyBuilder.create("org.hibernate:hibernate-entitymanager:3.6.6.Final:provided");
    private static final String ERROR_TEMPLATE = "scaffold/vraptor/error.jsp";
    private static final String FOOTER_TEMPLATE = "scaffold/vraptor/footer.jsp";
    private static final String HEADER_TEMPLATE = "scaffold/vraptor/header.jsp";
    private static final String WEB_XML_TEMPLATE = "scaffold/vraptor/web.xml";
    private static final String INDEX_TEMPLATE = "scaffold/vraptor/index.jsp";
    private static final String INDEX_CONTROLLER_TEMPLATE = "scaffold/vraptor/IndexController.jv";
    private static final String CONTROLLER_TEMPLATE = "scaffold/vraptor/Controller.jv";
    
    //
    // Protected members (nothing is private, to help subclassing)
    //
    protected final ShellPrompt prompt;
    protected final TemplateCompiler compiler;
    protected final Event<InstallFacets> install;
    protected int navigationTemplateIndent;
    protected TemplateResolver<ClassLoader> resolver;
    protected CompiledTemplateResource errorTemplate;
    protected CompiledTemplateResource footerTemplate;
    protected CompiledTemplateResource headerTemplate;
    protected CompiledTemplateResource indexTemplate;
    protected CompiledTemplateResource webXMLTemplate;
    protected CompiledTemplateResource indexControllerTemplate;
    protected CompiledTemplateResource controllerTemplate;
    protected int controllerTemplateQbeMetawidgetIndent;
    protected StaticJavaMetawidget qbeMetawidget;
    
    private Configuration config;

    //
    // Constructor
    //
    @Inject
    public VRaptorScaffold(final Configuration config, final ShellPrompt prompt, final TemplateCompiler compiler, final Event<InstallFacets> install) {

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
    
    /**
     * Parses the given XML and determines the indent of the given String
     * namespaces that Metawidget introduces.
     */
    protected int parseIndent(final String template, final String indentOf) {
        int indent = 0;
        int indexOf = template.indexOf(indentOf);

        while ((indexOf >= 0) && (template.charAt(indexOf) != '\n')) {
            if (template.charAt(indexOf) == '\t') {
                indent++;
            }

            indexOf--;
        }

        return indent;
    }

    protected void loadTemplates() {
        if (this.controllerTemplate == null) {
            this.controllerTemplate = compiler.compile(CONTROLLER_TEMPLATE);
            String template = Streams.toString(this.controllerTemplate.getSourceTemplateResource().getInputStream());
            this.controllerTemplateQbeMetawidgetIndent = parseIndent(template, "@{qbeMetawidget}");
        }
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
        if (this.indexControllerTemplate == null) {
            this.indexControllerTemplate = compiler.compile(INDEX_CONTROLLER_TEMPLATE);
        }
    }

    @Override
    public List<Resource<?>> generateIndex(String targetDir, Resource<?> template, boolean overwrite) {

        List<Resource<?>> result = new ArrayList<Resource<?>>();
        WebResourceFacet web = project.getFacet(WebResourceFacet.class);

        loadTemplates();
        generateTemplates(targetDir, overwrite);

        HashMap<Object, Object> context = getTemplateContext(targetDir, template);

        // Basic pages

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("error.jsp"), this.errorTemplate.render(context), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("footer.jsp"), this.footerTemplate.render(context), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/WEB-INF/jsp/index/index.jsp"), this.indexTemplate.render(context), overwrite));

        // Static resources

        final String STATIC_RESOURCES_DIR = "/scaffold/vraptor";

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/add.png"), getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/add.png"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/bootstrap.css"), getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/bootstrap.css"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/false.png"), getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/false.png"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/favicon.ico"), getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/favicon.ico"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/forge-logo.png"), getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/forge-logo.png"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/forge-style.css"), getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/forge-style.css"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/jboss-community.png"), getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/jboss-community.png"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/remove.png"), getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/remove.png"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/search.png"), getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/search.png"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/true.png"), getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/true.png"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/vraptor-logo.png"), getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/vraptor-logo.png"), overwrite));

        // web.xml

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("WEB-INF/web.xml"), this.webXMLTemplate.render(context), overwrite));

        return result;
    }

    protected Node removeConflictingErrorPages(final ServletFacet servlet) {
        Node webXML = XMLParser.parse(servlet.getConfigFile().getResourceInputStream());
        Node root = webXML.getRoot();
        List<Node> errorPages = root.get("error-page");

        for (String code : Arrays.asList("404", "500")) {
            for (Node errorPage : errorPages) {
                if (code.equals(errorPage.getSingle("error-code").getText()) && this.prompt.promptBoolean("Your web.xml already contains an error page for " + code + " status codes, replace it?")) {
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
            indexController.setPackage(java.getBasePackage() + ".view");
            result.add(ScaffoldUtil.createOrOverwrite(this.prompt, java.getJavaResource(indexController), indexController.toString(), overwrite));

        } catch (Exception e) {
            throw new RuntimeException("Error generating VRaptor scaffolding: IndexController", e);
        }

        return result;
    }

    private List<Resource<?>> addStaticScanningMavenPlugin() {

        List<Resource<?>> result = new ArrayList<Resource<?>>();
        WebResourceFacet web = project.getFacet(WebResourceFacet.class);
        ResourceFacet resourceFacet = project.getFacet(ResourceFacet.class);
        Node pom = XMLParser.parse(resourceFacet.getResource("../../../pom.xml").getResourceInputStream());

        Node plugin = pom.getSingle("build").getSingle("plugins").createChild("plugin");
        plugin.createChild("groupId").text("org.apache.maven.plugins");
        plugin.createChild("artifactId").text("maven-antrun-plugin");
        plugin.createChild("version").text("1.7");

        Node execution = plugin.createChild("executions").createChild("execution");
        execution.createChild("id").text("static-scanning");
        execution.createChild("phase").text("package");
        execution.createChild("configuration").createChild("target")
            .createChild("path").attribute("id", "build.classpath")
            .createChild("fileset")
                .attribute("dir", "${project.build.directory}/${project.build.finalName}/WEB-INF/lib")
                .attribute("includes", "*.jar")
                .getParent()
            .getParent()
            .createChild("java")
                .attribute("classpathref", "build.classpath")
                .attribute("classname", "br.com.caelum.vraptor.scan.VRaptorStaticScanning")
                .attribute("fork", "true")
                .createChild("arg").attribute("value", "${project.build.directory}/${project.build.finalName}/WEB-INF/web.xml")
                .getParent()
                .createChild("classpath").attribute("refid", "build.classpath")
                .getParent()
                .createChild("classpath").attribute("path", "${project.build.directory}/${project.build.finalName}/WEB-INF/classes")
                .getParent()
            .getParent()
            .createChild("war")
                .attribute("destfile", "${project.build.directory}/${project.build.finalName}.war")
                .attribute("webxml", "${project.build.directory}/${project.build.finalName}/WEB-INF/web.xml")
                .createChild("fileset").attribute("dir", "${project.build.directory}/${project.build.finalName}");
        execution.createChild("goals")
            .createChild("goal").text("run");

        result.add(web.createWebResource(XMLParser.toXMLString(pom), "../../../pom.xml"));

        return result;
    }

    private List<Resource<?>> addMavenDependencies() {

        DependencyFacet deps = project.getFacet(DependencyFacet.class);
        
        deps.addDirectDependency(HIBERNATE_DEPENDENCY);
        deps.addDirectDependency(VRAPTOR_DEPENDENCY);
        deps.addDirectDependency(HSQLDB_DEPENDENCY);

        return new ArrayList<Resource<?>>();
    }

    @Override
    public List<Resource<?>> setup(String targetDir, Resource<?> template, boolean overwrite) {
        
        List<Resource<?>> resources = new ArrayList<Resource<?>>();

        resources.addAll(generateIndex(targetDir, template, overwrite));
        resources.addAll(generateIndexController(targetDir, template, overwrite));
        resources.addAll(addMavenDependencies());
        resources.addAll(addStaticScanningMavenPlugin());

        return resources;
    }

    /**
     * Overridden to setup the Metawidgets.
     * <p>
     * Metawidgets must be configured per project <em>and per Forge
     * invocation</em>. It is not sufficient to simply configure them in
     * <code>setup</code> because the user may restart Forge and not run
     * <code>scaffold setup</code> a second time.
     */
    @Override
    public void setProject(Project project) {
        
        super.setProject(project);
        
        resetMetaWidgets();
    }

    private void resetMetaWidgets() {
        ForgeConfigReader configReader = new ForgeConfigReader(this.config, this.project);

//      this.entityMetawidget = new StaticHtmlMetawidget();
//      this.entityMetawidget.setConfigReader(configReader);
//      this.entityMetawidget.setConfig("scaffold/vraptor/metawidget-entity.xml");
//
//      this.searchMetawidget = new StaticHtmlMetawidget();
//      this.searchMetawidget.setConfigReader(configReader);
//      this.searchMetawidget.setConfig("scaffold/vraptor/metawidget-search.xml");
//
//      this.beanMetawidget = new StaticHtmlMetawidget();
//      this.beanMetawidget.setConfigReader(configReader);
//      this.beanMetawidget.setConfig("scaffold/vraptor/metawidget-bean.xml");

        this.qbeMetawidget = new StaticJavaMetawidget();
        this.qbeMetawidget.setConfigReader(configReader);
        this.qbeMetawidget.setConfig("/scaffold/vraptor/metawidget-qbe.xml");
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
        List<Resource<?>> result = new ArrayList<Resource<?>>();

        Map<Object, Object> context = CollectionUtils.newHashMap();
        context.put("appName", StringUtils.uncamelCase(this.project.getProjectRoot().getName()));
        context.put("targetDir", targetDir);

        try {
            WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);
            result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("header.jsp"), this.headerTemplate.render(context), overwrite));

        } catch (Exception e) {
            throw new RuntimeException("Error generating default templates", e);
        }

        return result;
    }

    @Override
    public List<Resource<?>> generateFromEntity(String targetDir, final Resource<?> template, final JavaClass entity, boolean overwrite) {
        
        resetMetaWidgets();
        
        List<Resource<?>> result = new ArrayList<Resource<?>>();
        try {
            JavaSourceFacet java = this.project.getFacet(JavaSourceFacet.class);
            ResourceFacet resourceFacet = project.getFacet(ResourceFacet.class);

            loadTemplates();
            
            // add class mapping to persistence.xml
            Node persistenceXml = XMLParser.parse(resourceFacet.getResource("META-INF/persistence.xml").getResourceInputStream());
            Node unit = persistenceXml.getSingle("persistence-unit");
            unit.createChild("class").text(entity.getQualifiedName());
            
            // context
            Map<Object, Object> context = CollectionUtils.newHashMap();
            context.put("entity", entity);
            String ccEntity = StringUtils.decapitalize(entity.getName());
            context.put("ccEntity", ccEntity);
            
            // Prepare qbeMetawidget
            this.qbeMetawidget.setPath(entity.getQualifiedName());
            StringWriter stringWriter = new StringWriter();
            this.qbeMetawidget.write(stringWriter, this.controllerTemplateQbeMetawidgetIndent);
            context.put("qbeMetawidget", stringWriter.toString().trim());
            
            //Set<String> qbeMetawidgetImports = this.qbeMetawidget.getImports();
            //qbeMetawidgetImports.remove(entity.getQualifiedName());
            //context.put("qbeMetawidgetImports", CollectionUtils.toString(qbeMetawidgetImports, ";\r\nimport ", true, false));

            // Create the Controller for this entity
            JavaClass controller = JavaParser.parse(JavaClass.class, this.controllerTemplate.render(context));
            controller.setPackage(java.getBasePackage() + ".view");
            result.add(ScaffoldUtil.createOrOverwrite(this.prompt, java.getJavaResource(controller), controller.toString(), overwrite));

        } catch (Exception e) {
            throw new RuntimeException("Error generating vraptor scaffolding: " + e.getMessage(), e);
        }

        return result;
    }

    @Override
    public List<Resource<?>> getGeneratedResources(String targetDir) {
        throw new UnsupportedOperationException("getGeneratedResources - Not supported yet.");
    }
}