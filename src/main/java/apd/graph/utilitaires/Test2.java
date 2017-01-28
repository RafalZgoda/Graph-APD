package apd.graph.utilitaires;

import mpi.MPI;

public class Test2 {

	    static int sharedVar = 0;

	public static void main(String[] args) throws Exception{

	    MPI.Init(args);
	    int rank = MPI.COMM_WORLD.Rank();
	    sharedVar++;
	    System.out.println("Proc <"+rank+">: sharedVar = <"+sharedVar+">");
	    MPI.Finalize();
	    }
	
	/*public static void main(String[] args)
	{
		MPI.Init(args);
        int me = MPI.COMM_WORLD.Rank();
        int tasks = MPI.COMM_WORLD.Size();

        MPI.COMM_WORLD.Barrier();
        Test t = new Test();
        if(me == 0)
        {
            
            try
    		{
    			t.readDimacs("anneau.cnf");
    			t.matrixGraph[0][7]=1;
    			t.matrixGraph[1][7]=1;
    			t.matrixGraph[2][7]=1;
    			System.out.println("root \n ");
    			t.afficheGraph();
	            ByteBuffer byteBuff = ByteBuffer.allocateDirect(2000 + MPI.SEND_OVERHEAD);
	            MPI.Buffer_attach(byteBuff);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutput out = null;
                out = new ObjectOutputStream(bos);
                out.writeObject(t);
                byte[] bytes = bos.toByteArray();

                System.out.println("Serialized to " + bytes.length);

                MPI.COMM_WORLD.Isend(bytes, 0, bytes.length, MPI.BYTE, 1, 0);
            }
            catch(Exception e)
    		{
    			System.out.println("une erreur s'est produite dans la lecture du fichier cnf");
    			e.printStackTrace();
    		}
        }
        if(me==1)
        {
            byte[] bytes = new byte[2000];
            Test recv = null;
            MPI.COMM_WORLD.Recv(bytes, 0, 2000, MPI.BYTE, MPI.ANY_SOURCE, 0);

            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInput in = null;
            try
            {
                in = new ObjectInputStream(bis);
                Object obj = in.readObject();
                recv = (Test)obj;
                t=recv;
                System.out.println("procs 1 \n ");
                t.afficheGraph();
            }
            catch(IOException ex)
            {

            }
            catch(ClassNotFoundException cnf)
            {

            }
        }
        
        MPI.COMM_WORLD.Barrier();
        
        if(me==0)
        {
        	t.matrixGraph[1][6]=1;
        	t.matrixGraph[0][6]=1;
        	System.out.println("root \n  apres ");
        	t.afficheGraph();
        }
        if(me==1)
        {
        	 System.out.println("procs 1 apres \n");
             t.afficheGraph();
        }
        MPI.Finalize();
    }*/
}
