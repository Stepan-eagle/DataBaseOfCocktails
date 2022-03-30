# Parsing for Cocktail-TelegramBot


## How it works

Program gets cocktail data from russian bartender site https://barlist.ru/ by SelemiunWebDriver.

You should run remoteChromeDriver; check Database URL, Username, Password in config.properties and run project by Dockerfile or by IDE.

## remoteChromeDriver
- [ChromeDriver](https://www.lambdatest.com/blog/run-selenium-tests-in-docker/)

docker pull selenium/standalone-chrome

docker run -d -p 4445:4444 --shm-size="2g" -v /dev/shm:/dev/shm selenium/standalone-chrome 
