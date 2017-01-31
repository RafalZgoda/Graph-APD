package apd.graph.utilitaires;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graphstream.graph.Node;

import mpi.MPI;
import mpi.Status; 

public class DFS1Algorithm implements Serializable{
	
	private int[][] matrixGraph;
	private ArrayList<Integer> voisins;
	private int parent;
	private boolean marque;
	
	public void initVoisins(int index)
	{
		voisins=new ArrayList<Integer>();
		for(int i=0;i<matrixGraph[index].length;i++)
		{
			if(matrixGraph[index][i]==1)
				voisins.add(i);
		}
	}
	
	public void readDimacs(String fileName)
	{
		try {
			BufferedReader in = new BufferedReader(new FileReader("files/" + fileName));

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
				throw new Exception("nombre de noeuds inferieur a 0 ");
			if (edges <= 0)
				throw new Exception("nombre d'arette inferieur a 0 ");

			matrixGraph=new int[vertices][vertices];

			final Pattern e = Pattern.compile("e\\s+(\\d+)\\s+(\\d+)\\s*");
			final Matcher me = e.matcher("");
			int from, to;

			for (String line = in.readLine(); line != null; line = in.readLine()) {
				me.reset(line);
				if (me.matches()) {
					from = Integer.parseInt(me.group(1));
					to = Integer.parseInt(me.group(2));
					
					if (from == 0 || from > vertices)
						throw new Exception("Mauvaise arrete: " + from + " in " + line);
					if (to == 0 || to > vertices)
						throw new Exception("Mauvase arrete: " + to + " in " + line);

					matrixGraph[from-1][to-1]=1;
					matrixGraph[to-1][from-1]=1;
					
				}
			}
			in.close();
		} catch (Exception e) {
			System.out.println("Une erreur s'est produite dans la lecture du fichier cnf");
			e.printStackTrace();
		}
	}
	
	public void afficheGraph()
	{
		System.out.print("[");
		for(int i=0;i<this.matrixGraph.length;i++)
		{
			System.out.print("[");
			for(int j=0;j<this.matrixGraph[i].length;j++)
			{
				System.out.print(this.matrixGraph[i][j]+",");
			}
			System.out.println("]");
		}
		System.out.println("]");
	}
	
	public void visite(int rank,int parent)
	{
		
		char[] message;
		if(!voisins.isEmpty())
		{
			message="dfs1".toCharArray();
			int current=voisins.remove(0);
			MPI.COMM_WORLD.Send(message, 0, 4, MPI.CHAR, current , 0);
			System.out.println(rank+" a envoye DFS1 a "+current+"\n");
		}
		else
		{
			//System.out.println("rank "+rank+" : ma liste de voisins est vide et mon parent : "+parent);
			if(parent>=0)
			{
				message="back".toCharArray();
				MPI.COMM_WORLD.Send(message, 0, 4, MPI.CHAR, parent , 0);
				System.out.println(rank+" a envoye BACK a "+parent+"\n");
			}
			else
			{
				System.out.println("Fin");
			}
		}
	}
	
	public void surReceptionMessage(String message,int rank, int source)
	{
		if(message.equals("dfs1"))
		{
			System.out.println(rank+" a recu DFS1 de "+source);
			if(marque)
			{
				voisins.remove((Integer) source);
				char[] msg="back".toCharArray();
				MPI.COMM_WORLD.Send(msg, 0, 4, MPI.CHAR, source , 0);
				System.out.println(rank+" a envoye BACK a "+source+"\n");
			}
			else
			{
				//System.out.println("j'ai été marqué par "+source);
				parent=source;
				marque=true;
				voisins.remove((Integer)parent);
				this.visite(rank, parent);
			}
		}
		else
		{
			System.out.println(rank+" a recu BACK de "+source);
			this.visite(rank, parent);
		}
	}
	
	
	public static void main(String args[]) throws Exception { 
		
		MPI.Init(args);
		
		int rank = MPI.COMM_WORLD.Rank();
		int size = MPI.COMM_WORLD.Size();
		char[] message="null".toCharArray();
		int root=0;
		DFS1Algorithm t = new DFS1Algorithm();
		boolean[] paramsOK={true};
		if(rank==root)
		{
			if(args.length==4)
			{
				t.readDimacs(args[3]);
				if(t.matrixGraph.length!=Integer.parseInt(args[1]))
					paramsOK[0]=false;
			}
			else
				paramsOK[0]=false;
		}
		
		MPI.COMM_WORLD.Bcast(paramsOK, 0, 1, MPI.BOOLEAN, 0);
		if(!paramsOK[0])
		{
			if (rank == 0) 
				System.out.println("Les arguments fournis sont incorrects ou incomplets (le nombre de processus doit être égale au nombre de noeuds du graphe)");
			
			MPI.Finalize();
			System.exit(-1);
		}
	
		t.readDimacs(args[3]);
		t.initVoisins(rank);
		int nbVoisins = t.voisins.size();
		Status s;
		if(rank==0)
		{
			t.afficheGraph(); // Simple affichage de la matrice
		}
		MPI.COMM_WORLD.Barrier();
		
		if(root==rank)
		{
			t.marque=true;
			t.parent=-1;
			t.visite(rank, t.parent);
			//t.afficheGraph();
			for(int i=0;i<nbVoisins;i++)
			{
				s=MPI.COMM_WORLD.Recv(message, 0, 4, MPI.CHAR, MPI.ANY_SOURCE, 0);
				t.surReceptionMessage(String.valueOf(message), rank, s.source);
			}
		}
		else
		{
			for(int i=0;i<nbVoisins;i++)
			{
				s=MPI.COMM_WORLD.Recv(message, 0, 4, MPI.CHAR, MPI.ANY_SOURCE, 0);
				t.surReceptionMessage(String.valueOf(message), rank, s.source);
			}
						
			
		}

		MPI.Finalize();      
	}
}
