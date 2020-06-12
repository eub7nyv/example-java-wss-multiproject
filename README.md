# WSS Server and Client Example
Example of using websocket library in Java - both producer and consumer

### Server
Set the `WSS_PORT` environment variable to the port the WSS should run on (default `9001`).

### Client
Set the `WSS_PORT` to the port where the server is running (default `9001`) and set the `WSS_HOST` to the hostname of the WSS (default `localhost`).

## Docker compose
The `docker-compose.yml` will startup an instance each of the client and a server.

### Build
_from the root dir_

`mvn` => will build both projects

`docker-compose build` => will build all images

### Run
`docker-compose up` => runs single instance of the publisher and client

### Run with multiple clients
`docker-compose up --scale client=5` => runs one publisher and 5 clients

# License
This is licensed under Apache-2.0
