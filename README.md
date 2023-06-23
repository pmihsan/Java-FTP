# JFTP
Advanced multithreaded FTP console application with user-password based login configuration, automatic log management and directory traversal functionalities and support for basic commands.

#### Commands (case insensitive)
| Commands | Uses                                                                                           |
|----------|------------------------------------------------------------------------------------------------|
| list     | list the contents of the current directory                                                     |
| cd       | Used to change to the specified directory. Can also be used to change into previous directory. |
| get      | Used to get files from the server. Can receive multiple arguments but not directories.         |
| put      | Similar to get, used put files on to the directory.                                            |
| pwd      | Tells us the present working directory.                                                        |
| whoami   | provides us with the username with which we have logged in.                                    |

### Usage
#### For Server
```java
    java SERVER PORT
```
* PORT - To run the server on specified PORT

#### For Client
```java
    java CLIENT USER@DOMAIN PORT
```
* USER - The user through which we log in to the server.

* DOMAIN - Domain name or IP address on which the server is running.

* PORT - To connect to the server running on specified PORT.

### Features
* Automatic log management on server startup, user authentication and command execution.
* After first startup of the server you can add other users to the server.
* Passwords are stored and transferred are encrypted using SHA-256 Hash.

### Easy Use
Download the latest release [jftp-2.0](https://github.com/pmihsan/Java-FTP/releases/tag/v2.0.0).
Instead of having all the source files, this [FTP.jar](https://github.com/pmihsan/Java-FTP/releases/download/v2.0.0/FTP.jar) is a precompiled version of all the class files. You could download this file and use the same as
```java
    java -cp FTP.jar {SERVER || CLIENT}
```
with the above-mentioned arguments for either the server or the client.

###### In case if there is a mismatch between the precompiled version and your java jdk, you have download all the source files and compile it again.