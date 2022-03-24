# Parsing for Cocktail-TelegramBot

# How it works
You should run remoteChromeDriver, check DB in config.properties and run project.
#remoteChromeDriver
- Настройка ChromeDriver
  [https://www.lambdatest.com/blog/run-selenium-tests-in-docker/](https://www.lambdatest.com/blog/run-selenium-tests-in-docker/)
-  docker pull selenium/standalone-chrome
-  docker run -d -p 4445:4444 --shm-size="2g" -v /dev/shm:/dev/shm selenium/standalone-chrome