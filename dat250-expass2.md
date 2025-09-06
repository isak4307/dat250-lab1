# <center> Dat250 lab report
# <center> Isak Yau

In this lab, I had to create a simple REST poll application.  
At the start, I used a lot of time to figure out how I would model the different
domain entities. After all, there weren't very descriptive information on how we 
should have modeled, it and how to link their relationships.  
I know that I spent too much time trying to figure out a "perfect" model structure 
for this application. This ended up with me starting to code the logic behind the application 
very late in the week.  
Looking back, there were definitely some implementations I regret using, such as atomicInteger as  unique id.  
This meant that I had to assign ID's somewhat manually, which was very difficult and tough to test.
Another implementation is the ActionClasses I had. They served no purpose other 
than very simple actions which could have been done in the pollmanager class.
Finally, my return values was often Strings, which made it very tough for me to create tests.  


In this lab, I did not encounter any problems regarding installtion
of different dependencies and softwares. Everything was running well.  

The application has no pending issues so far.   
It is able to fulfill the requests from the test scenarios, which means that it is an MVP.  
But I didn't use all the @JSON-annotations that were given to us, which might affect the 
relationship of the different entities.  

