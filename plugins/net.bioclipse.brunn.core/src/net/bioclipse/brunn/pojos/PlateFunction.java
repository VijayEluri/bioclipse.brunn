/**
 * 
 */
package net.bioclipse.brunn.pojos;

import net.bioclipse.brunn.tests.daos.PlateDAOTest;

/**
 * @author jonathan
 *
 */
public class PlateFunction extends AbstractBaseObject {
	
	private String expression;
	private double goodFrom;
	private double goodTo;
	private boolean hasSpecifiedValues;
	private AbstractBasePlate plate;
	
	public PlateFunction() {
	    super();
    }

	public PlateFunction( User creator, 
	                      String name, 
	                      String expression, 
	                      double goodFrom, 
	                      double goodTo, 
	                      boolean hasSpecifiedValue, 
	                      AbstractBasePlate plate ) {
		
	    super(creator, name);
	    this.expression = expression;
	    this.goodFrom = goodFrom;
	    this.goodTo = goodTo;
	    this.hasSpecifiedValues = hasSpecifiedValue;
	    this.plate = plate;
    }

	/**
	 * @return  the expression
	 */
	public String getExpression() {
    	return expression;
    }

	/**
	 * @param expression  the expression to set
	 */
	public void setExpression(String expression) {
    	this.expression = expression;
    }

	/**
	 * @return  the goodFrom
	 */
	public double getGoodFrom() {
    	return goodFrom;
    }

	/**
	 * @param goodFrom  the goodFrom to set
	 */
	public void setGoodFrom(double goodFrom) {
    	this.goodFrom = goodFrom;
    }

	/**
	 * @return  the goodTo
	 */
	public double getGoodTo() {
    	return goodTo;
    }

	/**
	 * @param goodTo  the goodTo to set
	 */
	public void setGoodTo(double goodTo) {
    	this.goodTo = goodTo;
    }

	
	
	public boolean isHasSpecifiedValues() {
    	return hasSpecifiedValues;
    }

	/**
	 * @param hasSpecifiedValue  the hasSpecifiedValue to set
	 */
	public void setHasSpecifiedValues(boolean hasSpecifiedValues) {
    	this.hasSpecifiedValues = hasSpecifiedValues;
    }

	/**
	 * @return  the plate
	 */
	public AbstractBasePlate getPlate() {
    	return plate;
    }

	/**
	 * @param plate  the plate to set
	 */
	public void setPlate(AbstractBasePlate plate) {
    	this.plate = plate;
    }

	@Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (!super.equals(obj))
		    return false;
	    if ( !(obj instanceof PlateFunction) )
		    return false;
	    final PlateFunction other = (PlateFunction) obj;
	    if (expression == null) {
		    if (other.getExpression() != null)
			    return false;
	    }
	    else if (!expression.equals(other.getExpression()))
		    return false;
	    if (Double.doubleToLongBits(goodFrom) != Double.doubleToLongBits(other.getGoodFrom()))
		    return false;
	    if (Double.doubleToLongBits(goodTo) != Double.doubleToLongBits(other.getGoodTo()))
		    return false;
	    if (hasSpecifiedValues != other.isHasSpecifiedValues())
		    return false;
	    return true;
    }

	public PlateFunction deepCopy() {
	    
		PlateFunction plateFunction = new PlateFunction();
		plateFunction.setCreator(creator);
		plateFunction.setName(name);
		plateFunction.setExpression(expression);
		plateFunction.setGoodFrom(goodFrom);
		plateFunction.setGoodTo(goodTo);
		plateFunction.setHasSpecifiedValues(hasSpecifiedValues);
		
		plateFunction.setHashCode(hashCode);
		plateFunction.setId(id);
		
		return plateFunction;
    }
	
	public PlateFunction makeNewCopy() {
	    
		PlateFunction plateFunction = new PlateFunction();
		plateFunction.setCreator(creator);
		plateFunction.setName(name);
		plateFunction.setExpression(expression);
		plateFunction.setGoodFrom(goodFrom);
		plateFunction.setGoodTo(goodTo);
		plateFunction.setHasSpecifiedValues(hasSpecifiedValues);
		
		return plateFunction;
    }
}
