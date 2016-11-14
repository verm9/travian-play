# travian-play
Game process automation application for a browser game Travian T4.2. Works on <a href="http://aspidanetwork.com">AspidaNetwork</a>. Servers with game speed x50000+ may be prefered.

The app is based on layers architecture. There can be distinguished two sub-apps: Central that is responsible for working with game server and Spring MVC that is used for controlling the first. The Central is working on HTTP level by using decorated JSoup library. Spring security is used to provide auth by a passphrase.

To run:
- Setup tomcat and run.
- Provide login data for aspidanetwork.com by modifying /resources/spring-app.xml. In another case you will be asked for this data after app started.
- Enter passphrase to enter: <b>vermilion</b>
- You will be redirected to main page where you can send commands to the Central.
- Press "Start" button to run Central.
P.S. to build something in "dorf2" buildings should be loaded by Central first (which will happen after Cental is started).

Screenshots:
https://drive.google.com/file/d/0B2zO_WwnzlhYN3VjQkptSGNIOW8/view?usp=sharing
https://drive.google.com/file/d/0B2zO_WwnzlhYTk0zcDU1cWh5RDQ/view?usp=sharing
