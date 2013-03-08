package myApp.test.selenium;

import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: eluibon
 * Date: 07/02/13
 * Time: 16.35
 */
public class WarBuilder {

    public static WebArchive buildWar() {

        MavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");

        WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackage("myApp.controller")
                .addPackage("myApp.controller.user")
                .addPackage("myApp.model.user")
                .addPackage("myApp.model.accesslog")
                .addPackage("myApp.service")
                .addPackage("myApp.util")
                .addPackage("myApp.util.converters")
                .addPackage("myApp.repository")
                .addPackage("myApp.security")
                .addPackage("myApp.security.accesslog")
                .addAsResource("bundles/myApp_en.properties")
                .addAsResource("bundles/MessageBundle_en.properties")
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                        // database initialization, Arquillian Persistence does not work well with Drone + Webdriver
                .addAsResource("import.sql", "import.sql")
                        // Apache Shiro config
                .addAsResource("shiro.ini", "shiro.ini")
                        // datasource
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/myApp-ds.xml"))
                        // all XHTML under src/main/webapp
                .merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
                        .importDirectory("src/main/webapp").as(GenericArchive.class),
                        "/", Filters.include(".*\\.xhtml$"))
                .merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
                        .importDirectory("src/main/webapp/resources").as(GenericArchive.class),
                        "/resources", Filters.includeAll())
                        // maven dependencies
                .addAsLibraries(resolver.artifact("org.apache.myfaces.extensions.cdi.bundles:myfaces-extcdi-bundle-jsf20").resolveAsFiles())
                .addAsLibraries(resolver.artifact("org.apache.shiro:shiro-core").resolveAsFiles())
                .addAsLibraries(resolver.artifact("org.apache.shiro:shiro-web").resolveAsFiles())
                .addAsLibraries(resolver.artifact("org.primefaces:primefaces").resolveAsFiles())
                .addAsLibraries(resolver.artifact("org.primefaces.extensions:primefaces-extensions").resolveAsFiles())
                .addAsLibraries(resolver.artifact("org.omnifaces:omnifaces").resolveAsFiles())
                .addAsLibraries(resolver.artifact("org.primefaces.themes:redmond").resolveAsFiles())
                .addAsLibraries(resolver.artifact("com.mysema.querydsl:querydsl-jpa").resolveAsFiles())
                .addAsLibraries(resolver.artifact("com.mysema.querydsl:querydsl-apt").resolveAsFiles())
                .addAsLibraries(resolver.artifact("org.springframework.data:spring-data-jpa").resolveAsFiles())

                        // config files
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/elsecurity.taglib.xml"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/beans.xml"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/faces-config.xml"))
                .setWebXML(new File("src/main/webapp/WEB-INF/web.xml"));

        // there seems to be some sort of bug with MANIFEST.MF and ShrinkWrap
        // this seems to be a quick workaround while we investigate...
        war.delete("META-INF/MANIFEST.MF");
        war.setManifest(new File("src/main/resources/META-INF/MANIFEST.MF"));

        return war;
    }
}
