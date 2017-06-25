package com.ondrejhrusovsky.test;
import java.io.IOException;
import java.net.MalformedURLException;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;

public class Test {

    public static void main2(final String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException
    {
    	java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
    		
    	try (final WebClient webClient = new WebClient(BrowserVersion.CHROME))
    	{
    		webClient.getOptions().setJavaScriptEnabled(false);
            HtmlPage page = webClient.getPage("https://ikvc.slovakrail.sk/mobile-sales-web/pages/connection/searchParam.xhtml");
            HtmlForm form = page.getFormByName("connectionParam");       
            HtmlSubmitInput button = form.getInputByName("connectionParam:submit");
            HtmlTextInput textField = form.getInputByName("connectionParam:fromInput");
            HtmlTextInput textField2 = form.getInputByName("connectionParam:toInput");
            textField.setValueAttribute("Bratislava hl.st.");
            textField2.setValueAttribute("Košice");
     
            page = button.click();
            form = page.getFormByName("connectionParam");       
            button = form.getInputByName("connectionParam:submit");
            page = button.click();
            webClient.getOptions().setJavaScriptEnabled(true);
            form = page.getFormByName("j_idt89:0:j_idt144");
            button = form.getInputByName("j_idt89:0:j_idt144:submit");
            page = button.click();
                        
            form = page.getFormByName("ticketParam");
                
            HtmlSelect select = (HtmlSelect) page.getElementByName("ticketParam:j_idt112:0:j_idt114");
            HtmlOption option = select.getOptionByValue("2");
            select.setSelectedAttribute(option, true);
            webClient.waitForBackgroundJavaScript(1000);
            button = form.getInputByName("ticketParam:j_idt333");
      
            page = button.click();

            System.out.println(page.asText());
  
        }
    }
}