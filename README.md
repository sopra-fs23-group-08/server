<!-- # SoPra RESTful Service Template FS23
edit for title  -->
<h1 align="center">
<br>
Yuker Tuker-Server
<br>
</h1>

## Introduction
Yuker Tuker is an exciting and competitive game that allows players to compete against each other in guessing a random YouTube video from a selected playlist, using a similar logic to poker. This is the back-end component of our project. The front-end part can be found [here](https://github.com/sopra-fs23-group-08/client).


## Technologies
The Java-written back end of this project utilizes the Spring Boot framework. Persistence is managed through JPA/Hibernate. Communication between the server and client is achieved through REST and websockets, with the additional usage of the STOMP messaging protocol for websockets. <!-- # recheck  -->

## Launch & Deployment

To help onboard a new developer joining our team, we have documented the necessary steps to get started with our application. Below are the commands required to build, run, and test the project locally:

### Build

To build the project, run the following command:

```bash
./gradlew build
```

This command will compile the source code, run tests, and package the application.

### Run

To run the project locally, execute the following command:


```bash
./gradlew bootRun
```

This will start the application on your local machine, allowing you to interact with it.
You can verify that the server is running by visiting `localhost:8080` in your browser.


### Test

To run the tests for the project, use the following command:


```bash
./gradlew test
```
This will execute the test suite and provide feedback on the application's functionality and reliability.


## Roadmap

- In-game chat 
- Automatic blind deduction
- Improved UI; structuring CSS code, replacing material ui components with custom ones

## Authors and Acknowledgment


SoPra Group 08 2023 consists of [Serafin Schoch](https://github.com/S3r4f1n), [Jonas Krumm](https://github.com/Dedphish),
[Yating Pan](https://github.com/YatingPan), [Rolando Villaseñor](https://github.com/RoVi80).

We would like to thank our teaching assistant [Sheena Lang](https://github.com/SheenaGit) for her help throughout the semester. We also thank Youtube for providing its API, and to the game of poker for inspiring our idea. This semester has proven to be both challenging and intriguing, offering us valuable opportunities for growth, as we acquired extensive knowledge not only in coding but also in teamwork and project execution. Even though none of us are doing Informatics as a major, we appreciate the opportunity and the experience gained from this project.

## License

GNU GPLv3






