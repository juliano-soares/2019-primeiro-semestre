/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package junit;

import static org.junit.Assert.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Keys;


/**
 *
 * @author juliano
 */
public class T2 {
    private static WebDriver driver;
 
    @BeforeClass
    public static void openBrowser() {      
        System.out.println("aqui!");
        // apontar "geckodriver" para onde estiver o arquivo "geckodriver"
        System.setProperty("webdriver.gecko.driver","geckodriver");
        // estamos usando o Firefox
        driver = new FirefoxDriver();
        driver.manage().window().maximize();
        driver.get("https://www.ufsm.br/busca?q=&orderby=date&order=DESC&perpage=15&area=servicos");
    }
    
    @Test
    public static void testaSelect1() {
        System.out.println("Testando Select1");
 
        // Selecionando a combobox diretamente pelo id
        Select combo = new Select(driver.findElement(By.id("orderby")));
        
        // Criando uma lista com as opções da combobox
        List<WebElement> opcoes = combo.getOptions();
        
        List resultadoRetornado = new LinkedList();		
        opcoes.forEach((conteudo) -> {
            resultadoRetornado.add(conteudo.getText());
        });

        List resultadoEsperado = new LinkedList();

        // Mudando algum desses elementos, não passa no teste
        resultadoEsperado.add("Data de criação");
        resultadoEsperado.add("Data de modificação");
        resultadoEsperado.add("Título");

        assertEquals(resultadoEsperado, resultadoRetornado);
    }
    
    @Test
    public static void testaSelect2() {
        System.out.println("Testando Select2");
       
        Select combo = new Select(driver.findElement(By.id("ordering")));
        
        List<WebElement> opcoes = combo.getOptions();
        
        // Combobox aceita só uma opção?
        assertTrue(!combo.isMultiple());
        
        // Combobox tem apenas duas opções?		 
        assertEquals(2, opcoes.size());
        
        // Imprimindo os textos das opções
        opcoes.forEach((WebElement conteudo) -> {
            System.out.println(conteudo.getText());
        });     
    }
    
    @Test
    public static void testaInput1() {
        System.out.println("Testando Input1");
        // Aqui, vamos tentar selecionar o elemento de outro modo:
        // usando xpath.
        // O xpath é um método fácil pois, na maioria dos casos, basta 
        // copiá-lo colá-lo no código diretamente, sem modificações.
        // É usado especialmente em casos onde não temos parâmetros de 
        // identificação. Ou seja, neste!
        WebElement elem = driver.findElement(By.xpath("//*[@id=\'arch-main-section\']/div[1]/input"));
        
        // O campo de texto é vazio por default?
        assertEquals("", elem.getText());
        
        // Enviando um pequeno texto
        elem.sendKeys("teste ordinário");
        
        // Limpando o texto.
        elem.clear();
    }
    
    @Test
    public static void testaInput2() {
        try {
            System.out.println("Testando Input2");
            WebElement elem = driver.findElement(By.xpath("//*[@id=\'portal-searchbox-field\']"));
            Thread.sleep(2000);
            elem.sendKeys("teste");
            // O valor da busca é "teste"?
            assertEquals("teste", elem.getAttribute("value"));
            // Submetendo a busca com o valor "teste".
            elem.sendKeys(Keys.RETURN);
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @AfterClass
    public static void closeBrowser(){
        driver.quit();
    }
    
    public static void main(String args[]) {
        openBrowser();
        testaSelect1();
        testaSelect2();
        testaInput1();
        testaInput2();
        closeBrowser();
    }
}