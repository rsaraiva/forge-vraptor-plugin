package org.jboss.forge.scaffold.vraptor;

import org.jboss.forge.project.Project;
import org.jboss.forge.test.AbstractShellTest;

public class AbstractVRaptorScaffoldTest extends AbstractShellTest {

    protected Project setupScaffoldProject() throws Exception {
        Project project = initializeJavaProject();
        queueInputLines("HIBERNATE", "CUSTOM_JDBC", "", "", "");
        getShell().execute("persistence setup --named default --database HSQLDB_IN_MEMORY --jdbcDriver org.hsqldb.jdbcDriver --jdbcURL jdbc:hsqldb:mem:test --jdbcUsername sa --jdbcPassword sa");
        queueInputLines("", "", "", "");
        getShell().execute("scaffold setup --scaffoldType vraptor");
        return project;
    }
}
