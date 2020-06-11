# WSS Server and Client Example
Example of using websocket library in Java - both producer and consumer

## Default
Default designation is to be neither client or server.

### Server
Set the `WSS_SERVER` environment variable to the port the WSS should run on.

### Client
Set the `WSS_SERVER_PORT` to the port where the server is running and set the `WSS_SERVER_HOST` to the hostname of the WSS.

### Run both
Set the `WSS_SERVER_HOST` to localhost, and the `WSS_SERVER` to the port desired.

## Dockerfile
The `Dockerfile` can be used to build and run the app in a container
`docker build -t foo:tag . && docker run -it --rm foo:tag`

## Docker compose
The `docker-compose.yml` will startup two instances, a client and a server.
`docker-compose up`

# License
This is licensed under Apache-2.0
