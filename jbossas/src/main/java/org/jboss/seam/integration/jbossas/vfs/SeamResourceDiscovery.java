package org.jboss.seam.integration.jbossas.vfs;


import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.seam.deployment.ClassDeploymentHandler;
import org.jboss.seam.deployment.ClassDescriptor;
import org.jboss.seam.deployment.DeploymentHandler;
import org.jboss.seam.deployment.DeploymentStrategy;
import org.jboss.seam.deployment.FileDescriptor;
import org.jboss.seam.deployment.Scanner;

/**
 * Seam Resource Discovery for JBoss MC
 * 
 * Actually, not a scanner at all :-)
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 * @author Pete Muir
 */
public class SeamResourceDiscovery implements Scanner
{
   private DeploymentStrategy deploymentStrategy;
	
   public SeamResourceDiscovery(DeploymentStrategy deploymentStrategy) 
   {
      if (deploymentStrategy == null)
         throw new IllegalArgumentException("Null deployment strategy.");
      this.deploymentStrategy = deploymentStrategy;
   }

   public void scanDirectories(File[] directories)
   {
      for (Entry<String, DeploymentHandler> entry : deploymentStrategy.getDeploymentHandlers().entrySet())
      {
         DeploymentHandler deploymentHandler = entry.getValue();
         
         if (deploymentHandler instanceof ClassDeploymentHandler)
         {
            ClassDeploymentHandler classDeploymentHandler = (ClassDeploymentHandler) deploymentHandler;
            // Any class with any of these annotations should be returned
            Set<Class<? extends Annotation>> annotations = classDeploymentHandler.getMetadata().getClassAnnotatedWith();
            Set<Class<?>> discoveredClasses = new HashSet<Class<?>>();
            // TODO retrieve discovered classes from MC AnnotationEnvironment
            // TODO Write a ClassDescriptorSet decorator to adapt between MC representation and Seam representation to avoid this for loop
            for (Class<?> clazz : discoveredClasses)
            {
               String classFileName = classFilenameFromClassName(clazz.getName());
               classDeploymentHandler.getClasses().add(new ClassDescriptor(classFileName, deploymentStrategy.getClassLoader().getResource(classFileName), clazz));
            }
         } 
         else
         {
            // Any resource with this suffix should be returned
            String fileNameSuffix = deploymentHandler.getMetadata().getFileNameSuffix();
            Set<URL> discoveredResources = new HashSet<URL>();
            // TODO retrieve discovered resources from MC
            for (URL url : discoveredResources)
            {
               String fileName = ""; // TODO discover "simple" file name - just the path in the jar
               deploymentHandler.getResources().add(new FileDescriptor(fileName, url));
            }
         }
      }
   }

   public void scanResources(String[] resources)
   {
      
   }
   
   public long getTimestamp() 
   {
	  // TODO Return the time when the last resource that Seam is interested in was modified (any resource visited by the above methods)
      return Long.MAX_VALUE;
   }

   public DeploymentStrategy getDeploymentStrategy() 
   {
	  return deploymentStrategy;
   }
   
   private static String classFilenameFromClassName(String className)
   {
      return className.substring(0).replace('.', '/') + ".class";
  }
}
