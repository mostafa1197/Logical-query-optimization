package tableau;

import java.util.ArrayList;
import java.util.List;

public class ChaseAlgorithm {

	/**
	 * Führt die Verbundabhängigkeit auf dem Tableau wie folgt aus:
	 * 
	 * 1. Für jeden Teilverbund aus der List schneide des Tableau und verbinde es
	 * mit den restlichen Teilverbunden 2. Führe Schritt 1 solange bis die Liste das
	 * Universum entspricht
	 * 
	 * @param table Ein Tableau
	 * @param jd    Eine Verbund-Abhängigkeit
	 * @return Liefert die resultierende Tableau nach der Ausführung der
	 *         Verbund-Abhängigkeit auf dem Tableau
	 */
	public static Tableau jd(Tableau table, List<String> jd) {
		Tableau copy = table.copyTableau();
		for (int i = copy.getTableau().get(0).size() - 1; i > 0; i--) {

			copy.deleteRow(i);

		}

		Tableau subTable = table;
		subTable.deleteRow(0);
		List<String> joinDependencies = jd;
		for (int i = 0; i < joinDependencies.size(); i++) {
			Tableau resultTable = null;
			String rightside = "";

			for (int j = 0; j < joinDependencies.get(i).length(); j++) {
				String leftside = joinDependencies.get(i).substring(0, joinDependencies.get(i).indexOf(","));
				List<String> leftSide = stringToList(leftside);
				if (resultTable == null) {
					resultTable = cutTable(subTable, leftSide);
				}
				String jdRest = joinDependencies.get(i).substring(leftside.length() + 1);

				if (jdRest.contains(",")) {
					rightside = jdRest.substring(0, jdRest.indexOf(","));
				} else {
					rightside = jdRest.substring(0);
				}
				List<String> rightSide = stringToList(rightside);
				resultTable = joinTableaus(resultTable, cutTable(subTable, rightSide));
				
				if (!jdRest.contains(",")) {
					break;
				}
				String rest = joinDependencies.get(i).substring(leftside.length() + rightside.length() + 2);
				joinDependencies.set(i, leftside + rightside + "," + rest);
			}
			subTable = resultTable;
		}

		for (int i = 0; i < subTable.getTableau().get(0).size(); i++) {
			for (int j = 0; j < subTable.getTableau().size(); j++) {
				copy.getTableau().get(j).add(subTable.getTableau().get(j).get(i));
			}
		}
		copy.setTags(subTable.getTags());
		return copy;
	}

	private static List<String> stringToList(String input) {
		List<String> charList = new ArrayList<>();

		for (int i = 0; i < input.length(); i++) {
			charList.add(String.valueOf(input.charAt(i)));
		}
		return charList;
	}

	/**
	 * @param firstT  Das erste Tableau
	 * @param secondT Das zweite Tableau
	 * @return Liefert eine resultierende Tableau aus dem Verbund beide Tableaus mit
	 *         natural join
	 */
	public static Tableau joinTableaus(Tableau firstT, Tableau secondT) {
		List<String> attributes = new ArrayList<String>();
		attributes.addAll(firstT.getAttributes());
		for (String attribute : secondT.getAttributes()) {
			if (!firstT.getAttributes().contains(attribute)) {
				attributes.add(attribute);
			}
		}

		List<String> commonAttributes = new ArrayList<String>();
		for (String attribute : secondT.getAttributes()) {
			if (firstT.getAttributes().contains(attribute)) {
				commonAttributes.add(attribute);
			}
		}
		Tableau resultTable = new Tableau(attributes, new ArrayList<Relation>(firstT.getRelations()));
		for (int i = 0; i < firstT.getTableau().get(0).size(); i++) {
			for (int j = 0; j < secondT.getTableau().get(0).size(); j++) {
				boolean check = true;
				for (String att : commonAttributes) {
					if (!firstT.getTableau().get(firstT.getAttributes().indexOf(att)).get(i).getValue()
							.equals(secondT.getTableau().get(secondT.getAttributes().indexOf(att)).get(j).getValue())) {
						check = false;
					}
				}
				if (check) {
					resultTable = createRow(firstT, secondT, commonAttributes, i, j, resultTable);

				}
			}
		}
		
		return resultTable;
	}

	private static Tableau createRow(Tableau firstT, Tableau secondT, List<String> commonAttributes, int row1, int row2,
			Tableau table) {
		List<Variable> newRow = new ArrayList<Variable>();
		for (int i = 0; i < firstT.getTableau().size(); i++) {
			newRow.add(firstT.getTableau().get(i).get(row1));
		}
		for (int i = 0; i < secondT.getTableau().size(); i++) {
			if (!commonAttributes.contains(secondT.getAttributes().get(i))) {
				newRow.add(secondT.getTableau().get(i).get(row2));
			}
		}
		if (!exist(table, newRow)) {
			return insertRow(table, newRow);
		} else
			return table;
	}

	private static Tableau insertRow(Tableau table, List<Variable> newRow) {
		Tableau resultTable = table;
		for (int i = 0; i < table.getTableau().size(); i++) {
			resultTable.getTableau().get(i).add(newRow.get(i));
			
		}
		resultTable.getTags().add(table.getRelations().get(0).getName());
		return resultTable;
	}

	private static boolean exist(Tableau table, List<Variable> newRow) {
		if (table.getTableau().get(0).size() == 0) {
			return false;
		}
		for (int i = 0; i < table.getTableau().get(0).size(); i++) {
			if (checkRow(table, newRow, i)) {
				return true;
			}
		}
		return false;
	}

	private static boolean checkRow(Tableau table, List<Variable> newRow, int i) {
		for (int j = 0; j < table.getTableau().size(); j++) {
			if (!table.getTableau().get(j).get(i).getValue().equals(newRow.get(j).getValue())) {

				return false;
			}
		}
		return true;
	}

	/**
	 * Entfernt Spalten aller nicht in der Liste enthaltene Attribute, dann führt
	 * die Methode cleanTableau, um duplikate zu eliminierens
	 * 
	 * @param table      Ein Tableau
	 * @param attributes Liste der Attribute
	 * @return Entfernt alle nicht in der Liste enthaltene Attribute aus dem Tableau
	 *         und liefert dieses Tableau zurück
	 */
	public static Tableau cutTable(Tableau table, List<String> attributes) {
		List<String> orderedAtt = new ArrayList<String>();
		for (int i = 0; i < table.getTableau().size(); i++) {
			if (attributes.contains(table.getAttributes().get(i))) {
				orderedAtt.add(table.getAttributes().get(i));
			}
		}
		Tableau resultTable = new Tableau(orderedAtt, new ArrayList<Relation>(table.getRelations()));
		int counter = 0;
		for (int i = 0; i < table.getTableau().size(); i++) {
			if (attributes.contains(table.getAttributes().get(i))) {
				resultTable.getTableau().get(counter).addAll(table.getTableau().get(i));
				counter++;
			}

		}
		resultTable.getTags().addAll(table.getTags());
		resultTable.getConditions().addAll(table.getConditions());
		resultTable.getRelations().addAll(table.getRelations());

		resultTable = cleanTableau(resultTable);

		return resultTable;
	}

	private static Tableau cleanTableau(Tableau table) {
		Tableau cleanedTable = table.copyTableau();
		for (int i = 0; i < table.getTableau().get(0).size(); i++) {
			List<Variable> row = new ArrayList<Variable>();
			for (int j = 0; j < table.getTableau().size(); j++) {
				row.add(table.getTableau().get(j).get(i));
			}
			if (!exist(cleanedTable, row)) {
				insertRow(cleanedTable, row);
			}
		}

		return cleanedTable;
	}

	public static List<FD> toFD(List<String> fds) {
		List<FD> functionalDependencies = new ArrayList<FD>();
		for (String fd : fds) {
			functionalDependencies.add(new FD(fd));
		}
		return functionalDependencies;
	}

	/**
	 * Führt die Methode fd solange aus, bis sich das Tableau nicht mehr ändert
	 * 
	 * @param table Ein Tableau
	 * @param fd Liste der Funktionale-Abhängigkeiten
	 * @param ind Liste der Inklusionsabhängigkeiten
	 * @return Liefert ein Tableau mit Einhaltung der Liste aller Funktionale-Abhängigkeiten 
	 */
	public static Tableau enforceFD(Tableau table, List<FD> fd, List<IND> ind) {
		List<FD> fds = new ArrayList<FD>();
		fds.addAll(fd);
		Tableau oldTable = table;
		Tableau newTable = table;
		if(ind!= null) {
			newTable = fdIND(table, fd, ind);
		}
		newTable = fd(table, fd);
		int i = 0;
		while (!oldTable.equals(newTable)) {
			List<FD> dsss = new ArrayList<FD>();
			dsss.addAll(fds);
			oldTable = newTable;
			newTable = fd(oldTable, dsss);
			i++;
			if (i > 10) {
				break;
			}

		}
		return newTable;

	}

	private static Tableau fdIND(Tableau table, List<FD> fds, List<IND> inds) {
		Tableau tableau = fd(table, fds);
		if(containsComplex(inds)) {
			for(IND ind : inds) {
				if(ind.isComplex()) {
					for(FD fd : fds) {
						if(isRelated(ind, fd)) {
							List<String> leftSide  = ind.getLeftSide();  
							List<String> rightSide = ind.getRightSide();
							for(int i = 0; i<fd.getLeftSide().size();i++) {
								for(int j = 0; j<leftSide.size();j++) {
									fd.getLeftSide().set(i, fd.getLeftSide().get(i).replaceAll(leftSide.get(j), rightSide.get(j)));
									fd.getRightSide().set(i, fd.getRightSide().get(i).replaceAll(leftSide.get(j), rightSide.get(j)));	
								}
							}
						}
					}
				}
			}
		}
		for(FD fd: fds) {
			for(String att : fd.getLeftSide()) {
				System.out.print(att);	
			}
			System.out.print("->");
			for(String att : fd.getRightSide()) {
				System.out.print(att);	
			}
			System.out.println();
		}
		return fd(table, fds);
		
	}


	private static boolean isRelated(IND ind, FD fd) {
		for(String att : fd.getLeftSide()) {
			if(ind.getAttribute().contains(att)) {
				return true;
			}
		}
		for(String att : fd.getRightSide()) {
			if(ind.getAttribute().contains(att)) {
				return true;
			}
		}
		return false;
	}

	private static boolean columnsAreEmpty(Tableau table, FD fd) {
		boolean check = true;
		for(String att: fd.getLeftSide()) {
			int index = table.getAttributes().indexOf(att);
			 check = true;
			for(int i = 1; i<table.getTableau().get(0).size(); i++) {
				if(!table.getTableau().get(index).get(i).getValue().equals("blank")) {
					check = false;
					break;
				}
			}
		}
		for(String att: fd.getRightSide()) {
			int index = table.getAttributes().indexOf(att);
			 check = true;
			for(int i = 1; i<table.getTableau().get(0).size(); i++) {
				if(!table.getTableau().get(index).get(i).getValue().equals("blank")) {
					check = false;
					break;
				}
			}
		}
		return check;
	}

	private static boolean containsComplex(List<IND> inds) {
		for(IND ind : inds) {
			if(ind.isComplex()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Für jede Funktionale-Abhängigkeit in der Liste führe die folgende Schritte auf:
	 * 1. Suche nach einem Trigger im Tableau mit der Methode getTrigger
	 * 2. Falls es einen Trigger gibt nutze die Methode fixRow, um den Funktionale-Abhängigkeit im Tableau anzupassen
	 * 3. Wiederhole Schritte 1 und 2 bis keine Trigger mehr existiert
	 * 
	 * @param table Ein Tableau
	 * @param fds Liste der Funktionale-Abhängigkeiten
	 * @return Liefert die resultierende Tableau nach der Ausführung der
	 *         Funktionale-Abhängigkeiten der Liste auf dem Tableau 
	 */
	private static Tableau fd(Tableau table, List<FD> fds) {
		Tableau resultTable = table;
		for (FD fd : fds) {
			Tuple trigger = getTrigger(table, fd);
			while (trigger != null) {
				resultTable = fixRow(resultTable, fd, trigger);
				trigger = getTrigger(resultTable, fd);
			}

		}
		return resultTable;
	}

	private static Tableau fixRow(Tableau table, FD fd, Tuple trigger) {
		Tableau resultTable = table;
		int attIndex = 0;
		System.out.println(trigger.getLeft() + "|" + trigger.getRight());
		if(table.getTags().get(trigger.getLeft()-1).equals(table.getTags().get(trigger.getRight()-1))){
			for (String att : fd.getRightSide()) {
				attIndex = resultTable.getAttributes().indexOf(att);
				if (resultTable.getTableau().get(attIndex).get(trigger.getLeft()).isAusgezeichnet()) {
					if (!resultTable.getTableau().get(attIndex).get(trigger.getRight()).isAusgezeichnet()) {
						String var = table.getTableau().get(attIndex).get(trigger.getRight()).getValue();
						for (int i = 0; i < resultTable.getTableau().get(0).size(); i++) {
							if (table.getTableau().get(attIndex).get(i).getValue().equals(var)) {
								resultTable.getTableau().get(attIndex).get(i)
										.setValue(resultTable.getTableau().get(attIndex).get(trigger.getLeft()).getValue());
								resultTable.getTableau().get(attIndex).get(i).setAusgezeichnet(true);
	
							}
						}
						resultTable.getTableau().get(attIndex).get(trigger.getRight())
								.setValue(resultTable.getTableau().get(attIndex).get(trigger.getLeft()).getValue());
						resultTable.getTableau().get(attIndex).get(trigger.getRight()).setAusgezeichnet(true);
					}
				} else if (resultTable.getTableau().get(attIndex).get(trigger.getRight()).isAusgezeichnet()) {
					String var = table.getTableau().get(attIndex).get(trigger.getLeft()).getValue();
					for (int i = 0; i < resultTable.getTableau().get(0).size(); i++) {
						if (table.getTableau().get(attIndex).get(i).getValue().equals(var)) {
							resultTable.getTableau().get(attIndex).get(i)
									.setValue(resultTable.getTableau().get(attIndex).get(trigger.getRight()).getValue());
							resultTable.getTableau().get(attIndex).get(i).setAusgezeichnet(true);
	
						}
					}
					resultTable.getTableau().get(attIndex).get(trigger.getLeft())
							.setValue(resultTable.getTableau().get(attIndex).get(trigger.getRight()).getValue());
					resultTable.getTableau().get(attIndex).get(trigger.getLeft()).setAusgezeichnet(true);
				} else {
					String var = table.getTableau().get(attIndex).get(trigger.getRight()).getValue();
					for (int i = 0; i < resultTable.getTableau().get(0).size(); i++) {
						if (table.getTableau().get(attIndex).get(i).getValue().equals(var)) {
							resultTable.getTableau().get(attIndex).get(i)
									.setValue(resultTable.getTableau().get(attIndex).get(trigger.getLeft()).getValue());
							resultTable.getTableau().get(attIndex).get(i).setAusgezeichnet(false);
	
						}
					}
					resultTable.getTableau().get(attIndex).get(trigger.getRight())
							.setValue(resultTable.getTableau().get(attIndex).get(trigger.getLeft()).getValue());
					resultTable.getTableau().get(attIndex).get(trigger.getRight()).setAusgezeichnet(true);
				}
			}
		}
		return resultTable;
	}

	/**
	 * @param table Ein Tableau
	 * @param fd Eine Funktionale-Abhängigkeit
	 * @return liefert die Indexen der zwei Zeilen, die den FD-Regel verletzen
	 */
	private static Tuple getTrigger(Tableau table, FD fd) {
		for (int i = 1; i < table.getTableau().get(0).size(); i++) {
			for (int j = i + 1; j < table.getTableau().get(0).size(); j++) {
				boolean check = true;
				for (String att : fd.getLeftSide()) {

					if (!table.getTableau().get(table.getAttributes().indexOf(att)).get(i).getValue()
							.equals(table.getTableau().get(table.getAttributes().indexOf(att)).get(j).getValue())) {
						check = false;
					}
				}
				if (check) {
					check = false;
					for (String att : fd.getRightSide()) {
						if (!table.getTableau().get(table.getAttributes().indexOf(att)).get(i).getValue()
								.equals("blank")
								&& !table.getTableau().get(table.getAttributes().indexOf(att)).get(j).getValue()
										.equals("blank")) {

							if (!table.getTableau().get(table.getAttributes().indexOf(att)).get(i).getValue().equals(
									table.getTableau().get(table.getAttributes().indexOf(att)).get(j).getValue())) {
								check = true;
							}
						}
					}

				}
				if (check) {
					return new Tuple(i, j);
				}
			}
		}
		return null;
	}

	/**
	 * Führt die Methode jd solange aus, bis sich das Tableau nicht mehr ändert
	 * 
	 * @param table Ein Tableau
	 * @param jd Liste der Verbund-Abhängigkeiten
	 * @return Liefert ein Tableau mit Einhaltung der Liste aller Verbund-Abhängigkeiten 
	 */
	public static Tableau enforceJD(Tableau table, List<String> jd) {
		if (table.getRelations().size() > 1) {
			return table;
		} else {
			List<String> jds = new ArrayList<String>();
			jds.addAll(jd);
			Tableau oldTable = table;
			Tableau newTable = jd(table, jd);
			int i = 0;
			while (!oldTable.equals(newTable)) {
				List<String> dsss = new ArrayList<String>();
				dsss.addAll(jds);
				oldTable = newTable;
				newTable = jd(oldTable, dsss);
				i++;
				if (i > 10) {
					break;
				}
			}
			newTable.printTableau();
			return newTable;
		}
	}

}

