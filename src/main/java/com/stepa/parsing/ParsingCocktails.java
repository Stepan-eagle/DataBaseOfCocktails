package com.stepa.parsing;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ParsingCocktails {

    //запуск браузера
    public static WebDriver launchBrowser(String url, String chromeDriver) {
        WebDriver driver;
        System.setProperty(
                "webdriver.chrome.driver",
                chromeDriver);
        driver = new ChromeDriver();
        driver.get(url);
        return driver;
    }

    public static List<String> navHref (WebDriver driver){
        WebElement navTabs = driver.findElement(By.xpath("//body/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/ul[1]"));
        List<WebElement> navItem = navTabs.findElements(By.className("nav-item"));
        List<String> navLink = new ArrayList<>();
        for(WebElement webElement : navItem) {
            navLink.add(webElement.findElement(By.tagName("a")).getAttribute("href"));
        }
        System.out.println("Ссылка 1-ый справочник " + navLink.get(0));
        System.out.println("Кол-во ссылок " + navLink.size());
        return navLink;
    }

    public static List<String> glossaryHref (WebDriver driver){
        WebElement navTab = driver.findElement(By.xpath("//body/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[3]"));
        List<WebElement> glossaryItem = navTab.findElements(By.className("glossary-item"));
        List<String> navLink = new ArrayList<>();
        for(WebElement webElement : glossaryItem){
            List<WebElement> li = webElement.findElements(By.tagName("li"));
            for(WebElement liElement : li){
                navLink.add(liElement.findElement(By.tagName("a")).getAttribute("href"));
            }
        }

        System.out.println("Первая ссылка " + navLink.get(0));
        System.out.println("Кол-во ссылок на коктейли " + navLink.size());
        return navLink;
    }

    public static List<String> allUrlOfCocktails (WebDriver driver, List<String> navLinks){
        List<String> glossaryLinks = new ArrayList<>();
        for(String url : navLinks){
            driver.navigate().to(url);
            glossaryLinks.addAll(glossaryHref(driver)); // коллекция ссылок на коктейли
        }
        System.out.println("Кол-во ссылок на коктейли " + glossaryLinks.size());
        return glossaryLinks;
    }

    public static String webElementToString(List<WebElement> ingr){
        List<String> all_elements_ingr = new ArrayList<>();
        for (int i = 0; i < (long) ingr.size(); i++) {
            all_elements_ingr.add(ingr.get(i).getText());
        }
        String listString = String.join(", ", all_elements_ingr);
        return listString;
    }

    public static void main(String[] args) throws InterruptedException, SQLException, IOException {
        String PATH_TO_PROPERTIES = "src/main/resources/config.properties";
        FileInputStream fileInputStream;
        Properties prop = new Properties();
        fileInputStream = new FileInputStream(PATH_TO_PROPERTIES);
        prop.load(fileInputStream);
        String site = prop.getProperty("site");
        String url = prop.getProperty("url");
        String user = prop.getProperty("username");
        String pass = prop.getProperty("password");
        String chromeDriver = prop.getProperty("driver");
        WebDriver driver = launchBrowser(site,chromeDriver);
        Thread.sleep(1000);//задержка 1 сек
        List<String> navLinks = navHref(driver); // коллекция ссылок по главам справочника. по умолчанию браузер сразу на 1-ой
        //цикл на перебор navLinks и сохранения ссылок
        List<String> glossaryLinks = allUrlOfCocktails(driver,navLinks);
        //создание таблицы
        ToMySQL tableOfCocktails = new ToMySQL();
        tableOfCocktails.createTable(url,user,pass);

        String[] name = new String[glossaryLinks.size()];
        String[] ingr = new String[glossaryLinks.size()];
        String[] pick = new String[glossaryLinks.size()];
        String[] technique = new String[glossaryLinks.size()];
        String[] comps = new String[glossaryLinks.size()];
        for(int i=0;i<glossaryLinks.size();i++){
            driver.navigate().to(glossaryLinks.get(i));
            name[i] = driver.findElement(By.xpath("//body[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/h1[1]/span[1]")).getText();
            ingr[i]= webElementToString(driver.findElements(By.className("collection-item")));// ингридиенты
            pick[i] = driver.findElement(By.xpath("//body/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/p[2]")).getText();
            technique[i] = driver.findElement(By.cssSelector("div.page-wrapper:nth-child(2) div.main-wrapper div.main div.main-inner div.content div.container div.row div.col-md-8.col-lg-9 div.push-top-bottom div.listing-detail > div:nth-child(13)")).getText();
            comps[i]= driver.findElement(By.xpath("//head/meta[5]")).getAttribute("content");// ингридиенты в ед числе

            ToMySQL addRowInTable = new ToMySQL();
            addRowInTable.insertToTable(url,user,pass,name[i], ingr[i], pick[i], technique[i], comps[i].substring(comps[i].indexOf("Состав: ")));
            System.out.println("Добавлено " + i + " коктейля");
        }
        driver.quit();
    }
}
