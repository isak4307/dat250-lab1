# <center> Dat250 lab 3 report
# <center> Isak Yau

In this lab, I had to create a frontend structure which would call the different REST API I 
have created in the previous lab.  
In the beginning, I decided to use React as the frontend framework. It was a framework I had never used
and I thought about using it because it is one of the more popular frameworks. It started off alright, since
I mostly focused on the design of things, such as creating the forms, and structuring them using CSS.  
But once I started on the coding aspect such as posting and getting data from the backend, it started getting tough.  
React had different rules that I had to abid, which I not only didn't know about, but was tricky to understand.  
So at times, I would stumble upon errors related to how my components were set up, that I can't use hooks outside components
etc.  
However, I was able to solve it by having all the helper functions under the same main function for each component.  
It wasn't the prettiest, and I tried having functions outside the main component function whenever it was possible.  

Another main issue I encountered was saving session data. Since the previous lab didn't have a requirement for saving a
user's session, I had the issue of storing the id of a user when they were creating polls and voting for a poll.  
I ended up creating a log inn mapping for my backend, which would verify the user, but also give back their user id.  
This wasn't the best solution, but it currently works. I didn't know any other solution, other than actually having to study 
up on Spring on their storage of users, but also spending time on fixing/reworking my backend, which wasn't ideal, considering 
I knew how little time I had on each lab.  

In general, there weren't any technical problems encountered. I did however had to switch back to java 21 instead of java 24  
in order for my gradle to be able to work. In lab 2, it worked with java 24, but for some reason when I put all of the src code 
into a directory called backend, it stopped working and had issues. However, I was able to solve it after a couple of hours.

The application has no pending issues so far. It is able to satisfy the MVP, and everything should be working.  

The link to my code for experiment 1-2 above are here:  

- [creating poll, including the options](frontend/src/components/pollComponent/PollComponent.jsx)
- [voting on a created poll](frontend/src/components/showComponent/ShowComponent.jsx)