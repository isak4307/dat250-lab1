# <center> Dat250 lab 6 report

# <center> Isak Yau


In this lab, I encountered multiple different problems.  
One of the problems was the fact that my pc was unable to connect to the RabbitMQ server running in my Docker.  
Even though everything seemed right, from the messageconfig class to the correct dependencies, it just didn't work.  
I ended up getting help from Peter, which helped me debug the error messages I got.  
In the end, he was able to help me connect to the RabbitMQ Docker server. I believe it was because of the Serialization 
and Deserialization issues with using Jackson. And I had to use objectMapper and also wrap things in try-exception clauses.  

An issue that I realized during implementation/designing the action flow of my messagebroker was that some of my Controller methods
required that it would get back an object after the messagebroker sent a message to perform an action.  
This was because of my earlier design where I wanted to return an object everytime the action was successfully executed.  
However, the fact that messagebrokers are one-way communication channels, it was going to be tough to return an object 
after successfully performing that action.  
I ended up fixing my tests and cleaning up my code to accommodate to the messagebroker, which wasn't tough, but was tedious and annoying.   

Next issue I encountered was that my tests failed even though when manually tested, it gave the correct outputs.  
I had to search for a while before there was someone on StackOverflow that talked about RabbitMQ delivering messages 
asynchronously. This made me realize that the reason why I got 0 or null values was because of the test program continued to run
before the values were fetched.  
Thus, StackOverflow gave me the idea to wrap those calls within Awaitility wrappers, which is the same as the promise calls in JavaScript.  


There are no pending issues with this lab. It seems like everything works accordingly.
Only thing to note is the anonymous vote. This was not in my initial design, since I didn't allow anonymous voting.  
Thus, the statement that I might have to change my domain model to suit messagebroker was not relevant for me.

