# Twitter Reatime trends 

## Find the top 10 trending tweets containing
 * star wars
 * real madrid
 * Justin Bieber
 
## Solution description

The solution is based on Akka and a set of four actors that

 * Connect to twitter's streaming api via twitter4j 
 * Parse tweets and extract hashtags
 * Accumulate partial results hashed by the hashtag's first letter 
 * Gather and print full results every 10 seconds
 
 
## How to run

The executable depends on four environmental variables being set

 * CONSUMER_KEY
 * CONSUMER_SECRET
 * ACCESS_TOKEN
 * ACCESS_TOKEN_SECRET
 
```
% mvn clean install
% export CONSUMER_KEY="xxx"; export CONSUMER_SECRET="xxx"; export ACCESS_TOKEN="xxx"; export ACCESS_TOKEN_SECRET="xxx" ; java -jar target/data-eng-test-1.0-SNAPSHOT-jar-with-dependencies.jar
```