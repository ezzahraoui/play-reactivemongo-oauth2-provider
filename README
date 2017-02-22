# play2-oauth2-provider with ReactiveMongo

This project is an example of how to use [play2-oauth2-provider](https://github.com/nulab/play2-oauth2-provider) with Reativemongo
This project is based on the work of [tsuyoshizawa](https://github.com/tsuyoshizawa) on its [example](https://github.com/tsuyoshizawa/scala-oauth2-provider-example-skinny-orm) that uses Skinny-ORM

# How to use 

Start by importing collections on ```/db``` folder

mongoimport -d test -c account account.json
mongoimport -d test -c oauthAuthorizationCode oauthAuthorizationCode.json
mongoimport -d test -c oauthClient oauthClient.json 

Try to create access tokens using curl

### Client credentials

```
$ curl http://localhost:9000/oauth/access_token -X POST -d "client_id=bob_client_id" -d "client_secret=bob_client_secret" -d "grant_type=client_credentials"
```

### Authorization code

```
$ curl http://localhost:9000/oauth/access_token -X POST -d "client_id=alice_client_id" -d "client_secret=alice_client_secret" -d "redirect_uri=http://localhost:3000/callback" -d "code=bob_code" -d "grant_type=authorization_code"
```

### Password

```
$ curl http://localhost:9000/oauth/access_token -X POST -d "client_id=alice_client_id2" -d "client_secret=alice_client_secret2" -d "username=alice@example.com" -d "password=alice" -d "grant_type=password"
```

### Refresh token

```
$ curl http://localhost:9000/oauth/access_token -X POST -d "client_id=alice_client_id2" -d "client_secret=alice_client_secret2" -d "refresh_token=${refresh_token}" -d "grant_type=refresh_token"
```

### Access resource using access_token

You can access application resource using access token

```
$ curl --dump-header - -H "Authorization: Bearer ${access_token}" http://localhost:9000/resources
```

