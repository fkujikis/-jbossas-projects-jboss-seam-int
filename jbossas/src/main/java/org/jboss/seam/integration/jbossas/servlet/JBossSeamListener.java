/*
* JBoss, Home of Professional Open Source
* Copyright 2006, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jboss.seam.integration.jbossas.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.kernel.Kernel;
import org.jboss.seam.servlet.SeamListener;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * Servlet context listener integrated with underlying Microcontainer.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class JBossSeamListener extends SeamListener
{
   private static final LogProvider log = Logging.getLogProvider(ServletContextListener.class);

   public void contextInitialized(ServletContextEvent event)
   {
      // TODO - enable some MC scanning notion in Seam
      super.contextInitialized(event);
      
      ServletContext context = event.getServletContext();
      Kernel kernel = getAttribute(context, Kernel.class.getName(), Kernel.class);
      DeploymentUnit unit = getAttribute(context, DeploymentUnit.class.getName(), DeploymentUnit.class);
      if (kernel != null && unit != null)
      {
         // apply kernel and deployment unit to Seam init
      }
      else
      {
         log.info("No Kernel or DeploymentUnit as ServletContext attribute.");
      }
   }

   /**
    * Get the attribute.
    *
    * @param context the servlet context
    * @param name the attribute name
    * @param expectedType the expected attribute type
    * @return null if no such attribute or found attribute instance
    */
   protected <T> T getAttribute(ServletContext context, String name, Class<T> expectedType)
   {
      Object attribute = context.getAttribute(name);
      if (attribute == null)
         return null;
      if (expectedType.isInstance(attribute) == false)
         throw new IllegalArgumentException("Illegal attribute type, expected: " + expectedType + ", actual: " + attribute);
      return expectedType.cast(attribute);
   }
}
