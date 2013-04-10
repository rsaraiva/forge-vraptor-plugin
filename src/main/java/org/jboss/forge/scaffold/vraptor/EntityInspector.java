package org.jboss.forge.scaffold.vraptor;

import java.lang.reflect.Field;
import org.metawidget.util.simple.StringUtils;

public class EntityInspector {
    
    private Class clazz;
    private String className;
    private String decapitalizedClassName;

    public EntityInspector(Class clazz) {
        this.clazz = clazz;
        this.className = clazz.getSimpleName();
        this.decapitalizedClassName = StringUtils.decapitalize(clazz.getSimpleName());
    }
    
    public String getSearchFormWidget() {
        
        StringBuilder builder = new StringBuilder();
        
        for (Field field : clazz.getDeclaredFields()) {
            
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
        
        for (Field field : clazz.getDeclaredFields()) {
            
            if (field.getName().equals("id") || field.getName().equals("version"))
                continue;
            
            String fieldName = field.getName();
            String uncamelCaseFieldName = StringUtils.uncamelCase(fieldName);
            
            builder.append("<tr>");
            builder.append("  <td class=\"label\"><label for=\"").append(fieldName).append("\"> ").append(uncamelCaseFieldName).append(":</label></td>");
            builder.append("  <td class=\"component\">");
            builder.append("    <span id=\"").append(fieldName).append("\">${").append(decapitalizedClassName).append(".").append(fieldName).append("}</span>");
            builder.append("  </td>");
            builder.append("  <td class=\"required\"></td>");
            builder.append("</tr>");
        }
        
        return builder.toString();
    }
    
    public String getSearchTableHeaderWidget() {

        StringBuilder builder = new StringBuilder();

        for (Field field : clazz.getDeclaredFields()) {

            if (field.getName().equals("id") || field.getName().equals("version")) {
                continue;
            }

            String uncamelCaseFieldName = StringUtils.uncamelCase(field.getName());

            builder.append("<th scope=\"col\">").append(uncamelCaseFieldName).append("</th>");
        }

        return builder.toString();
    }
    
    public String getSearchTableBodyWidget() {

        StringBuilder builder = new StringBuilder();

        for (Field field : clazz.getDeclaredFields()) {

            if (field.getName().equals("id") || field.getName().equals("version")) {
                continue;
            }

            String fieldName = field.getName();
            
            builder.append("<td><a href=\"<c:url value=\"/")
                    .append(decapitalizedClassName).append("/view/_{")
                    .append(decapitalizedClassName).append(".id}\"/>\"><span>_{")
                    .append(decapitalizedClassName).append(".")
                    .append(fieldName).append("}</span></a></td>");
        }

        return builder.toString();
    }    
}
