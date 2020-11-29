package de.bvv.kata.de.bvv.kata.supermarket;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Die Klasse implementiert Funktionalitäten auf Basis des Interfaces
 * {@link PricingRulesInterface}. Preisregeln für Artikel gibt es mit 
 * unterschiedlicher Rabattierung für verschiedene Gebindegrößen. Die 
 * Klasse überprüft diese Preisregeln und kann sie für bestimmte Artikel
 * in der Weise anwenden, dass der geringstmögliche Preis entsteht.
 * 
 * @author bvv/b00359 (Tobias Zepter)
 */
public class PricingRules implements PricingRulesInterface {
	
	List<PricingRuleValueObject> pricingRulesList;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(List<PricingRuleValueObject> pricingRulesList) {
		this.pricingRulesList = pricingRulesList;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPriceAvailableFor(char articleName) {
		//...
		return pricingRulesList.stream().anyMatch(x->x.getArticleName()==articleName);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public InterimResult getPriceForNEqualItems(char articleName, int articleCount) {
		double packagePrice = 0.0d;
		if(articleName==' ')
			return new InterimResult(packagePrice, 0);
		
		List<PricingRuleValueObject> articlePricePackages = getPackagesFor(articleName);
        for (PricingRuleValueObject pricingRuleValueObj : articlePricePackages) {
        	int currentPackageSize = pricingRuleValueObj.getPackageSize();
			if(articleCount>=currentPackageSize) {
        		while(articleCount>=currentPackageSize) {
        			packagePrice += pricingRuleValueObj.getPackagePrice();
        			articleCount = articleCount - currentPackageSize;
        		}
        	}
		}

		return new InterimResult(packagePrice, articleCount);
	}
	private List<PricingRuleValueObject> getPackagesFor(char articleName) {
		return pricingRulesList.stream()
				.filter(x->x.getArticleName()==articleName)
				.sorted(Comparator.comparingInt(PricingRuleValueObject::getPackageSize).reversed())
				.collect(Collectors.toList());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void checkConsistency() throws IllegalStateException {
		for (PricingRuleValueObject pricingRuleValueObj : pricingRulesList) {
			char articleName = pricingRuleValueObj.getArticleName();
			int packageSize = pricingRuleValueObj.getPackageSize();
			double packagePrice = pricingRuleValueObj.getPackagePrice();
			if(articleName=='\0' || articleName==' ')
				throw new IllegalStateException("Illegal Article Name");
			if(packageSize<=0 || packageSize>Integer.MAX_VALUE)
				throw new IllegalStateException("Illegal Article package size");
			if(packagePrice<0 || packagePrice>Double.MAX_VALUE)
				throw new IllegalStateException("Illegal Article package price");
		}
	}
	/**
	 * interne Repräsentation der Preisliste
	 */
	
}
