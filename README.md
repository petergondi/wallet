## Virtual Wallet
* Design and flow
* Api Description and Specs
* Installation
* Tools and Language used for Development
## 2.Introduction
This api is a simple simulation of a virtual wallet and how to transfer money from the virtual wallet to account number
1) Create recipient account number;
2) Send a transfer request using using the accountId created
3) Query transactions in a filtered pattern 

## 1 Description:
- Summarized request paths and description for contact creation

| Method | Path             | Description                                            |
|--------|------------------|--------------------------------------------------------|
| GET    | /contacts        | Returns a paginated list of all the requested contacts |
| POST   | /contacts/create | Adds new contact                                       |
| GET    | /contacts/{id}   | Retrieves the full details of a single contact              |

- Summarized request paths and description for loan request and repayment
s config file