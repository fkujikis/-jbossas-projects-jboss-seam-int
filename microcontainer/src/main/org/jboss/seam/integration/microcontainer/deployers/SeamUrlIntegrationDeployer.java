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
package org.jboss.seam.integration.microcontainer.deployers;

import java.net.URL;
import java.net.MalformedURLException;

import org.jboss.deployers.vfs.plugins.classloader.PathUrlIntegrationDeployer;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.util.StringPropertyReplacer;

/**
 * Seam integration deployer.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class SeamUrlIntegrationDeployer extends PathUrlIntegrationDeployer<JBossWebMetaData>
{
   public SeamUrlIntegrationDeployer()
   {
      // we only look at wars
      super(JBossWebMetaData.class);
      setIntegrationURL(getURL());
      setFiles(new String[]{"seam.properties", "META-INF/seam.properties", "META-INF/components.xml"});
   }

   /**
    * Get the Seam integration url.
    *
    * @return the seam jbossas integration url
    */
   protected URL getURL()
   {
      try
      {
         String url = "${jboss.home}/integration/seam/jboss-seam-int-jbossas.jar";
         url = StringPropertyReplacer.replaceProperties(url);
         return new URL(url);
      }
      catch (MalformedURLException e)
      {
         throw new IllegalArgumentException("Unexpected error: " + e);
      }
   }
}
