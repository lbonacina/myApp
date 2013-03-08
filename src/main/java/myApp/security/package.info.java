/**
 *  A collection of class related to Apache Shiro and it's integration with CDI, JSF, JPA and Infinispan
 *
 *  the core is CdiEnvironmentLoaderListener that does the startup of Shiro into a CDI-aware environment, so we
 *  can inject a JPA Realm and the Infinispan Cache for permissions
 *
 *  SecurityProducer offers some utility producers to use Shiro in the application
 *
 *  FacesAjaxAwareUserFilter is an extension to the PassThruAuthenticationFilter Shiro Filter to allow for session
 *  expiration on Ajax Request, see the excellent article by BalusC at : http://balusc.blogspot.it/2013/01/apache-shiro-is-it-ready-for-java-ee-6.html
 *
 *
 */
package myApp.security ;