# <center> Dat250 lab 5 report

# <center> Isak Yau

This lab was divided into two parts.  
The first part consisted of installing and being familiar with redis, which is a NoSQL database.  
After getting the basics familiar such as getting and setting objects into Redis,
the next part was to do so in this Poll application.  
I started initially using JSONGET and JSONSET to set the VoteOption object directly into the database.  
However, it was more tricky and tougher to maintain, not to mention that it had issues deserializing the
VoteOption object from the cache.  
I ended up switching to HSET and HGET. I also decided to simplify the storage, by only storing
the id's instead of the objects as a whole.

In this lab, I did encounter small problems regarding the installation of software and dependencies.   
First issue was how to download redis for Windows. It required me to use Docker which I was not familiar
with how to set up, and configure a container for redis.  
I had to spend some time searching up on my own.  
Another issue I encountered was my GitHub workflow.
This was tied to the fact that my tests used those methods that required redis running.  
Thus, I had to investigate whether there was a fix to having redis up and running when it was pushed to GitHub.

There are no pending issues with this lab. It seems like everything works accordingly.

