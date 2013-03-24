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
    public void testGenerateFromEntity() throws Exception
    {
        Project project = setupScaffoldProject();

        queueInputLines("");
        getShell().execute("entity --named Customer");
        getShell().execute("field string --named firstName");
        getShell().execute("field string --named lastName");
        
        // continue
    }
}
