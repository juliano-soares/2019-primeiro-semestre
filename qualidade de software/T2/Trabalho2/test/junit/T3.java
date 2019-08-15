/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package junit;

import static org.junit.Assert.*;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;


/**
 *
 * @author juliano
 */

public class T3 {
    private static WebDriver driver;
 
    @BeforeClass
    public static void openBrowser() {      
        System.out.println("aqui junity!");
        // apontar "geckodriver" para onde estiver o arquivo "geckodriver"
        System.setProperty("webdriver.gecko.driver","C:\\Users\\Juliano\\Downloads\\lib selenium\\geckodriver.exe");
        // estamos usando o Firefox
        driver = new FirefoxDriver();
        driver.manage().window().maximize();
        driver.get("https://www.ufsm.br/busca?q=&orderby=date&order=DESC&perpage=15&area=servicos");
    }
    
    
    // verifica e existencia das imagens no site da UFSM
    @Test
    public static void CheckImage() throws Exception {
        System.out.println("Verificando as duas imagens!");
	WebElement ImageFile1 = driver.findElement(By.className("img"));
        WebElement ImageFile2 = driver.findElement(By.cssSelector("img[alt=\"Acesso à informação\"]"));
        
        Boolean img1 = (Boolean) ((JavascriptExecutor)driver).executeScript("return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0", ImageFile1);
        Boolean img2 = (Boolean) ((JavascriptExecutor)driver).executeScript("return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0", ImageFile2);
        
        assertEquals(true,img1);
        if (!img1){
             System.out.println("Imagem 1 não foi exibida.");
        }
        else{
            System.out.println("Imagem 1 foi exibida.");
        }
        
        assertEquals(true,img2);
        if(!img2){
            System.out.println("Imagem 2 não foi exibida.");
        }
        else{
            System.out.println("Imagem 2 foi exibida.");
        }
    }
    
    @Test
    public static void testaImagem1() {
        System.out.println("Testando Imagem1");
        
        // Selecionando a imagem diretamente pelo title		
        WebElement logoUFSM = driver.findElement(By.className("img"));					

        /* testando seu conteudos (src, alt, size(width,height), tagname, localizacao, rect)*/
        String src = logoUFSM.getAttribute("src");
        //System.out.println(src);
        //"https://www.ufsm.br/wp-content/themes/ufsm/images/brasoa-branco-ufsm-150dpi.png"
        assertEquals("https://www.ufsm.br/wp-content/themes/ufsm/images/brasoa-branco-ufsm-150dpi.png",src);
        
        String alt = logoUFSM.getAttribute("alt");
        //System.out.println(alt);
        //"Imagem do brasão composta por quatro elementos: escudo, flor de lis, archotes e lema. "
        assertEquals("Imagem do brasão composta por quatro elementos: escudo, flor de lis, archotes e lema. ",alt);
        
        Dimension dim = logoUFSM.getSize();
        //System.out.println(dim);
        assertEquals("(120, 97)",dim.toString());	
        
        String TagName = logoUFSM.getTagName();
        //System.out.println(TagName);
        assertEquals("img",TagName);
        
        Point Location = logoUFSM.getLocation();
        //System.out.println("eixo X(169) = " + Location.getX());
        //System.out.println("eixo Y(89) = " + Location.getY());
        assertEquals(169,Location.getX());
        assertEquals(89,Location.getY());
        
        // testando o redirecionamento da imagem
        logoUFSM.click();
	if (driver.getCurrentUrl().equals("https://www.ufsm.br/")) {	
            System.out.println("CORRETO foi redirecionado a pagina inicial da ufsm");					
        } else {			
            System.out.println("ERRADO nao foi redirecionado a pagina inicial da ufsm");					
        }
        assertEquals("https://www.ufsm.br/",driver.getCurrentUrl());
        driver.switchTo().defaultContent();
    }
    
    @Test
    public static void testaImagem2() {
        WebElement acessoInf = driver.findElement(By.cssSelector("img[alt=\"Acesso à informação\"]"));
        
        // pegar algum valor css neste exemplo pego o alinhamento da imagem
        System.out.println("valor CSS de alinhamento que deve ser retornado: LEFT = " + acessoInf.getCssValue("text-align"));
        assertEquals("right",acessoInf.getCssValue("text-align"));
        
        // pegar algum atributo neste exemplo pego o alt
        System.out.println("ALT(Acesso à informação) = " + acessoInf.getAttribute("alt"));
        assertEquals("Acesso à informação",acessoInf.getAttribute("alt"));
        
        // [egar o tamanho da imagem
        System.out.println("size(92, 40) = " + acessoInf.getSize());
        assertEquals("(92, 40)",acessoInf.getSize().toString());
        
        // verificar se esta ativada
        System.out.println("TRUE = " + acessoInf.isDisplayed());
        assertEquals(false,acessoInf.isDisplayed());
        
        // testando o redirecionamento da imagem
        acessoInf.click();
	if (driver.getCurrentUrl().equals("https://www.ufsm.br/reitoria/acesso-a-informacao/")) {	
            System.out.println("CORRETO foi redirecionado a pagina de Acesso à Informação");					
        } else {			
            System.out.println("ERRADO nao foi redirecionado a de Acesso à Informação");					
        }
        assertEquals("https://www.ufsm.br/reitoria/acesso-a-informacao/",driver.getCurrentUrl());
        driver.switchTo().defaultContent();
    }
    
    @AfterClass
    public static void closeBrowser(){
        driver.quit();
    }
    
    public static void main(String args[]) throws Exception {
        openBrowser();
        //CheckImage();
        //testaImagem1();
        testaImagem2();
        closeBrowser();
    }
}