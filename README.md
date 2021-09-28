# Jobcoin

Coin mixer PoC for Jobcoin.

In practice the mixer would read transactions from a distributed queue and would process all transactions made to registered
deposit addresses. It's sole purpose would be to anonymize transactions by splitting a transaction
into N smaller transactions. The resulting N smaller transactions would then be published to a distributed queue e.g. Kafka, Redis, etc. 
A second service, jobcoin poster, would read from the queue and handle posting all transactions.

This architecture clearly separates responsibilities of anonymizing & posting transactions. 
Additionally, it allows each operation to scale independently.

For this PoC, the mixer/anonymizer will both mix and post transactions.

## Quick Start

Getting started with Jobcoin mixer is easy:
* clone repository `git clone https://github.com/dan-iam/jobcoin.git`
* build docker image `docker build --tag jobcoin .`
* run container `docker run -it jobcoin`
* run jobcoin `java -Durl=http://jobcoin.gemini.com/rockslide-scope/api -DhouseAddress=house -jar jobcoin.jar`
    * java options are, just that, optional. The default values are as follows: `-Durl=http://jobcoin.gemini.com/rockslide-scope/api` & `-DhouseAddress=house` thus `java -jar jobcoin.jar` will do the trick!
* register addresses with the following cmd: `register -a <address1>,<address2>,<address3>` e.g. `register -a gemini,dan,c4227fe561414ded9b6f22c4ce1f4754`
* transfer jobcoin with the following cmd: `transfer -t <fromAddress>,<toAddress>,<amount>` e.g. `transfer -t gemini,dan,1000000`
* profit!! 💸 💸 💸

### Jobcoin Info
* API: https://jobcoin.gemini.com/rockslide-scope/api 
* APP: https://jobcoin.gemini.com/rockslide-scope 

## Limitations
* The current implementation of jobcoin mixer has the following limitation: all transactions must be whole numbers e.g. 1, 10, 100, etc.
* Application terminates on invalid commands 🤮

## TODO
* prevent visual ambiguity in generated addresses by removing uppercase "O", the number "0", uppercase "I", lowercase "l"
* persist all transactions e.g. FileSystem, mysql, etc.
    * perform final reconciliation on all transactions
* replace FileSystem storage with key-value store e.g Redis, DynamoDB, etc.
* integrate distributed queue for publishing
    * "TransferCmd" would read from queue and process transactions made to deposit addresses.
    * "publish" function would not actually post transactions, but merely publish to queue where additional service would read from queue and handle publishing of transactions.
* add command to create new jobcoins for an address
* fix "bug" causing application to terminate on invalid commands
   * IMHO should be able to fork kotlinx-cli and augment outputAndTerminate function (https://github.com/Kotlin/kotlinx-cli/blob/c073cbd5bf43fddfbef9d7790c8e8a90d1781fae/core/commonMain/src/ArgParser.kt#L179-L181)
