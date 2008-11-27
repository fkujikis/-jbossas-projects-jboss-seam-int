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

/**
 * Abstract VDF connector.
 *
 * @param <U> exact vdf utility type
 * @param <T> exact attribute type
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public abstract class AbstractVDFConnector<U, T> implements VDFConnector<U>
{
   private ServletContext servletContext;
   private U utility;

   protected AbstractVDFConnector(ServletContext servletContext)
   {
      if (servletContext == null)
         throw new IllegalArgumentException("Null servlet context.");
      this.servletContext = servletContext;
   }

   /**
    * Get utility type.
    *
    * @return the utility type
    */
   protected abstract Class<T> getAttributeType();

   /**
    * Get utility attribute key.
    *
    * @return the attribute key
    */
   protected abstract String getAttributeKey();

   public boolean isValid()
   {
      return getUtility() != null;
   }

   public U getUtility()
   {
      if (utility == null)
      {
         String key = getAttributeKey();
         Object attribute = servletContext.getAttribute(key);
         if (attribute != null)
         {
            Class<T> type = getAttributeType();
            if (type.isInstance(attribute) == false)
               throw new IllegalArgumentException("Attribute " + key + " is not " + type.getName() + " instance: " + attribute);

            utility = getUtilityFromAttribute(type.cast(attribute));
         }
      }
      return utility;
   }

   /**
    * Get utility from attribute.
    *
    * @param attribute the attribute
    * @return attribute's utility
    */
   protected abstract U getUtilityFromAttribute(T attribute);
}
