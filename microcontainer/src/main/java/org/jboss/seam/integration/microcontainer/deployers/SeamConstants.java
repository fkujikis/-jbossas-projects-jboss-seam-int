/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.seam.integration.microcontainer.deployers;

/**
 * Seam constants.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface SeamConstants
{
   public static final String SEAM_PROPERTIES = "seam.properties";
   public static final String SEAM_PROPERTIES_META_INF = "META-INF/" + SEAM_PROPERTIES;
   public static final String SEAM_PROPERTIES_WEB_INF = "WEB-INF/classes/" + SEAM_PROPERTIES;
   public static final String SEAM_COMPONENTS = "components.xml";
   public static final String SEAM_COMPONENTS_META_INF = "META-INF/" + SEAM_COMPONENTS;
   public static final String SEAM_COMPONENTS_WEB_INF =  "WEB-INF/classes/META-INF/" + SEAM_COMPONENTS;

   public static final String[] SEAM_FILES = new String[]{
         SEAM_PROPERTIES,
         SEAM_PROPERTIES_META_INF,
         SEAM_PROPERTIES_WEB_INF,
         SEAM_COMPONENTS_META_INF,
         SEAM_COMPONENTS_WEB_INF
   };
}
