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
	int pricingRulesHashCode = 0;
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
        if(articlePricePackages.size()==0)
        	throw new IllegalStateException("Scanned Article Name:"+articleName+" with given package size does not exists in PriceRulesList");
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
		if(pricingRulesList==null)
			throw new IllegalStateException("PriceRulesList for Articles was not found");
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
		if(pricingRulesHashCode==0)
			pricingRulesHashCode = pricingRulesList.hashCode();
		else if(pricingRulesHashCode != pricingRulesList.hashCode())
			throw new IllegalStateException("PriceRulesList cannot be evolve two times.");
	}
	/**
	 * interne Repräsentation der Preisliste
	 */
	
}
