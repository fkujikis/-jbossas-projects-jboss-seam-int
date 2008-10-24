package org.jboss.seam.integration.jbossas.vfs;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import org.jboss.seam.deployment.AbstractScanner;
import org.jboss.seam.deployment.DeploymentStrategy;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;

/**
 * JBoss VSF aware scanner.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 * @author Pete Muir
 */
public class VFSScanner extends AbstractScanner
{
   private static final LogProvider log = Logging.getLogProvider(VFSScanner.class);

   private long timestamp;

   public VFSScanner(DeploymentStrategy deploymentStrategy)
   {
      super(deploymentStrategy);
   }

   /**
    * Get the virtual file root.
    *
    * @param url         the root URL
    * @param parentDepth level of parent depth
    * @return actual virtual file from url param
    * @throws IOException for any error
    */
   protected static VirtualFile getRoot(URL url, int parentDepth) throws IOException
   {
      log.trace("Root url: " + url);

      String urlString = url.toString();
      // TODO - this should go away once we figure out why -exp.war is part of CL resources
      if (urlString.startsWith("vfs") == false)
         return null;

      int p = urlString.indexOf(":");
      String file = urlString.substring(p + 1);
      URL vfsurl = null;
      String relative;
      File fp = new File(file);

      log.trace("File: " + fp);

      if (fp.exists())
      {
         vfsurl = fp.getParentFile().toURL();
         relative = fp.getName();
      }
      else
      {
         File curr = fp;
         relative = fp.getName();
         while ((curr = curr.getParentFile()) != null)
         {
            if (curr.exists())
            {
               vfsurl = curr.toURL();
               break;
            }
            else
            {
               relative = curr.getName() + "/" + relative;
            }
         }
      }

      log.trace("URL: " + vfsurl + ", relative: " + relative);

      VirtualFile top = VFS.getRoot(vfsurl);
      top = top.getChild(relative);
      while (parentDepth > 0)
      {
         if (top == null)
            throw new IllegalArgumentException("Null parent: " + vfsurl + ", relative: " + relative);
         top = top.getParent();
         parentDepth--;
      }

      log.trace("Top: " + top);

      return top;
   }

   public void scanDirectories(File[] directories)
   {
      for (File dir : directories)
      {
         try
         {
            VirtualFile root = getRoot(dir.toURL(), 0);
            if (root != null)
               handleRoot(root);
            else
               log.trace("Null root: " + dir);
         }
         catch (IOException e)
         {
            log.warn("Cannot scan directory " + dir, e);
         }
      }
   }

   public void scanResources(String[] resources)
   {
      for (String resourceName : resources)
      {
         try
         {
            Enumeration<URL> urlEnum = getDeploymentStrategy().getClassLoader().getResources(resourceName);
            while (urlEnum.hasMoreElements())
            {
               URL url = urlEnum.nextElement();
               VirtualFile root = getRoot(url, resourceName.lastIndexOf('/') > 0 ? 2 : 1);
               if (root != null)
                  handleRoot(root);
               else
                  log.trace("Null root: " + url);
            }
         }
         catch (IOException ioe)
         {
            log.warn("Cannot read resource: " + resourceName, ioe);
         }
      }
   }

   /**
    * Handle virtual file root.
    *
    * @param root the virtual file root
    * @throws IOException for any error
    */
   protected void handleRoot(VirtualFile root) throws IOException
   {
      if (root.isLeaf())
      {
         touchTimestamp(root);
         handleItemIgnoreErrors(root.getPathName());
      }
      else
      {
         String rootPathName = root.getPathName();
         int rootPathNameLength = rootPathName.length();
         List<VirtualFile> children = root.getChildrenRecursively(LeafVirtualFileFilter.INSTANCE);
         for (VirtualFile child : children)
         {
            String name = child.getPathName();
            // move past '/'
            int length = rootPathNameLength;
            if (name.charAt(length) == '/')
               length++;
            touchTimestamp(child);
            handleItemIgnoreErrors(name.substring(length));
         }
      }
   }

   /**
    * Handle item, ignore any errors.
    *
    * @param name the item name
    */
   protected void handleItemIgnoreErrors(String name)
   {
      try
      {
         handleItem(name);
      }
      catch (Throwable t)
      {
         log.warn("Error handling item: " + name, t);
      }
   }

   /**
    * Update timestamp.
    *
    * @param file the file to check
    * @throws IOException for any error
    */
   private void touchTimestamp(VirtualFile file) throws IOException
   {
      long lastModified = file.getLastModified();
      if (lastModified > timestamp)
      {
         timestamp = lastModified;
      }
   }

   @Override
   public long getTimestamp()
   {
      return timestamp;
   }
}
