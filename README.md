# Quarkus-Simple-REST

## Getting started
1. Clone the project
```
git clone https://github.com/raphael-97/Quarkus-simple-REST
```

2. Run in the root of this project
```
mvnw compile quarkus:dev

or 

docker compose up
```


3. In your browser, navigate to `http://localhost:8080/q/swagger-ui` to test the endpoints


### Endpoints


`GET /customers`

`GET /customers/{id}`

`POST /customers`

`PUT /customers/{id}`

`DELETE /customers/{id}`