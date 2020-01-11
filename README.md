# Development Test

## Development

To start your application in the dev profile, run:

    ./mvnw
    
or via dockerfile

    # docker build -t development_test .
    # docker run -it -p 8080:8080 development_test
    
## Production

    # docker-compose -f docker-compose.production.yml up
