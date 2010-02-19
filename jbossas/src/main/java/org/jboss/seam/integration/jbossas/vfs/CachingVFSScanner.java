package org.jboss.seam.integration.jbossas.vfs;


import org.jboss.seam.deployment.DeploymentStrategy;

/**
 * Caching JBoss VFS aware scanner.
 *
 * Use this one when you know VFSContext will be present in cache.
 * This is mostly true for apps that are deployed into jbossas deploy directory.
 * Otherwise change the VFSCache impl to make sure or use plain VFSScanner.
 * 
 * @deprecated This is no longer needed with VFS3
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
@Deprecated
public class CachingVFSScanner extends VFSScanner
{
   public CachingVFSScanner(DeploymentStrategy deploymentStrategy)
   {
      super(deploymentStrategy);
   }
}