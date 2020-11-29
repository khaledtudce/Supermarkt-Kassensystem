package de.bvv.kata.de.bvv.kata.supermarket;

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CheckoutTest extends TestCase {
	
	PricingRulesInterface pricingRules;
	Checkout checkOut;
	/**
	 * TEST auf nicht initialisierte Preisregeln, die bei Scannen auffallen 
	 */
	public void testMissingPrices() {
		checkOut = new Checkout();
		try {
			checkOut.scan('A');
		}
		catch( IllegalStateException e ) { }			
		catch ( Exception e) {
			fail(String.format("IllegalStateException expected, but '%s' got", e.getClass().getName()));
		}			
	}

	/**
	 * TEST auf die Entwicklung der Preisbildung eines Warenkorbes
	 * im Ablauf eines Scan-Vorgangs. Hier repräsentiert sich die 
	 * Anwendung unterschiedlicher Preisregeln zu einem Artikel. 
	 */
	public void testIncrementalsOnCheckoutYieldsToCorrectDiscounts() {
		checkOut = new Checkout();
		checkOut.setPricingRules(pricingRules);
		assertEquals(0.0d, checkOut.getTotalPrice());
		checkOut.scan('A');
		assertEquals(50.0d, checkOut.getTotalPrice());
		checkOut.scan('B');
		assertEquals(80.0d, checkOut.getTotalPrice());
		checkOut.scan('A');
		assertEquals(120.0d, checkOut.getTotalPrice());
		checkOut.scan('A');
		assertEquals(160.0d, checkOut.getTotalPrice());
		checkOut.scan('B');
		assertEquals(175.0d, checkOut.getTotalPrice());		
	}
	/**
	 * TEST für den Scan eines in den Preisregeln unbekannten Artikels
	 */
	public void testUnknownArticle() {
        checkOut = new Checkout();
		checkOut.setPricingRules(pricingRules);
		try {
			checkOut.scan('X');
		}
		catch( IllegalArgumentException e ) { }			
		catch ( Exception e) {
			fail(String.format("IllegalArgumentException expected, but '%s' got", e.getClass().getName()));
		}		
	}
	/**
	 * TEST auf die Inkonsistenz der Preisregeln bezogen auf eine bestimmte Menge
	 * eines Artikels, der zwar in den Regeln vorkommt, aber nicht in der notwendigen Gebindegröße
	 */
	public void testOnePieceOfAnArticleWithoutSinglePriceYieldsToAnException() {
        checkOut = new Checkout();
		checkOut.setPricingRules(pricingRules);
		checkOut.scan('E');
		try {
			checkOut.getTotalPrice();
		}
		catch( IllegalStateException e ) { }			
		catch ( Exception e) {
			fail(String.format("IllegalStateException expected, but '%s' got", e.getClass().getName()));
		}
	}
	/** 
	 * TEST auf die Ausnahme, die bei Preisabfrage ohne Preisregeln geworfen wird.
	 * (Das ist eine technische Entscheidung, man hätte auch null zurückgeben können.)
	 */
	public void testTotalPriceCalculationWithoutPricingRulesYieldsToAnException() {
		checkOut = new Checkout();
		try {
			checkOut.getTotalPrice();
		}
		catch( IllegalStateException e ) { }			
		catch ( Exception e) {
			fail(String.format("IllegalStateException expected, but '%s' got", e.getClass().getName()));
		}		
	}
	/**
	 * TEST auf inkonsistente Preisregeln. Diese Situation tritt auf, wenn Waren 
	 * teurer werden, obwohl man mehr von ihnen kauft. 
	 */
	public void testCheckIncreasingPriceListYieldsToException() {
		PricingRulesInterface pRules = new PricingRules();
        //check will be positive		
		ArrayList<PricingRuleValueObject> ruleList = 
			new ArrayList<>(Arrays.asList(
				new PricingRuleValueObject('A', 1, 50.0d),
				new PricingRuleValueObject('A', 2, 90.0d),
				new PricingRuleValueObject('B', 1, 30.0d)
			));
		pRules.init(ruleList);
		pRules.checkConsistency();
        //now check will be negative
		ruleList.add(new PricingRuleValueObject('A', 10, 451.0d));
		pRules.init(ruleList);
		try {
			pRules.checkConsistency();
		} 
		catch (IllegalStateException e){
			if (!e.getMessage().contains("evolve")) {
				fail("IllegalArgumentException message does not contain the word 'evolve' but should do");
			}
		}
		catch (Exception e) {
			fail(String.format("IllegalArgumentException expected, but '%s' got", e.getClass().getName()));			
		}
	}
	/**
	 * Hilfsmethode, die den Preis eines Warenkorbes berechnet, der durch einen String angegeben wird.
	 * Das ist möglich, weil die Artikelnamen Characters sind.
	 * 
	 * @param basketLine String, der die Folge der Artikel repräsentiert
	 * @return Gesamtpreis
	 * @throws IllegalStateException wenn die Preisregeln nicht zum Warenkorb passen
	 */
	
	/**
	 * TEST auf die korrekten Preise verschiedener Warenkörbe
	 */
	
	public void testManyCasesOfBasketsYieldToTheCorrectTotals() {
    	
		assertEquals( 0.0d, getPriceOfNewBasket( "" ));
		assertEquals( 50.0d, getPriceOfNewBasket( "A" ));
		assertEquals( 80.0d, getPriceOfNewBasket( "AB" ));
		assertEquals( 115.0d, getPriceOfNewBasket( "CDBA" ));
		assertEquals( 90.0d, getPriceOfNewBasket( "AA" ));
		assertEquals( 130.0d, getPriceOfNewBasket( "AAA" ));
		assertEquals( 180.0d, getPriceOfNewBasket( "AAAA" ));
		assertEquals( 220.0d, getPriceOfNewBasket( "AAAAA" ));
		assertEquals( 260.0d, getPriceOfNewBasket( "AAAAAA" ));
		assertEquals( 160.0d, getPriceOfNewBasket( "AAAB" ));
		assertEquals( 175.0d, getPriceOfNewBasket( "AAABB" ));
		assertEquals( 190.0d, getPriceOfNewBasket( "AAABBD" ));
		assertEquals( 190.0d, getPriceOfNewBasket( "DABABA" ));
		assertEquals( 40.0d, getPriceOfNewBasket( "FFFF" ));
		assertEquals( 50.0d, getPriceOfNewBasket( "FFFFFFFF" ));
		assertEquals( 70.0d, getPriceOfNewBasket( "FFFFFFFFFF" ));
		assertEquals( 85.0d, getPriceOfNewBasket( "FFFFFDFFFFF" ));
		assertEquals( 45.0d, getPriceOfNewBasket( "EEEDE" ));	
	}
	
	private double getPriceOfNewBasket(String basketLine) throws IllegalStateException {
		checkOut = new Checkout();
		checkOut.setPricingRules(pricingRules);
		basketLine.chars().forEachOrdered(c -> checkOut.scan((char)c));
		return checkOut.getTotalPrice();
	}
    public static Test suite() {
        return new TestSuite( CheckoutTest.class );
    }
    /**
     * Initialisierung der Beispiel-Preisregeln
     */
    @Override
    public void setUp() throws Exception {
    	pricingRules = new PricingRules();
    	pricingRules.init(
			new ArrayList<PricingRuleValueObject>(Arrays.asList(
				new PricingRuleValueObject('A', 1, 50.0d),
				new PricingRuleValueObject('A', 2, 90.0d),
				new PricingRuleValueObject('A', 3, 130.0d),
				new PricingRuleValueObject('B', 1, 30.0d),
				new PricingRuleValueObject('B', 2, 45.0d),
				new PricingRuleValueObject('C', 1, 20.0d),
				new PricingRuleValueObject('D', 1, 15.0d),
				new PricingRuleValueObject('E', 2, 15.0d),
				new PricingRuleValueObject('F', 1, 10.0d),
				new PricingRuleValueObject('F', 2, 20.0d),
				new PricingRuleValueObject('F', 6, 30.0d)
			)));
    }
}
