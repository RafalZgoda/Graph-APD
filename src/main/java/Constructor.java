
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class Constructor {
	public static void main(String[] args) {

		Graph graph = new SingleGraph("Graph");
		// CSS du Graph
		graph.addAttribute("ui.stylesheet",
				"url('file:///"+System.getProperty("user.dir")+"/files/stylesheet')");
		graph.addAttribute("ui.quality");
		graph.addAttribute("ui.antialias");

		// Lecture du fichier DIMACS 
		try {
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/files/aim-100-1_6-no-1.cnf"));
			String line;
			/* Boucle sur chaque ligne qui regarde le premier caractère (si le premier char est c, ignore la ligne */
			while ((line = reader.readLine()) != null) {
				if (line.length() >= 1) {
					char first = line.charAt(0);
					if (first == 'c') {
						System.out.println("Commentaire");
						/* Parcourt les caractères suivants pour recupérés les deux noeuds*/
					} else if (first == 'e') {
						int i = 1;
						boolean token = true;
						String node1 = "", node2 = "";
						while (token) {
							if (line.charAt(i) == ' ' || line.charAt(i) == 'e') {
								token = true;
								i++;
							} else
								token = false;
						}
						token = true;
						while (token) {
							if (line.charAt(i) == ' ' || line.charAt(i) == 'e') {
								token = false;
							} else {
								node1 = node1 + line.charAt(i);
								i++;
								token = true;
							}
						}
						token = true;
						while (token) {
							if (line.charAt(i) == ' ' || line.charAt(i) == 'e' || line.charAt(i) == '\n') {
								token = true;
								i++;
							} else
								token = false;
						}
						token = true;
						while (token) {
							if (i < line.length()) {
								if (line.charAt(i) == ' ' || line.charAt(i) == 'e') {
									token = false;
								} else {
									node2 = node2 + line.charAt(i);
									i++;
									token = true;
								}
							} else
								token = false;
						}
						/* Fin du parcours de la ligne, création des deux sommets (si il n'existe pas) et de l'arrete */ 
						System.out.println(node1 + " " + node2);
						if (graph.getNode(node1) != null) {
							System.out.println("Déja existant");
						} else {
							graph.addNode(node1).addAttribute("ui.label", node1);

						}
						if (graph.getNode(node2) != null) {
							System.out.println("Déja existant");
						} else {
							graph.addNode(node2).addAttribute("ui.label", node2);
						}
						graph.addEdge(node1 + node2, node1, node2);
					}
				}

			}
		} catch (Exception e) {
			System.err.format("Erreur lecture fichier ");
			e.printStackTrace();
		}
		
		graph.display();

	}
}
