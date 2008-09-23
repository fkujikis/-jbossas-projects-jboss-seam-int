package org.jboss.seam.integration.jbossas.jms;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import org.jboss.seam.jms.ManagedTopicPublisher;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

/**
 * Override Seam's TopicConnection to return a different JNDI name for the connection factory
 * 
 * @author Shane Bryzak
 */
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Name("org.jboss.seam.jms.topicConnection")
@Install(precedence=FRAMEWORK, genericDependencies=ManagedTopicPublisher.class)
public class TopicConnection extends org.jboss.seam.jms.TopicConnection
{
   private String topicConnectionFactoryJndiName = "ConnectionFactory";

   /**
    * The JNDI name of the TopicConnectionFactory
    */
   @Override
   public String getTopicConnectionFactoryJndiName()
   {
      return topicConnectionFactoryJndiName;
   }
}
