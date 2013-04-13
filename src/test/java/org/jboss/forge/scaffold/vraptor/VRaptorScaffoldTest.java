package org.jboss.forge.scaffold.vraptor;

import org.jboss.forge.project.Project;
import org.junit.Assert;
import org.junit.Test;

public class VRaptorScaffoldTest extends AbstractVRaptorScaffoldTest {

    @Test
    public void testSetupScaffold() throws Exception {
        Project project = setupScaffoldProject();
        Assert.assertTrue(project.hasFacet(VRaptorScaffold.class));
    }

    @Test
    public void testGenerateFromEntity() throws Exception {
        
        Project project = setupScaffoldProject();

        queueInputLines("", "");
        getShell().execute("entity --named Group");
        
        queueInputLines("");
        getShell().execute("field string --named name");
        
        queueInputLines("", "", "", "");
        getShell().execute("scaffold from-entity");
        
        queueInputLines("", "");
        getShell().execute("entity --named Contact");
        
        queueInputLines("");
        getShell().execute("field string --named name");
        
        queueInputLines("");
        getShell().execute("field manyToOne --named group --fieldType com.test.model.Group.java");
        
        //queueInputLines("");
        //getShell().execute("field int --named age");

        queueInputLines("", "", "", "", "");
        getShell().execute("scaffold from-entity");
    }
}
