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

import org.jboss.beans.metadata.spi.factory.BeanFactory;

/**
 * AbstractBeanFactoryTypeVDFConnector.
 *
 * @param <T> exact bean type
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public abstract class AbstractBeanFactoryVDFConnector<T> extends AbstractBeanVDFConnector<T, BeanFactory>
{
   protected AbstractBeanFactoryVDFConnector(ServletContext servletContext)
   {
      super(servletContext);
   }

   protected Class<BeanFactory> getBeanType()
   {
      return BeanFactory.class;
   }

   @Override
   protected T unwrap(BeanFactory factory)
   {
      try
      {
         Object bean = factory.createBean();
         Class<T> type = getUnwrappedType();
         if (type.isInstance(bean) == false)
            throw new IllegalArgumentException("Bean " + bean + " is not instance of " + type);

         return type.cast(bean);
      }
      catch (Throwable t)
      {
         throw new RuntimeException(t);
      }
   }
}