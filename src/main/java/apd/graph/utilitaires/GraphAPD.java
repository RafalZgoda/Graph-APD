package apd.graph.utilitaires;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.Viewer;

import mpi.MPI;

public class GraphAPD {

	private Graph graph;

	public GraphAPD(String nomFichier) {

		// CSS du Graph
		graph=new SingleGraph("Graph");;
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
	public void initGraph(String nomFichier) {
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

			String tmp;

			for (int i = 0; i < vertices; i++) {
				tmp = Integer.toString(i);
				graph.addNode(tmp);
			}

			final Pattern e = Pattern.compile("e\\s+(\\d+)\\s+(\\d+)\\s*");
			final Matcher me = e.matcher("");
			int from, to;
			Node node;

			for (String line = in.readLine(); line != null; line = in.readLine()) {
				me.reset(line);
				if (me.matches()) {
					from = Integer.parseInt(me.group(1));
					to = Integer.parseInt(me.group(2));
					
					if (from == 0 || from > vertices)
						throw new Exception("Mauvaise arrete: " + from + " in " + line);
					if (to == 0 || to > vertices)
						throw new Exception("Mauvase arrete: " + to + " in " + line);

					node = graph.getNode(from-1);
				    node.addAttribute("ui.label", from);
					from=from-1;
					to=to-1;
					graph.addEdge(from + "-" + to, from, to);
				}
			}

		} catch (Exception e) {
			System.err.format("Erreur lecture fichier ");
			e.printStackTrace();
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

	/*public static void main(String[] args) throws InterruptedException {

		try
		{
			
			MPI.Init(args);
			int rank = MPI.COMM_WORLD.Rank();
			int size = MPI.COMM_WORLD.Size();
			
			GraphAPD g;
			boolean[] paramsOK={true};
			if(rank==0)
			{
				if(args.length==4)
				{
					g = new GraphAPD(args[3]);
					if(g!=null && Integer.parseInt(args[1])!=g.graph.getNodeCount())
					{
						paramsOK[0]=false;
						//System.out.println("Les arguments fournis sont incorrects ou incomplets (le nombre de processus doit être égale au nombre de noeuds du graphe)");
						//System.exit(-1);
					}
				}
				else
					paramsOK[0]=false;
				
				
			}
			
			MPI.COMM_WORLD.Bcast(paramsOK, 0, 1, MPI.BOOLEAN, 0);
			
			if(paramsOK[0])
			{
				g = new GraphAPD(args[3]);
				if(0==rank)
					g.graph.display();
				Algorithm a = new EveilDistribueAlgorithm(g);
				a.start(args);
					
			}
			else
			{
				if (rank == 0) 
					System.out.println("Les arguments fournis sont incorrects ou incomplets (le nombre de processus doit être égale au nombre de noeuds du graphe)");
				
				MPI.Finalize();
				System.exit(-1);
			}
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Une erreur s'est produite: vérifiez le nom du fichier .cnf ou le nombre de processus");
		}
			

	}*/

}
