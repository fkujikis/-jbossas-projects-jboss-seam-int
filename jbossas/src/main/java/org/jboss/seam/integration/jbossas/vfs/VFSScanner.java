package org.jboss.seam.integration.jbossas.vfs;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import org.jboss.seam.deployment.AbstractScanner;
import org.jboss.seam.deployment.DeploymentStrategy;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.spi.registry.VFSRegistry;

/**
 * JBoss VFS aware scanner.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 * @author Pete Muir
 */
public class VFSScanner extends AbstractScanner
{
   protected final LogProvider log = Logging.getLogProvider(getClass());

   protected static Method handleMethod;
   private long timestamp;

   public VFSScanner(DeploymentStrategy deploymentStrategy)
   {
      super(deploymentStrategy);
   }

   static
   {
      try
      {
         handleMethod = AbstractScanner.class.getDeclaredMethod("handle", String.class);
         handleMethod.setAccessible(true);
      }
      catch (Throwable t)
      {
         handleMethod = null;
      }
   }

   /**
    * Get the virtual file root.
    *
    * @param url         the root URL
    * @param parentDepth level of parent depth
    * @return actual virtual file from url param
    * @throws IOException for any error
    */
   protected VirtualFile getRoot(URL url, int parentDepth) throws IOException
   {
      log.trace("Root url: " + url);

      // try cache first, it should also have proper depth
      VFSRegistry registry = VFSRegistry.getInstance();
      VirtualFile vf = registry.getFile(url);
      int depth = parentDepth;
      while (vf != null && depth > 0)
      {
         vf = vf.getParent();
         depth--;
      }
      if (vf != null)
         return vf;

      String urlString = url.toExternalForm();
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

      // no sense in checking cache, we already did that
      VirtualFile top = VFS.createNewRoot(vfsurl);
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
      scanDirectories(directories, new File[0]);
   }

   @Override
   public void scanDirectories(File[] directories, File[] excludedDirectories)
   {
      for (File directory : directories)
      {
         handleDirectory(directory, null, excludedDirectories);
      }
   }

   /**
    * Handle directories.
    *
    * @param file the file to handle
    * @param path the current path
    * @param excludedDirectories the excluded dirs
    */
   private void handleDirectory(File file, String path, File[] excludedDirectories)
   {
      for (File excludedDirectory : excludedDirectories)
      {
         if (file.equals(excludedDirectory))
         {
            log.trace("Skipping excluded directory: " + file);
            return;
         }
      }

      // one wrapper delegate per invocation
      FileModifiableResource delegate = new FileModifiableResource();

      log.trace("Handling directory: " + file);
      for (File child : file.listFiles())
      {
         String newPath = (path == null) ? child.getName() : path + '/' + child.getName();
         if (child.isDirectory())
         {
            handleDirectory(child, newPath, excludedDirectories);
         }
         else
         {
            delegate.setFile(child);
            try
            {
               handleItem(delegate, newPath);
            }
            catch (IOException ignored)
            {
            }
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
      // one wrapper delegate per invocation
      VirtualFileModifiableResource delegate = new VirtualFileModifiableResource();

      if (root.isLeaf())
      {
         delegate.setFile(root);
         touchTimestamp(delegate);
         handleItemIgnoreErrors(root.getPathName());
      }
      else
      {
         boolean isArchive = root.isArchive();
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

            String path = name.substring(length);

            if (isArchive)
            {
               // don't timestamp check zip entries, just handle them
               handleItemIgnoreErrors(path);
            }
            else
            {
               // check if we need to touch
               delegate.setFile(child);
               handleItem(delegate, path);
            }
         }
      }
   }

   /**
    * Handle item.
    *
    * @param resource the item's resource
    * @param name the item name
    * @throws IOException for any error
    */
   protected void handleItem(ModifiableResource resource, String name) throws IOException
   {
      boolean doTouch = true;
      try
      {
         if (handleMethod != null)
            doTouch = (Boolean)handleMethod.invoke(this, name);
      }
      catch (Exception e)
      {
         IOException ioe = new IOException();
         ioe.initCause(e);
         throw ioe;
      }

      if (doTouch)
      {
         touchTimestamp(resource);
      }
      // we haven't handled it before
      if (handleMethod == null)
      {
         handleItemIgnoreErrors(name);
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
         log.warn("Error handling item '" + name + "': " + t);
      }
   }

   /**
    * Update timestamp.
    *
    * @param file the file to check
    * @throws IOException for any error
    */
   private void touchTimestamp(ModifiableResource file) throws IOException
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
