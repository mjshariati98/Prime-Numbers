# Distributed Prime Numbers Generator

## Calculation Algorithm
A prime number is a natural number greater than 1, which is only divisible by 1 and itself. To compute whether a number `n` is prime or not, A naive solution is to iterate through all numbers from 2 to sqrt(n), and for every number, check if it divides `n`. If we find any number that divides, then `n` is not prime.

To refine this solution a little bit, we can divide `n` by only prime numbers in the range [2, sqrt(n)] and not all numbers in that range (The reason is obvious, just think about the prime numbers' definition). 
For distributed computing, we do the same to determine whether a number `n` is prime. Suppose We computed the prime numbers in the range [1, n-1] and assigned each of the prime numbers in the range [2, sqrt(n)] to one of our clients (workers). Now, if each client divides `n` by all of its devoted prime numbers and all declare that `n` is prime, Then `n` is prime. But, if only one of them declares that `n` is not prime (`n` is divisible by one of its devoted prime numbers), Then `n` is not prime.

Notes:
- If a client finds out that `n` is divisible by one of its prime numbers, the client will stop its computing process and send the result back to the server. Also, as soon as the server finds out that `n` is not prime, it will send a message to all its clients to stop their computing.
- At the end, if all of the clients declare that `n` is prime, then the server will add `n` to its prime numbers list and save it in a file called `log.txt`. Also, this number will be assigned to the client with the minimum number of assigned prime numbers at that time.
- If storing prime numbers in clients is not possible due to storage limitations, we can store prime numbers only in the server, and clients get the prime numbers from the server each time. This approach has a lot of communication overhead and is not recommended.

## Remove a client
If a client gets disconnected from the server, the server will find out, and the number that was computing will compute again. Also, the server keeps the state of all clients and will scatter its prime numbers to the remaining clients by disconnecting one of them.

## Add new clients 
If a new client connects to the server, It will join the computing process immediately. But, rebalancing the previous prime numbers still needs to be implemented.

## Data loss on server downtime
if the server goes down, restarting it with the `log.txt` file will reload all computed prime numbers and can continue its task to find the next prime numbers. Also, it's possible to implement a mechanism to get all of the calculated prime numbers from clients rather than a log file.

## How to run the program
Everything is dockerized! You just need to build and run it.