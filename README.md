# Delivery Fee Service

Spring Boot app for the Fujitsu trial task. It imports weather into H2 and calculates the delivery fee for a city and vehicle type.

Run:
`gradle bootRun`

API example:
`curl "http://localhost:8080/api/v1/delivery-fees?city=TALLINN&vehicleType=BIKE"`

Weather import runs on startup and then by cron. Default schedule is hourly at `HH:15`, configurable in `application.yml`.
