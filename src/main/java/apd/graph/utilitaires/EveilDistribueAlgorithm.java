package apd.graph.utilitaires;

import java.awt.Color;
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import mpi.MPI;
import mpi.Status; 

public class EveilDistribueAlgorithm implements Algorithm {

	private int[] noeudInitiateur;
	private GraphAPD graph;
	
	public EveilDistribueAlgorithm(GraphAPD g)
	{
		this.graph=g;
	}
	public EveilDistribueAlgorithm(GraphAPD g, int[] initiateurs, int nombreNoeuds)
	{
		this.graph=g;
		this.noeudInitiateur=initiateurs;
	}
	public boolean start(String[] args) throws InterruptedException
	{

				int[] hello={24};
				int[] messageAnimation=new int[2];
				Node node;
		
				int current;
				
				//MPI.Init(args);
				int rank = MPI.COMM_WORLD.Rank();
				int root = 0;
				int size = MPI.COMM_WORLD.Size();
				Status s;
				if (root == rank) {
					//this.graph.getGraph().display();
							
					node=this.graph.getGraph().getNode(0);
					node.addAttribute("etat", "actif");
					
					Iterator<Node> it = node.getNeighborNodeIterator();

				    while(it.hasNext()) {
				    	Node n =((Node)it.next());
					    
					    current=n.getIndex();
					    //System.out.println("current : "+current);

					    MPI.COMM_WORLD.Isend(hello, 0, 1, MPI.INT, current, 0);
						System.out.println("Proc 0 a envoyé hello au procs "+current);
						animation(new int[]{0,current});
						
				    }

				    for(int i=0;i<size;i++)
					{
						//System.out.println("proc 0 a recu une demande d'animation: "+messageAnimation[0]+"-"+messageAnimation[1]);
						MPI.COMM_WORLD.Recv(messageAnimation, 0, 2, MPI.INT, MPI.ANY_SOURCE, 0);
						animation(messageAnimation);
					}
				    
				    
				   
				}
				else
				{
					node=this.graph.getGraph().getNode(rank);
					
					Iterator<Node> it = node.getNeighborNodeIterator();

				    while(it.hasNext()) 
				    {
				   
					    it.next();
						s=MPI.COMM_WORLD.Recv(hello, 0, 1, MPI.INT, MPI.ANY_SOURCE, 0);
						System.out.println("----> Proc "+rank+" a recu hello de : "+s.source);
						
						surReceptionEveil(node,hello,messageAnimation,rank,s.source);
				    }
					
				}
		MPI.Finalize();   
		return true;
	}
	
	public void surReceptionEveil(Node node,int[] hello,int[] messageAnimation,int rank, int source)
	{
		int current;
		if(node.hasAttribute("etat") && node.getAttribute("etat").equals("actif"))
		{
			System.out.println("je ne fais rien car je suis deja actif : "+rank);
		}
		else
		{	
			node.addAttribute("etat", "actif");
			
			Iterator<Node> i = node.getNeighborNodeIterator();

		    while(i.hasNext()) 
		    {
		   
		       Node n =((Node)i.next());
		     
		       current=n.getIndex();
		       if(source!=current && current!=0)
		       {
			       System.out.println("Proc "+rank+" a envoye hello au voisin :"+current);
				   MPI.COMM_WORLD.Isend(hello, 0, 1, MPI.INT, current, 0);
				   messageAnimation[0]=rank;
				   messageAnimation[1]=current;
				   System.out.println("Proc "+rank+" a envoyé "+(rank)+"-"+(current)+" au processus root");
				   MPI.COMM_WORLD.Isend(messageAnimation, 0, 2, MPI.INT, 0, 0);
				   
		       }
		       else
		       {
		    	   System.out.println("proc "+rank+" a ete eveile par "+source+" pas besoin de lui renvoyer");
		       }
		       
		    }
		}
	}
	
	public void animation(int[] messageAnimation) throws InterruptedException
	{
		String arete=messageAnimation[0]+"-"+messageAnimation[1];
		String areteInverse=messageAnimation[1]+"-"+messageAnimation[0];
		System.out.println("je suis dans animation() avec l'arete : "+arete);
		boolean inverse=false;
		Edge e = this.graph.getGraph().getEdge(arete);
		if(e==null)
		{
			inverse=true;
			arete=areteInverse;
		}

			
		this.graph.getGraph().getEdge(arete).addAttribute("ui-color", Color.RED);
		
		SpriteManager sman = new SpriteManager(this.graph.getGraph());
		Sprite s = sman.addSprite(arete);
		s.addAttribute("shape", "box");
		s.attachToEdge(arete);
		
		for (double i = 0.1; i < 1.0; i += 0.1) {
			if(!inverse)
				s.setPosition(i);
			else
				s.setPosition(1-i);
			Thread.sleep(100);
		}
		//s.detach();
		//s.attachToEdge(arete);

	}
	public static void main(String[] args)
	{
		System.out.println("riiene ");
	}
}
