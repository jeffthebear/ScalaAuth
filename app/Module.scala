import com.google.inject.AbstractModule
import java.time.Clock

import com.feth.play.module.mail.IMailer;
import com.feth.play.module.mail.Mailer;
import com.feth.play.module.mail.Mailer.MailerFactory;
import com.feth.play.module.pa.Resolver;
import com.feth.play.module.pa.providers.openid.OpenIdAuthProvider;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
//import play.api.inject.Module;
import providers.MyStupidBasicAuthProvider;
import providers.MyUsernamePasswordAuthProvider;
import scala.collection.Seq;
import service.DataInitializer;
import service.MyResolver;
import service.MyUserService;

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule {

  override def configure() = {
    // Use the system clock as the default implementation of Clock
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
    // Ask Guice to create an instance of ApplicationTimer when the
    // application starts.
    //bind(classOf[ApplicationTimer]).asEagerSingleton()
    // Set AtomicCounter as the implementation for Counter.
    //bind(classOf[Counter]).to(classOf[AtomicCounter])


    //install(new FactoryModuleBuilder().implement(IMailer.class, Mailer.class).build(MailerFactory.class));

    bind(classOf[Resolver]).to(classOf[MyResolver]);

    bind(classOf[DataInitializer]).asEagerSingleton();

    bind(classOf[MyUserService]).asEagerSingleton();
    //bind(GoogleAuthProvider.class).asEagerSingleton();
    //bind(FacebookAuthProvider.class).asEagerSingleton();
    //bind(FoursquareAuthProvider.class).asEagerSingleton();
    bind(classOf[MyUsernamePasswordAuthProvider]).asEagerSingleton();
    bind(classOf[OpenIdAuthProvider]).asEagerSingleton();
    //bind(TwitterAuthProvider.class).asEagerSingleton();
    //bind(LinkedinAuthProvider.class).asEagerSingleton();
    //bind(VkAuthProvider.class).asEagerSingleton();
    //bind(XingAuthProvider.class).asEagerSingleton();
    //bind(UntappdAuthProvider.class).asEagerSingleton();
    //bind(PocketAuthProvider.class).asEagerSingleton();
    //bind(GithubAuthProvider.class).asEagerSingleton();
    bind(classOf[MyStupidBasicAuthProvider]).asEagerSingleton();
    //bind(SpnegoAuthProvider.class).asEagerSingleton();
    //bind(EventBriteAuthProvider.class).asEagerSingleton();
  }

}
