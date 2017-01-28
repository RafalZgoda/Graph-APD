package apd.graph.utilitaires;

import java.awt.Color;
import java.util.List;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.Viewer;

import mpi.MPI; 

public class ChangRobertsAlgorithm implements Algorithm {

	private int[] noeudInitiateur;
	private int[] etatNoeud; // 0: passif, 1: candidat, 2: elu, 3: battu
	private GraphAPD graph;
	
	public ChangRobertsAlgorithm(GraphAPD g)
	{
		this.graph=g;
	}
	public ChangRobertsAlgorithm(GraphAPD g, int[] initiateurs, int nombreNoeuds)
	{
		this.graph=g;
		this.noeudInitiateur=initiateurs;
		this.etatNoeud=new int[nombreNoeuds];
		for(int i=0;i<nombreNoeuds;i++)
		{
			if(i==noeudInitiateur[i])
				this.etatNoeud[i]=1;
			
		}
	}
	public boolean start(String[] args) throws InterruptedException
	{
		// Thread.sleep(2000);
				char[] message;
				Node node;
				System.out.println("hhhhhhhhhhhhellllool");
				int current;
				Viewer viewer=this.graph.getGraph().display();
				MPI.Init(args);
				int rank = MPI.COMM_WORLD.Rank();
				int root = 0;
				int size = MPI.COMM_WORLD.Size();
				System.out.println("size : "+size);
				if (root == rank) {
					
					message = "hello".toCharArray();
					System.out.println(message.length);
					System.out.println("j'ai envoye : " + String.valueOf(message)+ " rank : "+rank);
					MPI.COMM_WORLD.Send(message, 0, message.length, MPI.CHAR, 1, 0);
					animation(1+"-"+2,viewer.getGraphicGraph());
					
				}
				else
				{
					node=GraphAPD.graph.getNode(rank);
					message = "totot".toCharArray();
					MPI.COMM_WORLD.Recv(message, 0, 5, MPI.CHAR, MPI.ANY_SOURCE, 0);
					System.out.println("J'ai recu : " + String.valueOf(message)+" rank : "+rank);
					
					if(node.hasAttribute("etat") && node.getAttribute("etat").equals("actif"))
					{
						System.out.println("je ne fais rien car je suis deja actif : "+rank);
					}
					else
					{	
						node.addAttribute("etat", "actif");
						List<Integer> voisins=node.getAttribute("voisins");
						for(int i=0;i<voisins.size();i++)
						{
							current=voisins.get(i);
							System.out.println("j'ai envoye : " + String.valueOf(message)+ " rank : "+rank+" au voisin :"+current);
							MPI.COMM_WORLD.Send(message, 0, message.length, MPI.CHAR, current, 0);
							animation((rank+1)+"-"+(current+1),null);
							
						}
					}
					
				}
				/*if (me == 1) {
					message = "totot".toCharArray();
					this.graph.afficherGraph();
					MPI.COMM_WORLD.Recv(message, 0, 5, MPI.CHAR, 0, 0);
					System.out.println("J'ai recu : " + String.valueOf(message));
					// this.animation("1-2");

					SpriteManager sman = new SpriteManager(this.graph.getGraph());
					Sprite s = sman.addSprite("1-2");
					s.addAttribute("shape", "box");

					s.attachToEdge("1-2");
					this.graph.getGraph().getEdge("1-2").addAttribute("ui-color", Color.RED);
					for (double i = 0.1; i < 1.0; i += 0.1) {
						s.setPosition(i);
						Thread.sleep(1000);
					}
					
				}*/
		

		MPI.Finalize();   
		return true;
	}
	
	public void animation(String arrete,Graph g) throws InterruptedException
	{
		System.out.println("je suis dans animation avec arrete : "+arrete);
		SpriteManager sman = new SpriteManager(GraphAPD.graph);
		Sprite s = sman.addSprite(arrete);
		s.addAttribute("shape", "box");
		s.attachToEdge(arrete);
		System.out.println(s.attached());
		GraphAPD.graph.getEdge(arrete).addAttribute("ui-color", Color.RED);
		for (double i = 0.1; i < 1.0; i += 0.1) {
			s.setPosition(i);
			Thread.sleep(1000);
		}

	}
	public static void main(String[] args)
	{
		System.out.println("riiene ");
	}
}
