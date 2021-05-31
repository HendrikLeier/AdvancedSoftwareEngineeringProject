In order to be able to run this project you must fulfill only two requirements:

1. Docker is installed
2. Docker-Compose is installed

Well it comes without saying that installing those requires "sudo" or "admin" or whatever the permission is called on your system.

First you run "docker-compose build" with maximum privileges.
Next you run "docker-compose up" with maximum privileges and it should start right up.

Depending on your machine startup can take well over a minute.

To test the app use postman and query the routes from the documentation.

The port that things are being served on ist 8080.

Installation guide Docker: https://docs.docker.com/engine/install/
Installation guide Docker-Compose: https://docs.docker.com/compose/install/

Note: This was tested on Ubuntu 20.04 LTS
