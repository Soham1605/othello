
# Othello Game Application

This is a full-stack **Othello (Reversi)** game developed using Java **Spring Boot** for the backend and **Java Swing** for the frontend. It supports **Single Player** (vs Computer) and **Multiplayer** modes.

---

## Project Details

- **Backend:** Java Spring Boot, Spring Security (JWT Authentication), Hibernate JPA, PostgreSQL
- **Frontend:** Java Swing (desktop GUI)
- **Authentication:** Secure login/registration using **BCrypt password hashing** and **JWT tokens**
- **Game Mechanics:** 
  - Play against a **Computer AI** (Basic / Intermediate / Advanced)
  - Play **Online Multiplayer** (via invite system)
  - **Wins-Losses-Draws** statistics are tracked
- **Deployment Ready:** Can be hosted with external Postgres DB and a lightweight Spring Boot server

---

## 🚀 How to Run Locally

1. **Clone this repository:**
   ```bash
   git clone https://github.com/Soham1605/othello.git
   cd othello

2. **Configure Database:**
   - Open src/main/resources/application.properties
   - Replace database username and password:
     ```bash
     spring.datasource.username=your_postgres_username
     spring.datasource.password=your_postgres_password

3. **Run Backend:**
    ```bash
     mvn spring-boot:run
 - Backend runs on http://localhost:8081

4. **Run Frontend:**
   - Run the Main class (com.othello.othello.ui.Main.java) to launch the Swing application.
  

## What you will see:
- **Login/Registration Screen:** Can register new credentials (username, password) and login with existing credentials. We use Java Web Tokens for Authorization.
  
![image](https://github.com/user-attachments/assets/bdac0fb7-33c7-458c-beaf-97d51bcf6552)
![image](https://github.com/user-attachments/assets/6e1e2145-41bc-49dd-890d-23be8bc9d5d4)

- **Home Screen:** Can see the home screen for the application that allows user to play with computer/online and also shows Wins/Losses/Draws records
  
   ![image](https://github.com/user-attachments/assets/9d960070-6f67-4216-ab31-53ebbbe01db0)

- **Play with Computer:** We can select difficulty between Basic, Intermediate, and Advanced.

  The logic used for these difficulty levels is relatively simple. The idea of playing Othello is to **capture the corners** before the other player can, while also trying to **capture most amount of flips**. The **Basic model** randomly selects its moves, the **Advanced model** will try to capture corner when it can and try to flip the most when it can't. The **Intermediate model** does both with 50% probability. A human can still beat all 3 levels of computer.

  ![image](https://github.com/user-attachments/assets/ad6411f9-890a-402a-a4c8-1aefebe3e8c1)

  This is an 8*8 board with black and white pieces arranged as shown. Black always plays first so the user is assigned black. The hollow circles indicate **valid** moves and the user must click on those circles.

  ![image](https://github.com/user-attachments/assets/a0c87669-ef35-466f-9efe-d2424345c9c2)

  If the user selects a hollow circle out of turn, it won't be registered and the user will see this popup:

  ![image](https://github.com/user-attachments/assets/d737d29d-25ac-4b8c-a5bd-e056b492701b)

  Upon completion of game, game summary is shown as follows:

  ![image](https://github.com/user-attachments/assets/da571adf-c480-46a9-b449-b7134248f467)

- **Play Online:** We can either create or join a game. This is an invite based multiplayer system and not a lobby. This means that a player creates a game, the backend provides a code that is shared to the other player by our user. The other user joins the game with the given code.

  ![image](https://github.com/user-attachments/assets/ad59e39d-4366-4580-887b-04431d621c59)

  Creating a game launches a code from the backend that must be shared by the user to the other player (via text or by word .etc)

  ![image](https://github.com/user-attachments/assets/89246f15-f2fb-467d-97fd-c888d3c535ee)

  As the user wait for the other player to join the game, this is what he/she sees:

  ![image](https://github.com/user-attachments/assets/8d338a04-155b-4e44-9f23-e749847d7236)

  The other player enters the game by clicking Play Online -> Join Game -> and then he enters the shared invite code.

  ![image](https://github.com/user-attachments/assets/de41f090-ca85-4e4a-af47-567acd4a2a3b)

  The two windows indicate the boards of both players. The **user who creates the game is assigned Black** and always starts the game. The **user who joins is assigned White** and must follow. The usernames and the count of their corresponding pieces can be found at the top of the screen.

  ![image](https://github.com/user-attachments/assets/a78c48d5-919e-49c8-962a-92d772969765)

  The game status is the same for both players as the game progresses, with their corresponding valid moves shown to them.

  ![image](https://github.com/user-attachments/assets/f963766a-a276-4c67-b668-8877870e46a3)

  If a player plays out of turn, a popup tells them so.

  ![image](https://github.com/user-attachments/assets/59c47360-5849-4c3a-a563-a8e800be9821)

  As the game ends, the game summary is shown as follows:

  ![image](https://github.com/user-attachments/assets/f67ce63a-44cd-441c-9d57-bbc1ec71b383)

  Upon exiting from the boards, we go back to home window with **updated Wins/Losses/Draws ratios**. You will see that the user we just created has lost the game so his W/L/D ratio went from being 0/0/0 to 0/1/0.

  ![image](https://github.com/user-attachments/assets/8af5175e-a179-46e6-a059-435dc4b33077)


## Project Structure

- src/main/java/com/othello/othello
    - controller/ (REST Controllers)
    - model/ (JPA Entity Models)
    - repository/ (Database Access)
    - service/ (Business Logic Services)
    - security/ (JWT and Spring Security)
    - ui/ (Java Swing Frontend)
- src/main/resources
    - application.properties (Database Config)
 

## Known Limitations

- Java Swing UI requires desktop environment (cannot run in browser).
- Multiplayer is tested locally; deploying real-time multiplayer online would need WebSocket upgrade.
- No dynamic matchmaking yet (only invite-based).


## Features

-  User Registration & Login (JWT Authentication)
-  Play vs Computer (with AI difficulty)
-  Online Multiplayer with Real-Time Sync
-  Win-Loss-Draw Records
-  Secure Password Hashing (BCrypt)



