package org.jboss.seam.integration.jbossas.vfs;


import java.io.IOException;
import java.net.URL;

import org.jboss.seam.deployment.DeploymentStrategy;
import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;

/**
 * Caching JBoss VSF aware scanner.
 *
 * Use this one when you know VFSContext will be present in cache.
 * This is mostly true for apps that are deployed into jbossas deploy directory.
 * Otherwise change the VFSCache impl to make sure or use plain VFSScanner.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class CachingVFSScanner extends VFSScanner
{
   public CachingVFSScanner(DeploymentStrategy deploymentStrategy)
   {
      super(deploymentStrategy);
   }

   /**
    * Get the virtual file root.
    *
    * @param url         the root URL
    * @param parentDepth level of parent depth
    * @return actual virtual file from url param
    * @throws java.io.IOException for any error
    */
   protected VirtualFile getRoot(URL url, int parentDepth) throws IOException
   {
      log.trace("Root url: " + url);

      // get the cached file directly, as we expect it to already be there
      VirtualFile top = VFS.getCachedFile(url);
      while (parentDepth > 0)
      {
         if (top == null)
            throw new IllegalArgumentException("Null parent: " + url + ", there might be no matching VFSContext in VFSCache.");
         top = top.getParent();
         parentDepth--;
      }

      log.trace("Top: " + top);

      return top;
   }
}