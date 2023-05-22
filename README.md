## Virtual Wallet
* Design and Flow
* Api Description 
* Installation
* Tools and Language used for Development
## 1.Api Design and Flow
* System Diagram
![System Diagram](https://i.postimg.cc/yN5K1TGK/Ontop-1.png)
* **System Diagram Flow Explanation** 
- The virtual wallet is a simple digital wallet that holds users' virtaul money that they can later choose to transfer to their mobile account
* Diagram Explanation(The numbers indicates the order of flow in the diagram)
- 1.The end user initiates transfer request from their wallet to their bank account
- 2.The wallet Service receives the request and initiates a debit request to the wallet
- 3.The wallet service persist the information to the database
- 4.The wallet service then adds the request to the queue for bank account crediting
- 5.The Event Driven Consumer consumes the message from the queue
- 6.The Event Driven Consumer sends crediting request to the external bank transfer Api
- 7.The Event Driven Consumer receives the request back of the crediting result
- 8.The Event Driven Consumer updates the corresponding transaction in the database appropriately
* Refund and Retrial Section(Not Handled for now)
- Refunds are supposed to be done if any of process 4,5,6 or 7 fails
- A retrial mechanism may also be implemented before a refund is done especially if process 6 fails
## 2.Api Description
This api is a simple simulation of a virtual wallet and how to transfer money from the virtual wallet to account number
1) Create recipient account number;
2) Send a transfer request using using the accountId created
3) Query transactions in a filtered pattern 

### Description:
- Summarized request paths and description for **recipient A/C creation**,**account transfer request** and **to query the transactions ordered by descending “creation date” in a paginated table and filtered by amount and date**

| Method | Path                                                                              | Description                                                                                                         |
|--------|-----------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------|
| POST   | /v1/account                                                                       | Creates a recipient account or account to transfer money to                                                         |
| POST   | /v1/transfer                                                                      | Initiates transfer of funds between the wallet and the recipient bank account                                       |
| GET    | /v1/transfer?amount={transAmount}&date={yy-MM-dd}&page={startPage}&size={endPage} | Queries the transactions ordered by descending “creation date” in a paginated table and filtered by amount and date |


- #### Recipient A/C Creation
- Endpoint:/v1/account
- Field Descriptions

| Field         | Description                  | required |
|---------------|------------------------------|----------|
| firstName     | recipient bank A/c firstname | Yes      |
| lastName      | recipient bank A/C lastName  | Yes      |
| routingNumber | Bank routing number          | Yes      |
| nationalId    | recipient national id no     | Yes      |
| accountNo     | recipient Bank A/C number    | Yes      |

- Request
```
{
     "firstName":"Tony",
     "lastName":"Stark",
     "routingNumber":"211927207",
     "nationalId":"34532456",
     "accountNo":"1885226712"
 }
```
- Response
```
http status **201**
{
    "accountId": 52,
    "firstName": "Tony",
    "lastName": "Stark",
    "routingNumber": "211927207",
    "nationalId": "34532456",
    "accountNo": "1885226712"
}
```
- #### Wallet to Account Transfer
- Endpoint:/v1/transfer
- Request
- | Field     | Description                                                   | required |
  |-----------|---------------------------------------------------------------|----------|
  | accountId | accountId created when creating account                       | Yes      |
  | userId    | userId identifying the owner of the wallet to debit or credit | Yes      |
  | currency  | The currency to be used in transaction                        | Yes      |
  | amount    | The amount to be transacted                                   | Yes      |
  
```
http status **201**
{
     "accountId":"54",
     "userId":"1000",
     "currency":"USD",
     "amount":455
}
```
- Response
```
{
    "transactionId": 253,
    "status": "RECEIVED",
    "amount": 450.45,
    "statusDescription": "Transaction Accepted for processing!"
}
```
-this request should make you see an extra message into the queue as shown below
![System Diagram](https://i.postimg.cc/5yGnZmqR/Screenshot-2023-05-22-at-14-57-59.png)
- #### Query Transactions
- Endpoint:/v1/transfer
- Request
```
/v1/transfer?amount=450&date=2023-05-21&page=0&size=10
```
-Response
```
http status **200**
{
    "content": [
        {
            "walletTransactionId": 104,
            "userId": 1000,
            "amount": 450.00,
            "accountId": 1,
            "newAmount": 445.50,
            "status": "COMPLETED",
            "transFee": 4.50,
            "createdAt": "2023-05-21T19:27:34.992324",
            "updatedAt": "2023-05-21T19:27:35.515083"
        },
        {
            "walletTransactionId": 103,
            "userId": 1000,
            "amount": 450.00,
            "accountId": 1,
            "newAmount": 445.50,
            "status": "COMPLETED",
            "transFee": 4.50,
            "createdAt": "2023-05-21T19:27:33.011539",
            "updatedAt": "2023-05-21T19:27:33.541893"
        },
        {
            "walletTransactionId": 102,
            "userId": 1000,
            "amount": 450.00,
            "accountId": 1,
            "newAmount": 445.50,
            "status": "COMPLETED",
            "transFee": 4.50,
            "createdAt": "2023-05-21T19:27:29.710534",
            "updatedAt": "2023-05-21T19:27:30.587678"
        },
        {
            "walletTransactionId": 1,
            "userId": 1000,
            "amount": 450.00,
            "accountId": null,
            "newAmount": 445.50,
            "status": "COMPLETED",
            "transFee": 4.50,
            "createdAt": "2023-05-21T18:27:02.776573",
            "updatedAt": "2023-05-21T18:27:03.35347"
        }
    ],
    "pageable": {
        "sort": {
            "empty": false,
            "unsorted": false,
            "sorted": true
        },
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 10,
        "paged": true,
        "unpaged": false
    },
    "totalPages": 1,
    "totalElements": 4,
    "last": true,
    "size": 10,
    "number": 0,
    "sort": {
        "empty": false,
        "unsorted": false,
        "sorted": true
    },
    "numberOfElements": 4,
    "first": true,
    "empty": false
}
```
# 3.Installation Guide
This guide provides step-by-step instructions to install and run a Spring Boot application with a PostgreSQL database and ActiveMQ locally.
- Prerequisite
  Before proceeding with the installation, make sure you have the following prerequisites installed on your system:

- Java Development Kit (JDK) 8 or later
- Apache Maven
- PostgreSQL database
- ActiveMQ messaging broker
## Installation Steps
1) Clone the repository:
```
  git clone <repository url>
```
2) Configure the PostgreSQL database:
   - Create a new database called **wallet** in PostgreSQL for the application.
   - Update the database configuration in the application.properties file:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/wallet
spring.datasource.username=<database_username>
spring.datasource.password=<database_password>

```
3) Configure ActiveMQ:
  - Install and start the ActiveMQ messaging broker.
  - Update the ActiveMQ configuration in the application.properties file:
```
spring.activemq.broker-url=tcp://localhost:61616
spring.activemq.user=<user>
spring.activemq.password=<password>
spring.activemq.packages.trust-all=true
```
4) Build the application using Maven:
```
   mvn clean package

 ```
5) Run the application
```
java -jar target/<jar_file_name>.jar
```
6) You can now access the host through http://localhost:8080 and append the endpoints to send the request
   i.e http://localhost:8080/v1/account
## Postman Collection Link
https://interstellar-sunset-5393.postman.co/workspace/ds~6b5edbc7-d399-49f2-9c00-f3b47f2b8a22/collection/4932219-76120378-e128-4ea4-ab31-851c15a5e9e3?action=share&creator=4932219

# 4.Tools and Language Used for Development
- Java 17
- 3.1.0
- Postgres Database
- Git
- ActiveMq(For queueing transactions)
