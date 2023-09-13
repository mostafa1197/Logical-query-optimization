package tableau;

import java.util.ArrayList;
import java.util.List;

public class FD {
	private List<String> leftSide;
	private List<String> rightSide;

	public List<String> getLeftSide() {
		return leftSide;
	}

	public void setLeftSide(List<String> leftSide) {
		this.leftSide = leftSide;
	}

	public List<String> getRightSide() {
		return rightSide;
	}

	public void setRightSide(List<String> rightSide) {
		this.rightSide = rightSide;
	}

	public FD(String fd) {
		leftSide = new ArrayList<String>();
		rightSide = new ArrayList<String>();
		String leftFD = fd.substring(0, fd.indexOf("-"));
		String rightFD = fd.substring(leftFD.length() + 2);

		while (leftFD.contains(",")) {
			leftSide.add(leftFD.substring(0, leftFD.indexOf(",")));
			leftFD = leftFD.substring(leftFD.indexOf(",") + 1);

		}

		while (rightFD.contains(",")) {

			rightSide.add(rightFD.substring(0, rightFD.indexOf(",")));
			rightFD = rightFD.substring(rightFD.indexOf(",") + 1);

		}
		leftSide.add(leftFD);
		rightSide.add(rightFD);
	}

}
