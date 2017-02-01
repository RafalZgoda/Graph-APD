package apd.graph.frame;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import apd.graph.utilitaires.Algorithm;
import apd.graph.utilitaires.EveilDistribueAlgorithm;
import apd.graph.utilitaires.GraphAPD;
import mpi.Intracomm;
import mpi.MPI;

public class FenetreGraphAPD extends JFrame implements ActionListener{
	
		private String nameFile;
		private GraphAPD graphAPD;
		private int[] algoStarted={0};
		private Viewer viewer;
		
	    public FenetreGraphAPD(String[] args) {

	    	createMenuBar(args);
	    
	        setTitle("Graph APD");
	        Dimension dimMax = Toolkit.getDefaultToolkit().getScreenSize();
	        setMaximumSize(dimMax);

	        setExtendedState(JFrame.MAXIMIZED_BOTH);
	        setSize(1000, 600);
	        setLocationRelativeTo(null);
	        setLayout(new FlowLayout());
	        setDefaultCloseOperation(EXIT_ON_CLOSE);
	        
	    	if(MPI.COMM_WORLD.Rank()!=0)
	    	{
	    		System.out.println(" --- "+MPI.COMM_WORLD.Rank()+" en attente ");
	    		MPI.COMM_WORLD.Recv(algoStarted, 0, 1,MPI.INT,0, 0);
	    		//System.out.println(MPI.COMM_WORLD.Rank()+" apres le recv ");
	    		
	    		if(algoStarted[0]==1)
	    		{
	    			Algorithm a = new EveilDistribueAlgorithm(this.graphAPD);
	    			try {
						a.start(args);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}
	    	}
	    
	    }

	    private void createMenuBar(String[] args) {

	        JMenuBar menubar = new JMenuBar();	        
	        ImageIcon iconOpen = new ImageIcon("open.png");

	        JMenu fileMenu = new JMenu("File");
	        JMenu algos = new JMenu("Algorithms");
	        JMenu initiators = new JMenu("Initiators");
	        JButton start = new JButton("Start");
	        JMenu clear = new JMenu("Clear");
	        JMenuItem openMi = new JMenuItem("Open", iconOpen);
	        

	        JMenuItem exMi = new JMenuItem("Fichier d'exemple");
	        exMi.setToolTipText("Créer un graph par défaut");

	        fileMenu.add(openMi);
	        fileMenu.addSeparator();
	        fileMenu.add(exMi);
	        
	        menubar.add(fileMenu);
	        menubar.add(algos);
	        menubar.add(initiators);
	        menubar.add(start);
	        menubar.add(Box.createHorizontalGlue());
	        menubar.add(clear);

	        setJMenuBar(menubar);
	        
	        JPanel pan=new JPanel();
	        pan.setBackground(Color.white);
	        pan.setLayout(new BorderLayout());
	     
	        Dimension dimMax = Toolkit.getDefaultToolkit().getScreenSize();
	        pan.setPreferredSize(new Dimension((int)dimMax.getWidth() - 100,(int)dimMax.getHeight() - 70));
	        pan.setBorder(BorderFactory.createLineBorder(Color.black));
	        
            
	       exMi.addActionListener((ActionEvent event) -> {
	   
	        	this.nameFile="aim-100-1_6-no-1.cnf";
	        	this.graphAPD = new GraphAPD(nameFile);


	        	viewer = new Viewer(this.graphAPD.getGraph(), Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        		viewer.enableAutoLayout();
                View view = viewer.addDefaultView(false);
                ((Component) view).setBounds(0, 0, pan.getWidth(), pan.getHeight());
                pan.add((Component) view, BorderLayout.CENTER);
                
                this.getContentPane().validate();
                this.getContentPane().repaint();
	        });
	        
	        openMi.addActionListener((ActionEvent event) -> {
    	    	
	        	JFileChooser chooser = new JFileChooser();
	        	chooser.setApproveButtonText("Choix du fichier..."); 
	        	if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
	            {	
	   
	        		nameFile = chooser.getSelectedFile().getName();
	        		this.graphAPD = new GraphAPD(nameFile);

	        		viewer = new Viewer(this.graphAPD.getGraph(), Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
	        		viewer.enableAutoLayout();
	                View view = viewer.addDefaultView(false);
	                ((Component) view).setBounds(0, 0, pan.getWidth(), pan.getHeight());
	                pan.add((Component) view, BorderLayout.CENTER);
	                
	                this.getContentPane().validate();
	                this.getContentPane().repaint();
	                
	        	}
	        });
	       
	        	
	        start.addActionListener(this);
//	        start.addActionListener((ActionEvent event) -> {
//
//	        	 	MPI.Finalize();
//	            	MPI.Init(args);
//	    	    	System.out.println("hello algo avec proc "+MPI.COMM_WORLD.Rank());
	    	    	//FenetreGraphAPD.superFonction();
					
//					int[] newIds = {0,1,2,3,4,5,6,7,8};
//					Algorithm a = new EveilDistribueAlgorithm(FenetreGraphAPD.this.graphAPD);
//					Intracomm newComm = MPI.COMM_WORLD.Create((MPI.COMM_WORLD.Group()).Incl(newIds));
//					try {
//						System.out.println("START");
//						a.start(args, newComm);
//						System.out.println("FIN START");
//					} catch (InterruptedException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}

	            //});
	        
	        this.getContentPane().add(pan, BorderLayout.CENTER);

	        MPI.Finalize();
	      
	    }
	   

	    /**
	     * @param args
	     */
	    public static void main(String[] args) {

	    	MPI.Init(args);
	    	int rank = MPI.COMM_WORLD.Rank();
			int root = 0;
			FenetreGraphAPD ex= new FenetreGraphAPD(args);
			if(root==rank)
			{
				ex.setVisible(true);
			}
			//MPI.Finalize();
	
	    }

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
	    	System.out.println("hello algo avec proc "+MPI.COMM_WORLD.Rank());
	    	if(0==MPI.COMM_WORLD.Rank())
	    	{
				algoStarted[0]=1;
				System.out.println("--- size "+MPI.COMM_WORLD.Size());
				//ArrayList<Integer> list = this.graphAPD.getGraph().getEachNode();
				for(int i=1;i<MPI.COMM_WORLD.Size();i++)
				{
					System.out.println("0 a envoye a "+i+" algoStart = "+algoStarted[0]);
					MPI.COMM_WORLD.Send(algoStarted, 0, 1,MPI.INT,i, 0);
				}
	    	}
		}
}