package apd.graph.utilitaires;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

public class GraphAPD {
	
	private Graph graph;
	
	public GraphAPD(String nomFichier)
	{
		this.graph = new SingleGraph("Graph");
		
		// CSS du Graph
		graph.addAttribute("ui.stylesheet","url('file:///"+System.getProperty("user.dir")+"/files/stylesheet')");
		graph.addAttribute("ui.quality");
		graph.addAttribute("ui.antialias");
        //graph.addAttribute("ui.stylesheet", "edge { fill-color: grey; }" );
		
        initGraph(nomFichier);

	}
	
	/***
	 * 
	 * @param nomFichier
	 * @return true or false selon si le programme réussi à initialiser le graph ou non.
	 */
	public boolean initGraph(String nomFichier)
	{
		try(BufferedReader in = new BufferedReader(new FileReader("files/"+nomFichier))) {
			final Map<Integer,Set<Integer>> graphMap = new LinkedHashMap<Integer,Set<Integer>>();

			
			int vertices = -1;
			final Pattern p = Pattern.compile("p\\s+edge\\s+(\\d+)\\s+(\\d+)\\s*");
			final Matcher mp = p.matcher("");
			for(String line = in.readLine(); line != null; line = in.readLine()) { 
				mp.reset(line);
				if(mp.matches()) { 
					vertices = Integer.parseInt(mp.group(1));
					break;
				} 
			}
			if (vertices<0) throw new Exception("Ligne incorrecte ");
			for(int i = 0; i < vertices; i++) { 
				graphMap.put(i+1, new LinkedHashSet<Integer>(4));
			}
			
			final Pattern e = Pattern.compile("e\\s+(\\d+)\\s+(\\d+)\\s*");
			final Matcher me = e.matcher("");
			
			for(String line = in.readLine(); line != null; line = in.readLine()) { 
				me.reset(line);
				if(me.matches()) { 
					final int from = Integer.parseInt(me.group(1)), to = Integer.parseInt(me.group(2));
					if (!graphMap.containsKey(from)) throw new Exception("Mauvaise arrete: " + from + " in " + line);
					if (!graphMap.containsKey(to)) throw new Exception("Mauvase arrete: " + to + " in " + line);
					graphMap.get(from).add(to); 
					
				} 			
			}
			
			
			for (Map.Entry<Integer, Set<Integer>> entry : graphMap.entrySet()) {
			    String key = Integer.toString(entry.getKey());
			    Set<Integer> values = entry.getValue();
			    if (graph.getNode(key) == null)	
			    	this.graph.addNode(key).addAttribute("ui.label", key);
			    Iterator i=values.iterator();
			    String key2;
			    while(i.hasNext())
			    {
			    	key2=Integer.toString((int)i.next());
			    	if (graph.getNode(key2) == null)	
					    	this.graph.addNode(key2).addAttribute("ui.label", key2);;
			    	this.graph.addEdge(key+"-"+key2, key, key2);
			    }
			}
			
			return true;
		} catch (IOException e) {
			System.out.println("Mauvais fichier: " + nomFichier);
			return false;
			}
		 catch(Exception e)
		{
			 System.out.println("Le format DIMACS n'est pas respecté  "+e);
			return true;
		}
	}
	
	public void afficherGraph()
	{
		this.graph.display();
	}
	
	public static void main(String[] args) throws InterruptedException
	{
		GraphAPD g = new GraphAPD("aim-100-1_6-no-1.cnf");
		SpriteManager sman = new SpriteManager(g.graph);
		Sprite s = sman.addSprite("S1");

		/*for(Edge e:g.graph.getEachEdge()) {
	        System.out.println(e.getId());
	    }*/
		s.attachToEdge("1-2");
		
		g.afficherGraph();
		
		Thread.sleep(2000);
		
		g.getGraph().getEdge("1-2").addAttribute("ui-color", Color.RED);
		for(double i=0.1;i<1.0;i+=0.1)
		{
			s.setPosition(i);
			Thread.sleep(50);
		}
		/*for(double i=1;i>0;i-=0.1)
		{
			s.setPosition(i);
			Thread.sleep(50);
		}*/
		
		
	}
	
	public Graph getGraph()
	{
		return graph;
	}

}
