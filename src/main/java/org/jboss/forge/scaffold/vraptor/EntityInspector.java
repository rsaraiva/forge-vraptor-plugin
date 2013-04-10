package org.jboss.forge.scaffold.vraptor;

import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFilter;
import org.metawidget.util.simple.StringUtils;

public class EntityInspector {
    
    private String className;
    private String decapitalizedClassName;
    
    private JavaClass entity;

    public EntityInspector(JavaClass entity) {
        this.entity = entity;
        this.className = entity.getName();
        this.decapitalizedClassName = StringUtils.decapitalize(this.className);
    }
    
    public String getQueryByExampleJavaCode() {
        
        StringBuilder builder = new StringBuilder();
        
        for (Field<JavaClass> field : entity.getFields()) {
            
            if (field.getName().equals("id") || field.getName().equals("version"))
                continue;
            
            String fieldName = field.getName();
            String fieldType = field.getType();
            String capitalizeFieldName = StringUtils.capitalize(fieldName);
            
            // string
            
            if ("String".equals(field.getType())) {
                builder.append("String ").append(fieldName).append(" = example.get").append(capitalizeFieldName).append("();\n");
                builder.append("if (").append(fieldName).append(" != null && !\"\".equals(").append(fieldName).append(")) {\n");
                builder.append("predicatesList.add(builder.like(root.<String>get(\"").append(fieldName).append("\"), '%' + ").append(fieldName).append(" + '%'));\n");
                builder.append("}\n");
            }

            // int or short

            if ("int".equals(field.getType()) || "short".equals(field.getType()) || "byte".equals(field.getType())) {
                builder.append(fieldType).append(" ").append(fieldName).append(" = example.get").append(StringUtils.capitalize(fieldName)).append("();\n");
                builder.append("if (").append(fieldName).append(" != 0) {\n");
                builder.append("predicatesList.add(builder.equal(root.get(\"").append(fieldName).append("\"), ").append(fieldName).append("));\n");
                builder.append("}\n");
            }
            
            //todo: more types
        }
        
        return builder.toString();
    }
    
    public String getSearchFormWidget() {
        
        StringBuilder builder = new StringBuilder();
        
        for (Field<JavaClass> field : entity.getFields()) {
            
            if (field.getName().equals("id") || field.getName().equals("version"))
                continue;
            
            String fieldName = field.getName();
            String uncamelCaseFieldName = StringUtils.uncamelCase(fieldName);
            
            builder.append("<tr>");
            builder.append("  <td class=\"label\"><label for=\"").append(fieldName).append("\"> ").append(uncamelCaseFieldName).append(":</label></td>");
            builder.append("  <td class=\"component\">");
            builder.append("    <input id=\"").append(fieldName).append("\" type=\"text\" name=\"").append(decapitalizedClassName).append(".").append(fieldName).append("\" value=\"${").append(decapitalizedClassName).append(".").append(fieldName).append("}\" />");
            builder.append("  </td>");
            builder.append("  <td class=\"required\"></td>");
            builder.append("</tr>");
        }
        
        return builder.toString();
    }
    
    public String getViewWidget() {
        
        StringBuilder builder = new StringBuilder();
        
        for (Field<JavaClass> field : entity.getFields()) {
            
            if (field.getName().equals("id") || field.getName().equals("version"))
                continue;
            
            String fieldName = field.getName();
            String uncamelCaseFieldName = StringUtils.uncamelCase(fieldName);
            
            builder.append("<tr>\n");
            builder.append("  <td class=\"label\"><label for=\"").append(fieldName).append("\"> ").append(uncamelCaseFieldName).append(":</label></td>\n");
            builder.append("  <td class=\"component\">\n");
            builder.append("    <span id=\"").append(fieldName).append("\">${").append(decapitalizedClassName).append(".").append(fieldName).append("}</span>\n");
            builder.append("  </td>\n");
            builder.append("  <td class=\"required\"></td>\n");
            builder.append("</tr>\n");
        }
        
        return builder.toString();
    }
    
    public String getSearchTableHeaderWidget() {

        StringBuilder builder = new StringBuilder();

        for (Field<JavaClass> field : entity.getFields()) {

            if (field.getName().equals("id") || field.getName().equals("version")) {
                continue;
            }

            String uncamelCaseFieldName = StringUtils.uncamelCase(field.getName());

            builder.append("<th scope=\"col\">").append(uncamelCaseFieldName).append("</th>\n");
        }

        return builder.toString();
    }
    
    public String getSearchTableBodyWidget() {

        StringBuilder builder = new StringBuilder();

        builder.append("<c:forEach var=\"").append(decapitalizedClassName).append("\" items=\"${entities}\">\n");
        builder.append("<tr>\n");
        
        for (Field<JavaClass> field : entity.getFields()) {

            if (field.getName().equals("id") || field.getName().equals("version")) {
                continue;
            }

            String fieldName = field.getName();
            
            builder.append("<td><a href=\"<c:url value=\"/")
                    .append(decapitalizedClassName).append("/view/${")
                    .append(decapitalizedClassName).append(".id}\"/>\"><span>${")
                    .append(decapitalizedClassName).append(".")
                    .append(fieldName).append("}</span></a></td>\n");
        }
        
        builder.append("</tr>\n");
        builder.append("</c:forEach>\n");

        return builder.toString();
    }    

    public String getNavigation(WebResourceFacet web, String targetDir) {
        
        ResourceFilter filter = new ResourceFilter() {
            @Override
            public boolean accept(Resource<?> resource) {
                FileResource<?> file = (FileResource<?>) resource;

                if (!file.isDirectory() || file.getName().equals("index")) {
                    return false;
                }
                return true;
            }
        };
        
        StringBuilder builder = new StringBuilder();
        
        for (Resource<?> resource : web.getWebResource("WEB-INF/jsp" + targetDir).listResources(filter)) {
            
            String resourceName = resource.getName();
            String capitalizeResourceName = StringUtils.capitalize(resourceName);
            builder.append("<li><a href=\"<c:url value=\"/").append(resourceName).append("/search\"/>\">").append(capitalizeResourceName).append("</a></li>");
        }
        
        return builder.toString();
    }
}
