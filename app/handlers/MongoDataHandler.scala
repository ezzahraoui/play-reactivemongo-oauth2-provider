package handlers

import javax.inject._
import scala.concurrent.Future
import scala.util.{ Success, Failure }
import play.api.mvc.Session
import scalaoauth2.provider._
import models._
import services._
import scala.concurrent.ExecutionContext.Implicits.global

class MongoDataHandler @Inject() (
  accountService: AccountService,
  oAuthClientService: OAuthClientService,
  oAuthAuthorizationCodeService: OAuthAuthorizationCodeService,
  oAuthAccessTokenService: OAuthAccessTokenService) extends DataHandler[Account] {

  private val accessTokenExpireSeconds = 3600
  private def toAccessToken(accessToken: OAuthAccessToken) = {
    AccessToken(
      accessToken.accessToken,
      Some(accessToken.refreshToken),
      None,
      Some(accessTokenExpireSeconds),
      accessToken.createdAt.toDate)
  }

  override def createAccessToken(authInfo: AuthInfo[Account]): Future[AccessToken] = {
    val clientId = authInfo.clientId.getOrElse(throw new InvalidClient())
    oAuthClientService.findByClientId(clientId).flatMap {
      case Some(oAuthClient) =>
        oAuthAccessTokenService.create(authInfo.user, oAuthClient).flatMap { oAuthAccessToken =>
          /**
           * [TODO] Throw an exception if refresh function returns None
           */
          Future.successful(toAccessToken(oAuthAccessToken.get))
        }
      case _ => throw new InvalidClient()
    }
  }

  override def deleteAuthCode(code: String): Future[Unit] = {
    oAuthAuthorizationCodeService.delete(code)
  }

  override def findAuthInfoByCode(code: String): Future[Option[AuthInfo[Account]]] = {
    oAuthAuthorizationCodeService.findByCode(code).flatMap {
      case Some(oAuthAuthorizationCode) =>
        Future.successful(for {
          account <- oAuthAuthorizationCode.account
          client <- oAuthAuthorizationCode.oauthClient
        } yield {
          AuthInfo(
            user = account,
            clientId = Some(client.clientId),
            scope = None,
            redirectUri = oAuthAuthorizationCode.redirectUri)
        })
      case _ => Future.successful(None)
    }
  }

  override def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[Account]]] = {
    oAuthAccessTokenService.findByRefreshToken(refreshToken).flatMap {
      case Some(accessToken) =>
        Future.successful(for {
          account <- accessToken.account
          client <- accessToken.oauthClient
        } yield {
          AuthInfo(
            user = account,
            clientId = Some(client.clientId),
            scope = None,
            redirectUri = None)
        })
      case _ => Future.successful(None)
    }
  }

  override def findUser(request: AuthorizationRequest): Future[Option[Account]] = {
    request match {
      case request: PasswordRequest =>
        accountService.authenticate(request.username, request.password)
      case request: ClientCredentialsRequest =>
        request.clientCredential match {
          case Some(clientCredential) =>
            oAuthClientService.findClientCredentials(
              clientCredential.clientId,
              clientCredential.clientSecret.getOrElse(""))
          case _ =>
            Future.successful(None)
        }
      case _ =>
        Future.successful(None)
    }
  }

  override def getStoredAccessToken(authInfo: AuthInfo[Account]): Future[Option[AccessToken]] = {
    oAuthAccessTokenService.findByAuthorized(authInfo.user, authInfo.clientId.getOrElse("")).flatMap {
      case Some(oAuthAccessToken) =>
        Future.successful(Option(toAccessToken(oAuthAccessToken)))
      case _ => Future.successful(None)
    }
  }

  override def refreshAccessToken(authInfo: AuthInfo[Account], refreshToken: String): Future[AccessToken] = {
    val clientId = authInfo.clientId.getOrElse(throw new InvalidClient())
    oAuthClientService.findByClientId(clientId).flatMap {
      case Some(oAuthClient) =>
        oAuthAccessTokenService.refresh(authInfo.user, oAuthClient).flatMap { oAuthAccessToken =>
          /**
           * [TODO] Throw an exception if refresh function returns None
           */
          Future.successful(toAccessToken(oAuthAccessToken.get))
        }
      case _ => throw new InvalidClient()
    }
  }

  override def validateClient(request: AuthorizationRequest): Future[Boolean] = {
    request.clientCredential match {
      case Some(clientCredential) =>
        oAuthClientService.validate(clientCredential.clientId, clientCredential.clientSecret.getOrElse(""), request.grantType)
      case _ =>
        Future.successful(false)
    }
  }

  override def findAccessToken(token: String): Future[Option[AccessToken]] = {
    oAuthAccessTokenService.findByAccessToken(token).flatMap {
      case Some(oAuthAccessToken) =>
        Future.successful(Option(toAccessToken(oAuthAccessToken)))
      case _ => Future.successful(None)
    }
  }

  override def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[Account]]] = {
    oAuthAccessTokenService.findByAccessToken(accessToken.token).flatMap {
      case Some(accessToken) =>
        Future.successful(for {
          account <- accessToken.account
          client <- accessToken.oauthClient
        } yield {
          AuthInfo(
            user = account,
            clientId = Some(client.clientId),
            scope = None,
            redirectUri = None)
        })
      case _ => Future.successful(None)
    }
  }
}