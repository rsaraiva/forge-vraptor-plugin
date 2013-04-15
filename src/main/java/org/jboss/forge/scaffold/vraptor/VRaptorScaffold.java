package org.jboss.forge.scaffold.vraptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.parser.xml.XMLParserException;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.scaffold.AccessStrategy;
import org.jboss.forge.scaffold.ScaffoldProvider;
import org.jboss.forge.scaffold.TemplateStrategy;
import org.jboss.forge.scaffold.util.ScaffoldUtil;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.spec.javaee.PersistenceFacet;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.seam.render.TemplateCompiler;
import org.jboss.seam.render.spi.TemplateResolver;
import org.jboss.seam.render.template.CompiledTemplateResource;
import org.jboss.seam.render.template.resolver.ClassLoaderTemplateResolver;
import org.metawidget.util.CollectionUtils;
import org.metawidget.util.simple.StringUtils;

/**
 * @author <a href="mailto:rubens.saraiva@gmail.com">Rubens Saraiva</a>
 */
@Alias("vraptor")
@Help("VRaptor scaffolding")
@RequiresFacet({ VRaptorFacet.class, WebResourceFacet.class, DependencyFacet.class, PersistenceFacet.class })
public class VRaptorScaffold extends BaseFacet implements ScaffoldProvider {

    //
    // Private statics
    //
    private static final Dependency HSQLDB_DEPENDENCY = DependencyBuilder.create("hsqldb:hsqldb:1.8.0.10");
    private static final Dependency HIBERNATE_DEPENDENCY = DependencyBuilder
        .create("org.hibernate:hibernate-entitymanager:3.6.6.Final:provided");
    private static final String ERROR_TEMPLATE = "scaffold/vraptor/error.jsp";
    private static final String FOOTER_TEMPLATE = "scaffold/vraptor/footer.jsp";
    private static final String HEADER_TEMPLATE = "scaffold/vraptor/header.jsp";
    private static final String WEB_XML_TEMPLATE = "scaffold/vraptor/web.xml";
    private static final String INDEX_TEMPLATE = "scaffold/vraptor/index.jsp";
    private static final String INDEX_CONTROLLER_TEMPLATE = "scaffold/vraptor/IndexController.jv";
    private static final String CONTROLLER_TEMPLATE = "scaffold/vraptor/Controller.jv";
    private static final String EM_PROVIDER_TEMPLATE = "scaffold/vraptor/EntityManagerProvider.jv";
    private static final String SEARCH_TEMPLATE = "scaffold/vraptor/search.jsp";
    private static final String CREATE_TEMPLATE = "scaffold/vraptor/create.jsp";
    private static final String EDIT_TEMPLATE = "scaffold/vraptor/edit.jsp";
    private static final String VIEW_TEMPLATE = "scaffold/vraptor/view.jsp";

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
    protected CompiledTemplateResource emProviderTemplate;
    protected CompiledTemplateResource searchTemplate;
    protected CompiledTemplateResource createTemplate;
    protected CompiledTemplateResource editTemplate;
    protected CompiledTemplateResource viewTemplate;

    //
    // Constructor
    //
    @Inject
    public VRaptorScaffold(final ShellPrompt prompt, final TemplateCompiler compiler, final Event<InstallFacets> install) {

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

        if (this.controllerTemplate == null) {
            this.controllerTemplate = compiler.compile(CONTROLLER_TEMPLATE);
        }

        if (this.searchTemplate == null) {
            this.searchTemplate = this.compiler.compile(SEARCH_TEMPLATE);
        }

        if (this.createTemplate == null) {
            this.createTemplate = this.compiler.compile(CREATE_TEMPLATE);
        }

        if (this.editTemplate == null) {
            this.editTemplate = this.compiler.compile(EDIT_TEMPLATE);
        }

        if (this.viewTemplate == null) {
            this.viewTemplate = this.compiler.compile(VIEW_TEMPLATE);
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

        if (this.emProviderTemplate == null) {
            this.emProviderTemplate = compiler.compile(EM_PROVIDER_TEMPLATE);
        }
    }

    @Override
    public List<Resource<?>> generateIndex(String targetDir, Resource<?> template, boolean overwrite) {

        List<Resource<?>> result = new ArrayList<Resource<?>>();
        WebResourceFacet web = project.getFacet(WebResourceFacet.class);

        loadTemplates();

        // generateTemplates(targetDir, overwrite);

        HashMap<Object, Object> context = getTemplateContext(targetDir, template);

        // Basic pages

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("error.jsp"),
            this.errorTemplate.render(context), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("footer.jsp"),
            this.footerTemplate.render(context), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/WEB-INF/jsp/index/index.jsp"),
            this.indexTemplate.render(context), overwrite));

        // Static resources

        final String STATIC_RESOURCES_DIR = "/scaffold/vraptor";

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/add.png"), getClass()
            .getResourceAsStream(STATIC_RESOURCES_DIR + "/add.png"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/bootstrap.css"),
            getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/bootstrap.css"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/false.png"), getClass()
            .getResourceAsStream(STATIC_RESOURCES_DIR + "/false.png"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/favicon.ico"), getClass()
            .getResourceAsStream(STATIC_RESOURCES_DIR + "/favicon.ico"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/forge-logo.png"),
            getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/forge-logo.png"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/forge-style.css"),
            getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/forge-style.css"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/jboss-community.png"),
            getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/jboss-community.png"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/remove.png"), getClass()
            .getResourceAsStream(STATIC_RESOURCES_DIR + "/remove.png"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/search.png"), getClass()
            .getResourceAsStream(STATIC_RESOURCES_DIR + "/search.png"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/true.png"), getClass()
            .getResourceAsStream(STATIC_RESOURCES_DIR + "/true.png"), overwrite));

        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/vraptor-logo.png"),
            getClass().getResourceAsStream(STATIC_RESOURCES_DIR + "/vraptor-logo.png"), overwrite));

        // web.xml
        JavaSourceFacet java = this.project.getFacet(JavaSourceFacet.class);
        context.put("basePackage", java.getBasePackage());
        result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("WEB-INF/web.xml"),
            this.webXMLTemplate.render(context), overwrite));

        return result;
    }

    protected Node removeConflictingErrorPages(final ServletFacet servlet) {
        Node webXML = XMLParser.parse(servlet.getConfigFile().getResourceInputStream());
        Node root = webXML.getRoot();
        List<Node> errorPages = root.get("error-page");

        for (String code : Arrays.asList("404", "500")) {
            for (Node errorPage : errorPages) {
                if (code.equals(errorPage.getSingle("error-code").getText())
                    && this.prompt.promptBoolean("Your web.xml already contains an error page for " + code
                        + " status codes, replace it?")) {
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
            result.add(ScaffoldUtil.createOrOverwrite(this.prompt, java.getJavaResource(indexController),
                indexController.toString(), overwrite));

        } catch (Exception e) {
            throw new RuntimeException("Error generating VRaptor scaffolding: IndexController", e);
        }

        return result;
    }

    private List<Resource<?>> generateEntityManagerProvider(String targetDir, Resource<?> template, boolean overwrite) {

        List<Resource<?>> result = new ArrayList<Resource<?>>();
        HashMap<Object, Object> context = getTemplateContext(targetDir, template);

        try {
            JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

            JavaClass emProvider = JavaParser.parse(JavaClass.class, this.emProviderTemplate.render(context));
            emProvider.setPackage(java.getBasePackage() + ".provider");
            result.add(ScaffoldUtil.createOrOverwrite(this.prompt, java.getJavaResource(emProvider),
                emProvider.toString(), overwrite));

        } catch (Exception e) {
            throw new RuntimeException("Error generating VRaptor scaffolding: EntityManagerProvider", e);
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
        execution.createChild("configuration").createChild("target").createChild("path")
            .attribute("id", "build.classpath").createChild("fileset")
            .attribute("dir", "${project.build.directory}/${project.build.finalName}/WEB-INF/lib")
            .attribute("includes", "*.jar").getParent().getParent().createChild("java")
            .attribute("classpathref", "build.classpath")
            .attribute("classname", "br.com.caelum.vraptor.scan.VRaptorStaticScanning").attribute("fork", "true")
            .createChild("arg")
            .attribute("value", "${project.build.directory}/${project.build.finalName}/WEB-INF/web.xml").getParent()
            .createChild("classpath").attribute("refid", "build.classpath").getParent().createChild("classpath")
            .attribute("path", "${project.build.directory}/${project.build.finalName}/WEB-INF/classes").getParent()
            .getParent().createChild("war")
            .attribute("destfile", "${project.build.directory}/${project.build.finalName}.war")
            .attribute("webxml", "${project.build.directory}/${project.build.finalName}/WEB-INF/web.xml")
            .createChild("fileset").attribute("dir", "${project.build.directory}/${project.build.finalName}");
        execution.createChild("goals").createChild("goal").text("run");

        result.add(web.createWebResource(XMLParser.toXMLString(pom), "../../../pom.xml"));

        return result;
    }

    private List<Resource<?>> addMavenDependencies() {

        DependencyFacet deps = project.getFacet(DependencyFacet.class);

        deps.addDirectDependency(HIBERNATE_DEPENDENCY);
        deps.addDirectDependency(HSQLDB_DEPENDENCY);

        return new ArrayList<Resource<?>>();
    }

    @Override
    public List<Resource<?>> setup(String targetDir, Resource<?> template, boolean overwrite) {

        List<Resource<?>> resources = new ArrayList<Resource<?>>();

        resources.addAll(generateIndex(targetDir, template, overwrite));
        resources.addAll(generateIndexController(targetDir, template, overwrite));
        resources.addAll(generateEntityManagerProvider(targetDir, template, overwrite));
        resources.addAll(addMavenDependencies());
        resources.addAll(addStaticScanningMavenPlugin());

        return resources;
    }

    @Override
    public void setProject(Project project) {
        super.setProject(project);
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
        return new ArrayList<Resource<?>>();
    }

    @Override
    public List<Resource<?>> generateFromEntity(String targetDir, final Resource<?> template, final JavaClass entity,
        boolean overwrite) {

        List<Resource<?>> result = new ArrayList<Resource<?>>();
        try {
            JavaSourceFacet java = this.project.getFacet(JavaSourceFacet.class);
            ResourceFacet resources = project.getFacet(ResourceFacet.class);
            WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);

            loadTemplates();

            addPersistenceClassMapping(resources, entity);

            // context
            Map<Object, Object> context = CollectionUtils.newHashMap();
            context.put("entity", entity);
            String ccEntity = StringUtils.decapitalize(entity.getName());
            context.put("ccEntity", ccEntity);
            context.put("elIdValue", "${" + ccEntity + ".id}");

            EntityInspector entityInspector = new EntityInspector(entity);
            context.put("queryByExampleJavaCode", entityInspector.getQueryByExampleJavaCode());
            context.put("selectOptionsJavaCode", entityInspector.getSelectOptionsJavaCode());
            context.put("searchFormWidget", entityInspector.getFormWidget());
            context.put("searchTableHeaderWidget", entityInspector.getSearchTableHeaderWidget());
            context.put("searchTableBodyWidget", entityInspector.getSearchTableBodyWidget());
            context.put("viewWidget", entityInspector.getViewWidget());

            // Create the Controller for this entity
            JavaClass controller = JavaParser.parse(JavaClass.class, this.controllerTemplate.render(context));
            controller.setPackage(java.getBasePackage() + ".view");
            result.add(ScaffoldUtil.createOrOverwrite(this.prompt, java.getJavaResource(controller),
                controller.toString(), overwrite));

            String jspDir = "jsp/";
            jspDir += (targetDir != null && !targetDir.equals("")) ? targetDir + "/" : "";

            // Search
            result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
                web.getWebResource("WEB-INF/" + jspDir + ccEntity + "/search.jsp"),
                this.searchTemplate.render(context), overwrite));

            // Create
            result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
                web.getWebResource("WEB-INF/" + jspDir + ccEntity + "/create.jsp"),
                this.createTemplate.render(context), overwrite));

            // Edit
            result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
                web.getWebResource("WEB-INF/" + jspDir + ccEntity + "/edit.jsp"), this.editTemplate.render(context),
                overwrite));

            // View
            result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
                web.getWebResource("WEB-INF/" + jspDir + ccEntity + "/view.jsp"), this.viewTemplate.render(context),
                overwrite));

            // Navigation
            context.put("navigation", entityInspector.getNavigation(web, targetDir));
            context.put("appName", StringUtils.uncamelCase(this.project.getProjectRoot().getName()));
            result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("header.jsp"),
                this.headerTemplate.render(context), overwrite));

        } catch (Exception e) {
            throw new RuntimeException("Error generating vraptor scaffolding: " + e.getMessage(), e);
        }

        return result;
    }

    @Override
    public List<Resource<?>> getGeneratedResources(String targetDir) {
        throw new UnsupportedOperationException("getGeneratedResources - Not supported yet.");
    }

    private void addPersistenceClassMapping(ResourceFacet resources, final JavaClass entity)
        throws IllegalArgumentException, XMLParserException {
        FileResource<?> persistence = (FileResource<?>) resources.getResourceFolder().getChild(
            "META-INF/persistence.xml");
        Node persistenceNode = XMLParser.parse(persistence.getResourceInputStream());
        Node unit = persistenceNode.getSingle("persistence-unit");

        List<Node> propertiesNodes = unit.get("properties").get(0).getChildren();

        // new list
        List<Node> nodes = new ArrayList<Node>();
        for (int i = 0; i < unit.getChildren().size(); i++) {
            Node node = unit.getChildren().get(i);
            nodes.add(node);
            if (node.getName().equals("non-jta-data-source")) {
                nodes.add(new Node("class").text(entity.getQualifiedName()));
            }
        }
        // clean unit chindren
        while (!unit.getChildren().isEmpty()) {
            unit.removeChild(unit.getChildren().get(0));
        }
        // create new list
        for (Node node : nodes) {
            unit.createChild(node.getName()).text(node.getText());
        }
        // create properties list
        for (Node node : propertiesNodes) {
            Node child = unit.get("properties").get(0).createChild(node.getName());
            Map<String, String> attributes = node.getAttributes();
            for (Map.Entry<String, String> attribute : attributes.entrySet()) {
                child.attribute(attribute.getKey(), attribute.getValue());
            }
        }
        persistence.setContents(XMLParser.toXMLString(persistenceNode));
    }
}
