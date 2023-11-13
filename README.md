## How to run

### Single application run
You can provide custom `SERVER_PORT` and `SENSOR_NAME` via run configuration if it's required or also provide it via `application.yml`:
```yml
server:
  port: {value} - optional

sensor:
  name: {value} - optional
  server:
    url: {value} - should match with server url
```
and also check `SERVER_URL` to send a request to a server.

### Multiple applications run
1. open `edit configuration` window.
2. clone existing configuration `n` times.
3. for each configuration provide unique `SERVER_PORT` and `SENSOR_NAME` values via env variables, example:

##### First instance
```
SERVER_PORT=8082;SENSOR_NAME=Sensor_1
```

##### Second instance
```
SERVER_PORT=8083;SENSOR_NAME=Sensor_2
```

and so on
