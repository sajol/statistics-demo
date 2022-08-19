# HelloFresh JVM Take Home Test

Create an HTTP service for recording of statistics of some arbitrary data over
a closed period of time.

* [`POST /event`](#post-event)
  * [Example Payload](#example-payload)
* [`GET /stats`](#get-stats)
  * [Example Response](#example-response)
* [Testing](#testing)
* [Requirements](#requirements)
  * [Bonus Requirement](#bonus-requirement)
* [Review Criteria](#review-criteria)

## `POST /event`

This route receives 3 values separated by a comma (`,`) where:

1. _timestamp_: An integer with the Unix timestamp in millisecond resolution when the
   event happened. The data is not ordered by this timestamp, this means that
   you may receive old data in any row.
1. 𝑥: A real number with a fractional part of up to 10 digits, always in 0..1.
1. 𝑦: An integer in 1,073,741,823..2,147,483,647.

The response should be [202](https://httpstatuses.com/202) if the data was
successfully processed. Choose appropriate status codes for other circumstances,
also think about what to do if some rows are valid and others are not.

### Example Payload

```csv
1607341341814,0.0442672968,1282509067
1607341339814,0.0473002568,1785397644
1607341331814,0.0899538547,1852154378
1607341271814,0.0586780608,111212767
1607341261814,0.0231608748,1539565646
1607341331814,0.7796950936,1820653751
1607341291814,0.0876221433,1194727708
1607341338814,0.0302456915,1760856792
1607341311814,0.0554600768,2127711810
1607340341814,0.0360791311,1563887095
```

## `GET /stats`

Returns statistics about the data that was received so far. It **MUST** return
the data points that lie within the past 60 seconds separated by a comma (`,`):

1. Total
1. Sum 𝑥
1. Avg 𝑥
1. Sum 𝑦
1. Avg 𝑦

For 𝑥 a fractional part of up to 10 digits is expected. Choose appropriate
status codes for possible circumstances, also think about what to do if no data
was recorded so far.

### Example Response

```csv
7,1.1345444135,0.1620777734,11824011150,1689144450.000
```

## Testing

We included a little program in `producer.jar` for your convenience that should
aid you while developing your solution.

### Print Random Data

    java -jar producer.jar -m=console

Prints 50 lines of random data to standard output.

### Infinite Random Data

    java -jar producer.jar -m=http -p=8080

Produced infinite random data to `http://localhost:8080/event` as specified in
[`POST /event`](#post-event).

### Test

    java -jar producer.jar -m=test -p=8080

Sends the [example payload](#example-payload) to `http://localhost:8080/event`
and then calls `http://localhost:8080/stats` and expects the
[example response](#example-response) back.

> **NOTE** that the sum and average of 𝑥 are expected to be accurate up to the
> 5th fractional part.

## Requirements

1. Create a pull request to the master branch by finishing your code in a different branch.
1. Use [Kotlin/JVM](https://kotlinlang.org/) or Java
1. Use [Gradle](https://gradle.org/) or [Maven](https://maven.apache.org/)
1. API must be thread safe
1. API must accept concurrent requests
1. Project and tests must be buildable and executable
1. No databases (MySQL, Postgres, H2, HyperSQL, …)
1. No cleanup threads in any form

### Bonus Requirement

This problem can be solved in constant time and space
[_O_(1)](https://en.wikipedia.org/wiki/Big_O_notation). This means that the
amount of consumed memory, and the time it takes to calculate the statistics is
irrespective of the inputs.

**There will be bonus points if you can come up with such a solution.**

## Review Criteria

We expect that the assignment will not take more than 4–5 hours of work. In our
judgement we rely on common sense and do not expect production ready code. We
are rather interested in your problem solving skills and command of the
programming language that you chose. We may run the submission against different
input data sets, therefore the submission should be executable.

General criteria from most important to less important:

1. Functional and non-functional requirements are met.
1. Prefer application efficiency over code organisation complexity.
1. Code is readable and comprehensible. Setup instructions and run instructions
   are provided.
1. Tests are show cased (no need to cover everything).
1. Supporting notes on taken decisions and further clarifications are welcome.


## Background of solution

The goal here is to store statistics events and return statistics of 60 seconds. The operations should conform to O(1) space-time complexity.

Here LinkedHashMap has been used to store the statistics. LinkedHash acts as LRU cache which keeps the statistics from least recently accessed to most recently accessed.
It provides constant-time performance for the basic operations ( add, contains and remove). For our case, we will add statistics and get statistics.

For constant time space complexity our LinkedHashMap will store maximum 60 items at any given time.
To achieve this we have used this condition:
    
    size() > CACHE_SIZE
If at any time our cache size grows beyond 60 then the eldest element will be removed. This ensures constant space complexity


On the other hand when we get the statistics at any given time, It simply iterates over the copy of all the statistics in our cache.
Now here it will have to iterate over maximum 60 statistics at any given time. Because that is the maximum capability of our cache.


LinkedHashMap => Size(60)  Space: O(1) Traverse time: O(1)


Basically this is what has been done for storing statistics:

1. Convert the millisecond timestamp to second and use that as the cache key.
   1. If we get same timestamp or timestamp within 1000 range from each other they will be placed
      under same key. 1 sec equals 1000 millisecond.
2. After generating the cache key first try to check whether there is any statistics already there.
   1. If we find any statistics then aggregate the new one and store the updated value.
   2. Otherwise, keep the fresh statistics.
   3. Each statistics in the cache has its own lock. So, different threads will be able to modify all
      the different items concurrently.
   4. Concurrency has been considered for this step


For accessing the statistics:
1. Get all the statistics from the cache at any given time.
2. Copy all of them and produce aggregated result.
   1. Copy all the statistics so that other threads can continue modification on the statistics.
   2. While copying concurrency has been considered so that other threads can't modify it while reading.


***Note*** Only events that lie between 60 seconds time frame from the current time has been considered as there is no 
predictable ordering of the event timestamp. 

Also, it has been assumed that timestamp ordering is not mandatory here. So, it deletes least accessed item irrespective 
of its timestamp.

## Setup and run instructions
To build/test/run this project we need:
1. Java 11
2. Maven

If maven is not installed in local machine then no problem. Maven wrapper has been provided with this project.

#### Test
Basic unit test has been provided. This can be run using:

###### If maven is installed then

    mvn test

###### If maven is not installed then
    ./.mvnw test


#### Run
To run this project run the following commands

###### If maven is installed then

    mvn clean spring-boot:run

###### If maven is not installed then
    ./.mvnw clean spring-boot:run
