## About

This is an example project that illustrates creating a RESTful API in Spring Boot.

## Runnning this project

```
mvn spring-boot:run
```

## Server context path
the root path is **/gigy**

## Get token

```
curl -X POST --user 'gigy:secret' -d 'grant_type=password&username=peter@example.com&password=password' http://localhost:8000/gigy/oauth/token
```

## Example commands

Getting all people from the API:
```
curl -i -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -X GET http://localhost:8000/gigy/people
```

## Get token by grant_type client_credentials
curl -X POST --user 'gigy:secret' -d 'grant_type=client_credentials' http://localhost:8000/gigy/oauth/token

## Get jwt token
curl -X POST --user 'gigy:secret' -d 'grant_type=client_credentials' http://localhost:8000/gigy/oauth/token                                                                   demo ✱

{"access_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJ0cmFkZSJdLCJleHAiOjE1MTI3MDk4MjksImp0aSI6IjFkZGJjZjU5LTI4OGYtNDc3OS04MGRiLWMxMGFkYWUwZjQ5YiIsImNsaWVudF9pZCI6ImdpZ3kifQ.QMP5493G2Z9q3tYZ2Jnc8bSn2X6zs-Sbr3EYpq7B7A0","token_type":"bearer","expires_in":3599,"scope":"trade","jti":"1ddbcf59-288f-4779-80db-c10adae0f49b"}

## Request api with jwt token
curl -i -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJ0cmFkZSJdLCJleHAiOjE1MTI3MDk4MDUsImp0aSI6IjZlMzU5NjU0LTQ4YWQtNDI3My04OGU2LWM5YzZmZTM1M2MyMSIsImNsaWVudF9pZCI6ImdpZ3kifQ.-bsiv3L2vMC0YlhB-enqp0cDCuroM1kI5bT016n_1dc" -X GET http://localhost:8000/gigy/info

## LICENSE

The code is released under the Apache License 2.0. See LICENSE for details.
