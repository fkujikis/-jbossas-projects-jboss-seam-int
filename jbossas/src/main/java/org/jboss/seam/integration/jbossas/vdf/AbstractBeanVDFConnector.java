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

import org.jboss.dependency.spi.ControllerContext;
import org.jboss.dependency.spi.ControllerState;
import org.jboss.kernel.Kernel;
import org.jboss.kernel.plugins.bootstrap.basic.KernelConstants;
import org.jboss.kernel.spi.dependency.KernelController;

/**
 * AbstractBeanVDFConnector.
 *
 * @param <U> exact unwrapped type
 * @param <T> exact bean type
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public abstract class AbstractBeanVDFConnector<U, T> extends AbstractVDFConnector<U, Kernel>
{
   /** The bean state */
   private ControllerState state;

   public AbstractBeanVDFConnector(ServletContext servletContext)
   {
      super(servletContext);
   }

   protected Class<Kernel> getAttributeType()
   {
      return Kernel.class;
   }

   protected String getAttributeKey()
   {
      return KernelConstants.KERNEL_NAME;
   }

   /**
    * Get bean key.
    *
    * @return the bean key
    */
   protected abstract Object getBeanKey();

   /**
    * The bean type.
    *
    * @return the bean type
    */
   protected abstract Class<T> getBeanType();

   /**
    * Get unwrapped type.
    * e.g. bean might be bean factory
    *
    * @return the unwrapped type
    */
   protected abstract Class<U> getUnwrappedType();

   /**
    * Unwrap bean.
    * Default impl just re-casts.
    *
    * @param bean the bean
    * @return unwrapped bean
    */
   protected U unwrap(T bean)
   {
      Class<U> unwrappedType = getUnwrappedType();
      if (unwrappedType.isInstance(bean) == false)
         throw new IllegalArgumentException("Bean " + bean + " is not instance of " + unwrappedType);

      return unwrappedType.cast(bean);
   }

   protected U getUtilityFromAttribute(Kernel kernel)
   {
      KernelController controller = kernel.getController();
      ControllerContext context;
      if (state == null)
         context = controller.getContext(getBeanKey(), state);
      else
         context = controller.getInstalledContext(getBeanKey());

      if (context == null)
         return null;

      Object target = context.getTarget();
      Class<T> beanType = getBeanType();
      if (beanType.isInstance(target) == false)
         throw new IllegalArgumentException("Bean " + target + " is not instance of " + beanType);

      return unwrap(beanType.cast(target));
   }

   /**
    * Set the expected bean state.
    *
    * @param state the bean state
    */
   public void setState(ControllerState state)
   {
      this.state = state;
   }

   @Override
   public String toString()
   {
      StringBuilder builder = new StringBuilder();
      builder.append("bean=").append(getBeanKey());
      if (state != null)
         builder.append(", state=").append(state);
      return builder.toString();
   }
}