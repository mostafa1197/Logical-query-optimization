package tableau;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Sagiv {

	/**
	 * Minimiert das Tableau unter Einhaltung der Intgritätsbedingungen mit den
	 * folgenden Schritten: 1. Prüfe, ob das Tableau aus einer Relation besteht,
	 * falls true mach bei Schritt 2 weiter sonst rufe sagivIND auf 2. Starte bei
	 * Zeile 1 und vergleiche es mit jeder Zeile 3. Falls 1 die Andere Zeile
	 * subsumieren kann, rechne die Fellows dieser Zeile und prüfe, ob die
	 * subsumiert werden können 4. Falls ja, löche diese Zeile und dessen Fellows 5.
	 * Wiederhole bei 1, bis sich das Tableau nicht mehr ändert
	 * 
	 * @param table Ein Tableau
	 * @param inds  Liste der Inklusion-Abhängigkeiten
	 * @return Eine minimierte optimierte Tableau unter Einhaltung der
	 *         Intgritätsbedingungen
	 */
	public static Tableau sagiv(Tableau table, List<IND> inds) {
		Tableau resultTable = table.copyTableau();
		for (int i = 1; i < resultTable.getTableau().get(0).size(); i++) {
			for (int j = 1; j < resultTable.getTableau().get(0).size(); j++) {
				if (i != j) {
					if (resultTable.getTags().get(i - 1).equals(resultTable.getTags().get(j - 1))) {
						if (compareAusgezeichneteVars(resultTable, i, j)) {

							List<String> fellows = checkFellows(resultTable, i, j);
							if (fellows != null && fellows.size() > 0) {

								fellows = sortList(fellows);
								for (String fellow : fellows) {
									resultTable.deleteRow(Integer.valueOf(fellow));
								}
								resultTable.deleteRow(j);
								return sagiv(resultTable, inds);
							} else if (fellows != null) {
								resultTable.deleteRow(j);
								return sagiv(resultTable, inds);
							}
						}
					}
				}
			}
		}
		if (inds != null) {
			if (inds.size() > 0) {
				return sagivIND(table, inds);
			}
			return table;

		} else
			return resultTable;
	}

	/**
	 * Minimiert das Tableau unter Einhaltung der Intgritätsbedingungen mit den
	 * folgenden Schritten: 1. Rufe die Methode getRows auf, um alle Zeilen, die von
	 * der Inklusion-Abhängigkeiten betroffen sind zu bekommen. 2. Prüfe, ob Zeilen
	 * der Oberen-Klassen, Zeilen aus dem unter ihr liegende Klasse subsumieren kann
	 * unter Beachtung der Fellows 3. Falls ja, löche diese Zeile mit seine Fellows,
	 * dann rufe die Methode sagiv auf 4. Falls nicht liefere das Tableau zurück
	 * 
	 * 
	 * @param table Ein Tableau
	 * @param inds  Liste der Inklusion-Abhängigkeiten
	 * @return Eine minimierte optimierte Tableau unter Einhaltung der
	 *         Intgritätsbedingungen
	 */
	private static Tableau sagivIND(Tableau table, List<IND> inds) {
		Tableau resultTable = table.copyTableau();
		for (IND ind : inds) {
			List<ArrayList<String>> rows = getRows(resultTable, ind);
			for (int i = 0; i < rows.size() - 1; i++) {
				for (int j = 0; j < rows.get(i).size(); j++) {
					for (int k = i + 1; k < rows.size(); k++) {
						for (int s = 0; s < rows.get(k).size(); s++) {
							if (compareAusgezeichneteVars(resultTable, Integer.valueOf(rows.get(i).get(j)),
									Integer.valueOf(rows.get(k).get(s)))) {
								List<String> fellows = checkFellows(resultTable, Integer.valueOf(rows.get(i).get(j)),
										Integer.valueOf(rows.get(k).get(s)));
								if (fellows != null && fellows.size() > 0) {
									fellows = sortList(fellows);
									for (String fellow : fellows) {
										resultTable.deleteRow(Integer.valueOf(fellow));
									}
									resultTable.deleteRow(Integer.valueOf(rows.get(k).get(s)));
									return sagiv(resultTable, inds);
								} else if (fellows != null) {
									resultTable.deleteRow(Integer.valueOf(rows.get(k).get(s)));
									return sagiv(resultTable, inds);
								}
							}
						}
					}
				}
			}
		}
		return resultTable;
	}

	/**
	 * Führt die folgenden Schritten auf: 1. Suche die Zeilenummern, die zu der
	 * Relation der ersten Stelle der IND gehören 2. Markiere diese Zeilenummern in
	 * der ersten Stelle der resultierenden Liste 3. Wiederhole Schritt 1 und 2 für
	 * jede Stelle der IND
	 * 
	 * Die Stellen am Anfang der Liste heißen Obereklassen
	 * 
	 * 
	 * @param table Ein Tableau
	 * @param ind   Eine Inklusion-Abhängigkeit
	 * @return Liefert eine Liste der von der IND betroffene Zeilen unterteilt in
	 *         Oberklassen
	 */
	public static List<ArrayList<String>> getRows(Tableau table, IND ind) {
		List<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
		int index = 0;
		for (int i = 0; i < ind.getRelations().size(); i++) {
			rows.add(new ArrayList<String>());
		}
		for (String relation : ind.getRelations()) {
			for (int i = 0; i < table.getTags().size(); i++) {
				if (table.getTags().get(i).equals(relation)) {
					rows.get(index).add(String.valueOf(i + 1));
				}
			}
			index++;
		}
		return rows;
	}

	private static boolean checkRow(Tableau table, int j) {
		for (String condition : table.getConditions()) {

			if (condition.contains("w" + j)) {
				return false;
			} else if (condition.contains("or") && condition.indexOf("w") != condition.lastIndexOf("w")) {
				int x = Integer.valueOf(condition.substring(condition.indexOf("w") + 1, condition.indexOf(",")));
				int y = Integer.valueOf(condition.substring(condition.lastIndexOf("w") + 1, condition.indexOf(")")));
				if (j >= x && j <= y) {
					return false;
				}
			}

		}
		return true;
	}

	private static List<String> checkFellows(Tableau table, int i, int j) {
		Tableau resultTable = table.copyTableau();
		List<String> fellows = getFellows(resultTable, i, j, null);
		if (fellows != null && fellows.size() > 0) {
			if (fellows.contains(String.valueOf(i))) {
				fellows.remove(String.valueOf(i));
			}
			for (String fellow : fellows) {
				if (!compareAusgezeichneteVars(resultTable, i, Integer.valueOf(fellow))) {
					return null;
				}
			}
		}
		return fellows;
	}

	public static List<String> sortList(List<String> rows) {

		Comparator<String> descendingComparator = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				// Convert strings to integers for comparison
				int num1 = Integer.parseInt(o1);
				int num2 = Integer.parseInt(o2);

				// Compare in descending order
				return Integer.compare(num2, num1);
			}
		};

		// Sort the list using the custom comparator
		Collections.sort(rows, descendingComparator);

		return rows;
	}

	public static boolean compareAusgezeichneteVars(Tableau table, int row1, int row2) {
		for (int i = 0; i < table.getTableau().size(); i++) {
			if (!checkRow(table, row2)) {
				return false;
			}
			if (table.getTableau().get(i).get(row2).isAusgezeichnet()) {
				if (!table.getTableau().get(i).get(row1).isAusgezeichnet()) {
					return false;
				}
			}
		}
		return true;
	}

	public static List<String> getFellows(Tableau table, int referenceRow, int row, List<String> fellow) {
		List<String> fellows = new ArrayList<String>();
		Tableau copyT = table.copyTableau();
		if (fellow != null) {
			fellows = fellow;
		}
		for (int i = 0; i < copyT.getTableau().size(); i++) {
			if (!copyT.getTableau().get(i).get(row).isAusgezeichnet()) {
				for (int j = 1; j < copyT.getTableau().get(0).size(); j++) {
					if (j != row) {
						if (copyT.getTableau().get(i).get(j).getValue()
								.equals(copyT.getTableau().get(i).get(row).getValue())
								&& !copyT.getTableau().get(i).get(referenceRow).getValue()
										.equals(copyT.getTableau().get(i).get(j).getValue())
								&& !copyT.getTableau().get(i).get(j).getValue().equals("blank")) {
							if (!fellows.contains(Integer.toString(j))) {
								fellows.add(Integer.toString(j));
							}
						}
					}
				}
			}
		}
		if (!fellows.equals(fellow)) {
			List<String> copy = new ArrayList<String>(fellows);

			for (int i = 0; i < copyT.getTableau().size(); i++) {
				copyT.getTableau().get(i).set(row, new Variable("a" + i, true));
			}
			for (String r : copy) {
				fellows = getFellows(copyT, referenceRow, Integer.valueOf(r), fellows);
			}
		}

		return fellows;
	}

	public static void main(String[] args) {

		List<String> attributes = List.of("A", "B", "C", "D");
//		Tableau table = new Tableau(attributes);
//		table.getTableau().get(0).add(new Variable("a1", true));
//		table.getTableau().get(1).add(new Variable("b1", false));
//		table.getTableau().get(2).add(new Variable("b2", false));
//		table.getTableau().get(3).add(new Variable("a4", true));
//
//		table.getTableau().get(0).add(new Variable("a1", true));
//		table.getTableau().get(1).add(new Variable("a2", true));
//		table.getTableau().get(2).add(new Variable("b3", false));
//		table.getTableau().get(3).add(new Variable("b4", false));
//
//		table.getTableau().get(0).add(new Variable("b5", false));
//		table.getTableau().get(1).add(new Variable("a2", true));
//		table.getTableau().get(2).add(new Variable("b3", false));
//		table.getTableau().get(3).add(new Variable("a4", true));
//		table.getTags().add("R1");
//		table.getTags().add("R1");
//		table.getTags().add("R1");
//		table.getTags().add("R1");
//		table.getTags().add("R1");

		List<Relation> relations = new ArrayList<Relation>();
		Relation r1 = new Relation();
		List<String> attributes1 = new ArrayList<String>();
		List<String> universe = new ArrayList<String>();
		universe.add("A");
		universe.add("B");
		universe.add("C");
		universe.add("D");
		universe.add("E");
		universe.add("F");
		attributes1.add("A");
		attributes1.add("B");
		r1.setAttributes(attributes1);
		r1.setName("R1");
		Relation r2 = new Relation();
		List<String> attributes2 = new ArrayList<String>();
		attributes2.add("A");
		attributes2.add("C");
		r2.setAttributes(attributes2);
		r2.setName("R2");
		Relation r3 = new Relation();
		List<String> attributes3 = new ArrayList<String>();
		attributes3.add("A");
		attributes3.add("D");
		r3.setAttributes(attributes3);
		r3.setName("R3");
		Relation r4 = new Relation();
		List<String> attributes4 = new ArrayList<String>();
		attributes4.add("D");
		attributes4.add("E");
		r4.setAttributes(attributes4);
		r4.setName("R4");
		Relation r5 = new Relation();
		List<String> attributes5 = new ArrayList<String>();
		attributes5.add("D");
		attributes5.add("F");
		r5.setAttributes(attributes5);
		r5.setName("R5");

		relations.add(r1);
		relations.add(r2);
		relations.add(r3);
		relations.add(r4);
		relations.add(r5);

		r1.setUniverse(universe);
		r2.setUniverse(universe);
		r3.setUniverse(universe);
		r4.setUniverse(universe);
		r5.setUniverse(universe);

		Tableau table = new Tableau(attributes, relations);
		table.getTableau().get(0).add(new Variable("a1", true));
		table.getTableau().get(1).add(new Variable("a2", true));
		table.getTableau().get(2).add(new Variable("a3", true));
		table.getTableau().get(3).add(new Variable("blank", false));

		table.getTableau().get(0).add(new Variable("a1", true));
		table.getTableau().get(1).add(new Variable("b1", false));
		table.getTableau().get(2).add(new Variable("a3", true));
		table.getTableau().get(3).add(new Variable("b3", false));

		table.getTableau().get(0).add(new Variable("b4", false));
		table.getTableau().get(1).add(new Variable("b1", false));
		table.getTableau().get(2).add(new Variable("b5", false));
		table.getTableau().get(3).add(new Variable("b6", false));

		table.getTableau().get(0).add(new Variable("a1", true));
		table.getTableau().get(1).add(new Variable("b7", false));
		table.getTableau().get(2).add(new Variable("a3", true));
		table.getTableau().get(3).add(new Variable("b6", false));

		table.getTableau().get(0).add(new Variable("b8", false));
		table.getTableau().get(1).add(new Variable("a2", true));
		table.getTableau().get(2).add(new Variable("a3", true));
		table.getTableau().get(3).add(new Variable("b9", false));

		table.getTags().add("R1");
		table.getTags().add("R1");
		table.getTags().add("R1");
		table.getTags().add("R1");
		table.getTags().add("R1");

		Tableau r = table;
		List<String> x = sortList(getFellows(table, 1, 2, null));
		r.printTableau();

	}

}
