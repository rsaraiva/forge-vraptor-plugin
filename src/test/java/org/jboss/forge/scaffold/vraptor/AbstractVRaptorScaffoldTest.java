package org.jboss.forge.scaffold.vraptor;

import org.jboss.forge.project.Project;
import org.jboss.forge.test.AbstractShellTest;

public class AbstractVRaptorScaffoldTest extends AbstractShellTest {

    protected Project setupScaffoldProject() throws Exception {
        Project project = initializeJavaProject();
        queueInputLines("", "", "", "");
        getShell().execute("persistence setup --provider HIBERNATE --container CUSTOM_NON_JTA --named default --jndiDataSource java:jboss/datasources/ExampleDS");
        queueInputLines("", "", "", "");
        getShell().execute("scaffold setup --scaffoldType vraptor");
        return project;
    }
}
