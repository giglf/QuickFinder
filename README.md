## QuickerFinder

A tool for files searching.

Writing by Java, can be worker in both windows and linux.

I don't have Mac but it will work in Mac in theory :-)



## Setting with Mysql

`CREATE USER 'quickFinder'@'localhost' IDENTIFIED BY 'thePasswordThatEasyKnow';`

`FLUSH PRIVILEGES;`

`CREATE DATABASE quickfinder;`

`GRANT ALL PRIVILEGES ON quickfinder.* TO 'quickFinder'@'localhost';`

