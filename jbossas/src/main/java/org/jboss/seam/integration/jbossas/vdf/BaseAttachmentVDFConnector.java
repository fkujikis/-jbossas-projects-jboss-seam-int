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

import javax.servlet.ServletContext;

import org.jboss.deployers.structure.spi.DeploymentUnit;

/**
 * Attachment VDF connector.
 * It knows how to access vdf attachments from ServletContext.
 *
 * @param <U> exact vdf attachment type
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public abstract class BaseAttachmentVDFConnector<U> extends AttachmentVDFConnector<U>
{
   private boolean allowHierarchyLookup;

   protected BaseAttachmentVDFConnector(ServletContext servletContext)
   {
      super(servletContext);
   }

   /**
    * Get attachment type.
    *
    * @return the attachment type
    */
   protected abstract Class<U> getAttachmentType();

   protected U getUtilityFromAttribute(DeploymentUnit unit)
   {
      U utility = lookup(unit);
      if (utility == null && allowHierarchyLookup)
      {
         DeploymentUnit parent = unit.getParent();
         while(parent != null && utility == null)
         {
            utility = lookup(parent);
            parent = parent.getParent();
         }
      }
      return utility;
   }

   /**
    * Do attachment lookup.
    *
    * @param unit the deployment unit
    * @return the lookup result
    */
   protected U lookup(DeploymentUnit unit)
   {
      return unit.getAttachment(getAttachmentType());
   }

   /**
    * Do we allow to do hierarchy attachment lookup on deployment unit.
    *
    * @param allowHierarchyLookup the allow hierarchy lookup flag
    */
   public void setAllowHierarchyLookup(boolean allowHierarchyLookup)
   {
      this.allowHierarchyLookup = allowHierarchyLookup;
   }
}