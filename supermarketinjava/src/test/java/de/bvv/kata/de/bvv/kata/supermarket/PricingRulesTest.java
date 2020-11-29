package de.bvv.kata.de.bvv.kata.supermarket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class PricingRulesTest{
	
	PricingRulesInterface pricingRules = new PricingRules();
	public PricingRulesTest() {
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
					new PricingRuleValueObject('F', 3, 20.0d),
					new PricingRuleValueObject('F', 6, 30.0d)
				)));
	}
	
	@Test
	public void WhenDataProvided_ShouldBeAbleToTellIfAvaialable() {	
		assertEquals(true, pricingRules.isPriceAvailableFor('A'));	
		assertEquals(false, pricingRules.isPriceAvailableFor('R'));	
	}
	
	@Test
	public void ShouldBeAbleToReturnPriceForOneArticle() {
		InterimResult priceForNEqualItems = pricingRules.getPriceForNEqualItems('A', 1);
		assertEquals(priceForNEqualItems.getRemainingCount(), 0);
		assertEquals(0, priceForNEqualItems.getCalculatedPrice(), 50.0d);
	}
	
	@Test
	public void ShouldBeAbleToReturnPriceForPackagedArticle() {
		InterimResult priceForNEqualItems = pricingRules.getPriceForNEqualItems('A', 3);
		assertEquals(priceForNEqualItems.getRemainingCount(), 0);
		assertEquals(0, priceForNEqualItems.getCalculatedPrice(), 130.0d);
	}

	@Test
	public void ShouldBeAbleToReturnPriceForDoublePackagedArticle() {
		InterimResult priceForNEqualItems = pricingRules.getPriceForNEqualItems('A', 6);
		assertEquals(priceForNEqualItems.getRemainingCount(), 0);
		assertEquals(0, priceForNEqualItems.getCalculatedPrice(), 260.0d);
	}
	
	@Test
	public void ShouldBeAbleToReturnPriceForPackagedArticleAndRemaining() {
		boolean IllegalStateExceptionThrowed = false;
		try {
			pricingRules.getPriceForNEqualItems('E', 3);
		}
		catch( IllegalStateException e ) { IllegalStateExceptionThrowed = true; }
		assertTrue(IllegalStateExceptionThrowed);
	}
}
