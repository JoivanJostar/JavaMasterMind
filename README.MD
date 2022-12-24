不知道国外哪个大学的Java作业，用Socket写一个联机小游戏，游戏叫Master Mind(珠玑妙算)，就是一个猜球小游戏。大概是2022年11月份接的单子，那天晚上和室友去西安泽西岛洗脚去了，刚消费完就来活了。

支持的功能：游戏大厅里有提前设置好的游戏房间，多玩家可以同时在线游玩，想在一块玩的话就加入一个房间，房间满了自动开局，谁先在12轮里才对球就赢了，具体规则见assignment2.PDF

服务器架构上，为每一个游戏房间设置一个服务主线程，该主线程再为每个房间里的客户端单独建立一个线程和一个TCP长连接进行游戏服务的交互。并且所有游戏交互线程的生命周期由房间服务线程负责。

服务器会记录那些中途逃跑的玩家，并为每个房间维护一个计分板，当一场游戏结束后，所有玩家可以查看计分板了解游戏排名。

头一回启动客户端需要注册登录才能进入服务器玩游戏。



PS:这个单子的客户态度及其不好，感觉就不会好好说话，对自己说出的东西一点不负责，搁那信口雌黄，中介也不靠谱，给的预算也贼少。



Brief
This program implement a simple network MasterMind Game by java Socket.
It has the following features:

1. You can play with other players in a game room maintained by Server.
2. Mutiple groups of players can play at the same time in different game rooms.
3. You can browse the rooms of Server, and select which room you join in to play
4. The first one who guesses the secret code right in the shortest time will be the winner.
5. The server create a independent thread for each player to play and use a scoreboard to
  record the completion of each player.
6. The server will record those players who escape in the game!
  Files:
  Server/
  ServerMain.java //the main driver
  GameHall.java //the server that bind the port and handle the requests from clients
  GamerLauncher.java // the game launcher for a game match
  PlayGame.java //the class that implement the game logic
  GameRoom.java // class GameRoom
  Player.java //the player class (player id, player name…)
  Rules.java //the game rules
  GameConfigureation.java //the configuration of colors and max guess times
  ScoreBoard.java //the scoreboard class, it is a list of ScoreBoradItem
  ScoreBoradItem.java //the item of scoreboard.
  SecretCodeGenerator.java // help to generate the code
  CommonResult.java // the unified encapsulation of communication data format
  Client/
  ClinetMain.java // the client that interacts with server
  InputUtility.java //help to verify the input from the client’s standard input stream
  Vaild.java // help to verify the guess input when playing game.
  GameRoom.java // same as above
  Player.java // same as above
  GameConfigureation.java // same as above
  ScoreBoard.java // same as above
  ScoreBoradItem.java // same as above
  CommonResult.java // same as above
  How to Run
  Run server first:
  The command is
  java ServerMain
  After the server started, run the clients:
  java ClientMain server_ip is_test_mode
  where the argument server_ip is the ip address of server machine. is_test_mode means if run
  in test mode ( you can see the secret code while playing game),
  is_test_mode=1 means using the test mode. Otherwise not use.
  E.g: java ClientMain 192.168.1.5 1
  After you start the client.
  It will allow you input a player name , by witch other players could recognize you.
  please input your player name (at most 20 characters):
  testPlayer
  then the client terminal will print a select menu ,and you can now choose to get the game
  rule or join to play:
  welcome testPlayer
  please select the menu:
  [1] GetRule
  [2] JoinGame
  [3] Quit
  1
  Here I want to get the game rule, so I type 1 , then the client will request the game rule from
  server and print it to you:
  Welcome to Mastermind. Here are the rules.
  The computer will think of a secret code. The code consists of 4
  colored pegs. The pegs MUST be one of six colors: blue, green,
  orange, purple, red, or yellow. A color may appear more than once in
  the code. You try to guess what colored pegs are in the code and
  what order they are in. After you make a valid guess the result
  (feedback) will be displayed.
  The result consists of a black peg for each peg you have guessed
  exactly correct (color and position) in your guess. For each peg in
  the guess that is the correct color, but is out of position, you get
  a white peg. For each peg, which is fully incorrect, you get no
  feedback.
  The result consists of a black peg for each peg you have guessed
  exactly correct (color and position) in your guess. For each peg in
  the guess that is the correct color, but is out of position, you get
  a white peg. For each peg, which is fully incorrect, you get no
  feedback.
  Only the first letter of the color is displayed. B for Blue, R for
  Red, and so forth. When entering guesses you only need to enter the
  first character of each color as a capital letter.
  Then I want to play game with others, so I type 2:
  please select the menu:
  please select the menu:
  [1] GetRule
  [2] JoinGame
  [3] Quit
  2
  Room ID:0 Room Name:RemoteGameRoom0 Room Size:1 playersNum:0 Gaming
  players in this room are:
  Bob
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Room ID:1 Room Name:RemoteGameRoom1 Room Size:2 playersNum:1 Free
players in this room are:
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Room ID:2 Room Name:RemoteGameRoom2 Room Size:3 playersNum:1 Free
players in this room are:
JOJO
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Room ID:3 Room Name:RemoteGameRoom3 Room Size:4 playersNum:0 Free
players in this room are:
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
When you type 2 to join game, then the client will request the room information from server
and print it on screen.
Here you can browse the rooms, and select a room to join in.
As seen above, there are 4 rooms in server, the Room 2 allows 3 player to play, and now a
player named JOJO has already in Room 2 waiting others to join in.
Room0‘s status is Gaming, that means, player named Bob is playing, if you want to join in
Room 0 , the server will refuse that request.
Here I want to play with JOJO, so I type the room id of Room 2:
please select the room id [0-3] that you want to join in. e.g:2
2
Join room success
Player testPlayer Join in the room RemoteGameRoom2, waiting other players to join in......
Now I have joined in Room 2, but room 2 needs 3 players to start the game. So I have to wait
another one to join in.
Here another player called COCO also joins in the Room2, now we can play together
Game starts .....
Generating secret code ...(for this example the secret code is OPPB)
You have 12 guesses left.
What is your next guess?
Type in the characters for your guess and press enter.
Enter guess: OPPB
OPPB -> Result: 4B_0W – You guess right !!
getting the scores from others.......
GameScoreBoard:
--------------------------------------------------------------------------------------------------------------------
id name status turns time_cost
3 COCO Lost 12 42s
0 testPlayer Finish 1 56s
2 JOJO Escape 3 69s
player id: 0 player name: testPlayer is Winner!!
After I finish the game, the Server will return the score board, which records the completion
of each player:
COCO Looses the game, he/she used all 12 times but did not guess right.
JOJO escape from the game (for example, he/she shut down the process)
testPlayer guesses right using 56 seconds, so, he won the game in the shortest time.
TRY AND PLAY ！