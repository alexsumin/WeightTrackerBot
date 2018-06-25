# Weight Tracker Bot
>This simple bot helps you to monitor your weight, presenting the results as a chart.

[![CircleCI](https://circleci.com/gh/alexsumin/WeightTrackerBot.svg?style=svg)](https://circleci.com/gh/alexsumin/WeightTrackerBot)

What is this bot designed for? There are many different applications for weight control, but if you only need to save changes and sometimes see the statistics, I think the telegram bot is the best option.
Try it out [here](https://t.me/WeightMonitorBot)

###Built With
* Java 8
* [Maven](https://maven.apache.org/) - Dependency Management
* [MySQL](https://www.mysql.com/) - Database
* [Telegram Spring Boot Starter](https://github.com/xabgesagtx/telegram-spring-boot-starter)
* [Project Lombok](https://projectlombok.org/) - Library

###How to use
1. Clone this project
2. You need to register on Telegram.
Open Telegram app, search for [@BotFather](http://t.me/BotFather) and start the chat. 
Send /newbot command and follow the instructions. 
After completing the initial steps, you’ll get your token and username.
Сhange the config values for bot.token and bot.username in the application.properties to the credentials of your bot 
3. Configure the [database](https://dev.mysql.com/doc/mysql-getting-started/en/).
Сhange the config values for database in the application.properties

And then you're ready to compile

`mvn clean install`

And run

`java -jar WeightTrackerBot.jar`

###How to use on the server
There is an extra step. You have to install [Xvfb](https://en.wikipedia.org/wiki/Xvfb). It needs for chart generating. 

In Ubuntu:

`sudo apt install xvfb`

And just run jar file with command

`xvfb-run java -jar WeightTrackerBot.jar`

It works fine on my [Digital Ocean VPS](https://m.do.co/c/f9feb8c41e77)
###Bot commands
After the running, you can send commands to the bot
>* To add a new measurement, simply send to the bot this value
>* To view a chart, send to the bot '/chart'
>* To get statistics, send to the bot '/stat'
>* To delete the last data value, send to the bot '/delete'
>* To get help, send to the bot '/help'

###Credits
* [Curve fitting and styling AreaChart](http://fxexperience.com/2012/01/curve-fitting-and-styling-areachart/)
* [Java-FX for server-side image generation](https://stackoverflow.com/a/18483029)
* Icon used for bot made by <a href="https://www.flaticon.com/authors/itim2101" title="itim2101">itim2101</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>