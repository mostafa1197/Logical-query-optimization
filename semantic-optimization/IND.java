package tableau;

import java.util.ArrayList;
import java.util.List;

import javax.print.AttributeException;

public class IND {
	private List<String> relations;
	private String attribute;
	private boolean complex;

	public IND(List<String> relations, String attribute, boolean complex) {
		this.relations = relations;
		this.attribute = attribute;
		this.complex = complex;
	}

	public List<String> getRelations() {
		return relations;
	}

	public void setRelations(List<String> relations) {
		this.relations = relations;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public boolean isComplex() {
		return complex;
	}

	public void setComplex(boolean complex) {
		this.complex = complex;
	}

	public List<String> getLeftSide() {
		attribute = attribute.replaceAll(" ", "");
		List<String> leftSideAttributes = new ArrayList<String>();
		String leftSide = attribute.substring(attribute.indexOf("(")+1, attribute.indexOf("),"));
		while(leftSide.contains(",")) {
			leftSideAttributes.add(leftSide.substring(0,leftSide.indexOf(",")));
			leftSide = leftSide.substring(leftSide.indexOf(",")+1);
		}
		leftSideAttributes.add(leftSide);
		return leftSideAttributes;
	}

	public List<String> getRightSide() {
		attribute = attribute.replaceAll(" ", "");
		List<String> rightSideAttributes = new ArrayList<String>();
		String rightSide = attribute.substring(attribute.indexOf(",(")+2, attribute.lastIndexOf(")"));
		while(rightSide.contains(",")) {
			rightSideAttributes.add(rightSide.substring(0,rightSide.indexOf(",")));
			rightSide = rightSide.substring(rightSide.indexOf(",")+1);
		}
		rightSideAttributes.add(rightSide);
		return rightSideAttributes;
	}

}
