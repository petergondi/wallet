## Virtual Wallet
* Design and Flow
* Api Description 
* Installation
* Tools and Language used for Development
## 1.Api Design and Flow
* System Diagram
![System Diagram](https://i.postimg.cc/yN5K1TGK/Ontop-1.png)

## 2.Api Description
This api is a simple simulation of a virtual wallet and how to transfer money from the virtual wallet to account number
1) Create recipient account number;
2) Send a transfer request using using the accountId created
3) Query transactions in a filtered pattern 

## 1 Description:
- Summarized request paths and description for **recipient A/C creation**,**account transfer request** and **to query the transactions ordered by descending “creation date” in a paginated table and filtered by amount and date**

| Method | Path                                                                              | Description                                                                                                         |
|--------|-----------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------|
| POST   | /v1/account                                                                       | Creates a recipient account or account to transfer money to                                                         |
| POST   | /v1/transfer                                                                      | Initiates transfer of funds between the wallet and the recipient bank account                                       |
| GET    | /v1/transfer?amount={transAmount}&date={yy-MM-dd}&page={startPage}&size={endPage} | Queries the transactions ordered by descending “creation date” in a paginated table and filtered by amount and date |


- ## Recipient A/C Creation
- Endpoint:/v1/account
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
- ## Wallet to Account Transfer
- Endpoint:/v1/transfer
- Request
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
- ## Query Transactions
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