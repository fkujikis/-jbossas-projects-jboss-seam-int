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
package org.jboss.seam.integration.jbossas.vdf;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

import org.jboss.deployers.spi.annotations.AnnotationEnvironment;
import org.jboss.deployers.structure.spi.DeploymentUnit;

/**
 *  AnnotationEnvironment VDF connector.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class FederatedAnnotationEnvironmentVDFConnector extends AttachmentVDFConnector<List<AnnotationEnvironment>>
{
   protected FederatedAnnotationEnvironmentVDFConnector(ServletContext servletContext)
   {
      super(servletContext);
   }

   protected List<AnnotationEnvironment> getUtilityFromAttribute(DeploymentUnit unit)
   {
      List<AnnotationEnvironment> list = new ArrayList<AnnotationEnvironment>();
      DeploymentUnit top = unit.getTopLevel();
      findAllAnnotationEnvironments(top, list);
      return list;
   }

   /**
    * Find all anotation environments recursively.
    *
    * @param unit the current deployment unit
    * @param list the holder list
    */
   protected void findAllAnnotationEnvironments(DeploymentUnit unit, List<AnnotationEnvironment> list)
   {
      applyAnnotationEnvironment(unit, list);
      List<DeploymentUnit> children = unit.getChildren();
      if (children != null && children.isEmpty() == false)
      {
         for (DeploymentUnit child : children)
         {
            findAllAnnotationEnvironments(child, list);
         }
      }
   }

   /**
    * Get annotation environment.
    *
    * @param unit the deployment unit
    * @param list the list
    */
   protected void applyAnnotationEnvironment(DeploymentUnit unit, List<AnnotationEnvironment> list)
   {
      AnnotationEnvironment ae = unit.getAttachment(AnnotationEnvironment.class);
      if (ae != null)
         list.add(ae);
   }
}