package apd.graph.utilitaires;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.Viewer;

public class GraphAPD {

	public static Graph graph=new SingleGraph("Graph");;

	public GraphAPD(String nomFichier) {

		// CSS du Graph
		graph.addAttribute("ui.quality");
		graph.addAttribute("ui.antialias");
		graph.addAttribute("ui.stylesheet", "url('file:///" + System.getProperty("user.dir") + "/files/stylesheet')");
		// graph.addAttribute("ui.stylesheet", "edge { fill-color: grey; }" );

		initGraph(nomFichier);

	}

	/***
	 * 
	 * @param nomFichier
	 * @return true or false selon si le programme r\u00e9ussi \u00e0
	 *         initialiser le graph ou non.
	 */
	public boolean initGraph(String nomFichier) {
		try {

			BufferedReader in = new BufferedReader(new FileReader("files/" + nomFichier));

			int vertices = -1;
			int edges = -1;
			final Pattern p = Pattern.compile("p\\s+edge\\s+(\\d+)\\s+(\\d+)\\s*");
			final Matcher mp = p.matcher("");
			for (String line = in.readLine(); line != null; line = in.readLine()) {
				mp.reset(line);
				if (mp.matches()) {
					vertices = Integer.parseInt(mp.group(1));
					edges = Integer.parseInt(mp.group(2));
					break;
				}
			}

			if (vertices <= 0)
				throw new Exception("nombre de noeuds inf\u00e9rieur \u00e0 0 ");
			if (edges <= 0)
				throw new Exception("nombre d'arr\u00e8te inf\u00e9rieur \u00e0 0 ");

			// int graphTab[][]= new int[vertices+1][edges+1];
			String tmp, tmp2;

			for (int i = 0; i < vertices; i++) {
				tmp = Integer.toString(i + 1);
				graph.addNode(tmp).addAttribute("ui.label", tmp);
			}

			final Pattern e = Pattern.compile("e\\s+(\\d+)\\s+(\\d+)\\s*");
			final Matcher me = e.matcher("");
			int from, to;
			Node node;

			ArrayList<Integer> voisins;
			for (String line = in.readLine(); line != null; line = in.readLine()) {
				me.reset(line);
				if (me.matches()) {
					from = Integer.parseInt(me.group(1));
					to = Integer.parseInt(me.group(2));
					//System.out.println("from : "+from+" to: "+to);
					if (from == 0 || from > vertices)
						throw new Exception("Mauvaise arrete: " + from + " in " + line);
					if (to == 0 || to > vertices)
						throw new Exception("Mauvase arrete: " + to + " in " + line);

					/*node = graph.getNode(from-1);
					if (node.hasAttribute("voisins")) {
						voisins = (ArrayList<Integer>) node.getAttribute("voisins", ArrayList.class);
						voisins.add(to-1);
						node.setAttribute("voisins", voisins);
					} else {
						voisins=new ArrayList<Integer>();
						voisins.add(to-1);
						node.addAttribute("voisins", voisins);
					}*/
					graph.addEdge(from + "-" + to, from-1, to-1);
				}
			}
			return true;
		} catch (Exception e) {
			System.err.format("Erreur lecture fichier ");
			e.printStackTrace();
			return false;
		}
	}

	public Viewer getViewer() {
		Viewer viewer = graph.display();
		return viewer;
	}

	public void animation(String arrete) throws InterruptedException {
		SpriteManager sman = new SpriteManager(graph);
		Sprite s = sman.addSprite(arrete);
		s.addAttribute("shape", "box");

		s.attachToEdge(arrete);
		graph.getEdge(arrete).addAttribute("ui-color", Color.RED);
		for (double i = 0.1; i < 1.0; i += 0.1) {
			s.setPosition(i);
			sleep();
		}
	}

	protected void sleep() {
		try {
			Thread.sleep(50);
		} catch (Exception e) {
		}
	}

	public Graph getGraph() {
		return graph;
	}

	public static void main(String[] args) throws InterruptedException {

		try
		{
	
				//GraphAPD g = new GraphAPD("aim-100-1_6-no-1.cnf");
			GraphAPD g = new GraphAPD("anneau.cnf");
				Algorithm a = new ChangRobertsAlgorithm(g);
				a.start(args);
				//g.getGraph().display();
		
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("errror");
		}
			

	}

}
