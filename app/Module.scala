import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import services._
import handlers._

class Module extends AbstractModule with ScalaModule {

  override def configure() = {
  	bind[MongoDataHandler]
    bind[AccountService]
    bind[OAuthClientService]
    bind[OAuthAccessTokenService]
    bind[OAuthAuthorizationCodeService]
  }

}
