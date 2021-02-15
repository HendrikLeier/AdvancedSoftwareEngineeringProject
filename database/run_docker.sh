docker rm local-postgres
docker build -t local-postgres .
docker run -v postgres_data:/var/lib/postgresql/data -p 5432:5432 --name local-postgres local-postgres
docker stop local-postgres