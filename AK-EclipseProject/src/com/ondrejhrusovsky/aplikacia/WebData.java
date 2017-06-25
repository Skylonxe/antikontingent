package com.ondrejhrusovsky.aplikacia;

import com.gargoylesoftware.htmlunit.BrowserVersion;

public class WebData {
	
	public static final BrowserVersion VerziaPrehliadaca = BrowserVersion.CHROME;
	public static final String LINK_VYHLADAVANIE = "https://ikvc.slovakrail.sk/mobile-sales-web/pages/connection/searchParam.xhtml";
	
	public static final String NAME_VYHLADAVANIE_FORM = "connectionParam";
	public static final String NAME_VYHLADAVANIE_TEXTINPUT_ODKIAL = NAME_VYHLADAVANIE_FORM + ":fromInput";
	public static final String NAME_VYHLADAVANIE_TEXTINPUT_KAM = NAME_VYHLADAVANIE_FORM + ":toInput";
	public static final String NAME_VYHLADAVANIE_TEXTINPUT_CEZ = NAME_VYHLADAVANIE_FORM + ":viaInput";
	public static final String NAME_VYHLADAVANIE_TEXTINPUT_DATUM = NAME_VYHLADAVANIE_FORM + ":date";
	public static final String NAME_VYHLADAVANIE_TEXTINPUT_CAS = NAME_VYHLADAVANIE_FORM + ":time";
	public static final String NAME_VYHLADAVANIE_SUBMITINPUT_VYHLADAT = NAME_VYHLADAVANIE_FORM + ":submit";
	
	public static final String ELEM_SPOJE_ZACIATOKKONIEC = "//div[@class='searched_connections']/div[@class='spoj']";
	public static final String ELEM_SPOJE_SPOJ = "//div[@class='searched_connections']/div[@class='i']";
	public static final String NAME_SPOJE_FORM_INFO_PREFIX = "j_idt89:";
	public static final String NAME_SPOJE_FORM_INFO_SUFFIX = ":j_idt131";
	public static final String NAME_SPOJE_SUBMITINPUT_INFO_SUFFIX = ":submit";
	public static final String NAME_SPOJE_FORM_NAKUP_PREFIX = "j_idt89:";
	public static final String NAME_SPOJE_FORM_NAKUP_SUFFIX = ":j_idt144";
	public static final String NAME_SPOJE_SUBMITINPUT_NAKUP_SUFFIX = ":submit";
	
	public static final String XPATH_SPOJE_NAZVYVLAKOV = "h2";
	public static final String XPATH_SPOJE_TRVANIE = "div[@class='info']/p/strong";
	
	public static final String XPATH_INFO_BLOKZOZNAMZASTAVOK = "//div[@class='block']";
	public static final String XPATH_INFO_ZOZNAMZASTAVOK = "ul";
	public static final String XPATH_INFO_NAZOVVZDIALENOST = "strong";
	public static final String XPATH_INFO_CAS1 = "p/em[1]";
	public static final String XPATH_INFO_CAS2 = "p/em[2]";
	public static final String ATTRIBUTE_INFO_NASTUPNAVYSTUPNA = "class";
	public static final String XPATH_INFO_POZNAMKA = "div[@class='note']";
	public static final String XPATH_SPOJE_PRESTUP = "p[@class='prestup']";
	public static final String XPATH_SPOJE_NASTUPNYCAS = "div[@class='time']/p[1]/strong";
	public static final String XPATH_SPOJE_NASTUPNYDATUM = "div[@class='time']/p[1]/span[2]";
	public static final String XPATH_SPOJE_VYSTUPNYCAS = "div[@class='time']/p[2]/strong";
	public static final String XPATH_SPOJE_VYSTUPNYDATUM = "div[@class='time']/p[2]/span[2]";
	
	
	public static final String NAME_NAKUP_SELECT_REZERVACIA = "j_idt92:j_idt94";
	public static final String NAME_NAKUP_OPTION_LISTOK = "1";
	public static final String NAME_NAKUP_SELECT_TYPCESTUJUCEHO = "ticketParam:j_idt112:0:j_idt115";
	public static final String NAME_NAKUP_OPTION_STUDENT = "2";
	
	public static final String NAME_NAKUP_FORM_NAKUP = "ticketParam";
	public static final String NAME_NAKUP_FORM_NAKUP_SUBMIT = "ticketParam:j_idt345";
	
	public static final String NAME_POKLADNA_FORM_ZRUSNAKUP = "cancelShoppingCartForm";
	public static final String NAME_POKLADNA_FORM_ZRUSNAKUP_SUBMIT = NAME_POKLADNA_FORM_ZRUSNAKUP + ":submit";
	
}
