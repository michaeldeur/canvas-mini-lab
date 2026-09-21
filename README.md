# 04.01: Mini-Lab Warmup 

* Author: Michael Deur
* Class: CS408
* Semester: Fall 2026

## Project Title: Canvas Assignment and GPA Dashboard

## Description

This program lets users easily calculate their GPA based by checking and 
unchecking which classes to be used to weigh against it. Additionally, 
the program displays your profile as well as making it easy to find 
assignments from each class, their points, and due dates.

## API Endpoints Used

### API Endpoints Used

| Method | Endpoint | Description & Data Retrieved |
| :--- | :--- | :--- |
| **GET** | `/api/v1/users/self/profile` | Retrieves the authenticated student's profile information, including display name and avatar URL for the dashboard header. |
| **GET** | `/api/v1/courses?enrollment_state=active&include[]=enrollments&include[]=total_scores` | Fetches all active enrolled courses along with total percentage scores used for interactive GPA calculation. |
| **GET** | `/api/v1/courses/:id/assignments` | Retrieves all assignments for a selected course, including title, due date, points possible, and Canvas direct links. |

## Reflection

Overall, this project was pretty enjoyable, and quite a fun learning 
experience. Before this project, I didn't know that it was possible to 
build and use data from Canvas for working on personal projects. Using a
complete tech stack for a larger project was also a fun as, this is also 
one of the few times I have been able to get a tech stack properly 
working, let alone a complete tech stack. The most enjoyable part was 
planning and customizing what the application was going to do as well as 
trying to figure out and identify any issues with the current Canvas that 
I wanted to solve. Two big issues for me were selectively being able to 
calculate GPA and easily see what and when assignments are due in each 
class. So, my program aims to solve those issues in a readable way.

Even though it was fun, the project was not very easy and I ran into a 
few challenges along the way. One challenge I ran into was trying to 
figure out how data transfer objects worked as well as why they were 
needed for the endpoints I was using. I was able to get this figured 
out though, and my knowledge in that area is a little bit better. Another 
big hurdle I ran into was understanding how to bridge between the
front end and backend using a framework, in this case Thymeleaf. I think 
there is still alot of capability here I am not taking advantage of and 
hope to learn in future projects, but I was able to figure out how to 
implement what I needed for my program after running into probably way 
to many errors.

There is actually alot that I want to improve on this site if I had more 
time. I would like to be able to not just calculate not just semester GPA 
but also calculate total cumulative GPA if possible, as well as potentially
be able to show a visual graph of what your GPA has looked like in 
college to be able to allow students to see if they are improving in the 
big picture, and not just in the moment. Additionally, I would also like 
to be able to do more than just display assignment due dates and points, 
but also recommend personal due dates which days and amount of time 
students should focus on working on a project. Finally, it would be cool 
to be able to use the program to be able to turn itself in, but I'm not 
quite that confident in being able to make that functionality reliable 
yet. I have a lot of other minor tweaks and plans for this as well, but 
we'll about getting these other potential features sorted out first.

## Compiling and Using

To compile the project, run this in your terminal inside the project directory containing the Authentication.java file:
```javac Authentication.java```
```java Authentication```

To use the program follow the commands given in the menus, with R for register, L for login, P for reset password, and Q to quit the program.

## Results