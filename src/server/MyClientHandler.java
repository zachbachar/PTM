package server;

import java.io.*;
import java.util.ArrayList;

import cache.FilesCacheManager;
import exceptions.PipeGameBuilderException;
import exceptions.PipeGameTileException;
import interfaces.CacheManager;
import interfaces.ClientHandler;
import interfaces.Solver;
import pipeGame.PipeGameLevel;
import pipeGame.PipeGameSolver;

public class MyClientHandler implements ClientHandler {

	private String problem = null;
	private Solver<PipeGameLevel> solver;
	private CacheManager filesCacheManager;
	private PipeGameLevel _pgl;
	private BufferedReader inFClient = null;

	public MyClientHandler() {
		filesCacheManager = FilesCacheManager.getInstance();
		//System.out.println("new pipe game solver");
		this.solver = new PipeGameSolver();
	}
	
	public MyClientHandler(InputStream in) {
		this();
		inFClient = new BufferedReader(new InputStreamReader(in));
		createProblem(inFClient);
	}
	
	@Override
	public void handleClient(InputStream inFromClient, OutputStream outToClient) throws IOException {
		//System.out.println("starting handle client");
		PrintWriter outTC = new PrintWriter(outToClient);
		if (inFClient == null)
				inFClient = new BufferedReader(new InputStreamReader(inFromClient));
		try {
			if(problem == null)
				createProblem(inFClient);
			
			// check if we have the solution
			String sol = filesCacheManager.load(problem);

			// we don't have solution
			if (sol == null) {
				//System.out.println("new problem");
				// ask for solution from solver
				try {
					if(_pgl != null)
						sol = solver.solve(_pgl);
					else
						sol = solver.solve(new PipeGameLevel(problem));
				} catch (PipeGameBuilderException e) {
					// TODO Auto-generated catch block
					System.out.println("pipe game builder exception");
					e.printStackTrace();
				} catch (PipeGameTileException e) {
					// TODO Auto-generated catch block
					System.out.println("pipe game tile exception");
					e.printStackTrace();
				} 
				
				System.out.println("saving solution to file");
				// save the solution to file
				filesCacheManager.save(problem, sol);
				
				System.out.println("sending " + sol + "to client");
				// send solution to client
				outTC.write(sol);
				outTC.println("done");
				outTC.flush();
			}
			// we have solution
			else {
				// send solution to client
				System.out.println("sol found in files");
				System.out.println("sending " + sol + "to client");
				outTC.write(sol);
				outTC.println("done");
				outTC.flush();
			}

			// close client connection
			inFClient.close();
			outTC.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setSolver(Solver<PipeGameLevel> solver) {
		this.solver = solver;
	}
	
	public int getPriority() {
		if(problem != null) {
			_pgl = null;
			try {
				_pgl = new PipeGameLevel(problem);
			} catch (PipeGameBuilderException e) {
				// TODO Auto-generated catch block
				System.out.println("pipe game builder exception");
				e.printStackTrace();
			} catch (PipeGameTileException e) {
				// TODO Auto-generated catch block
				System.out.println("pipe game tile exception");
				e.printStackTrace();
			}
			return _pgl.getLevelSize();
		}
		return 0;
	}
	
	private void createProblem(BufferedReader in) {
		String line;
		ArrayList<String> lines = new ArrayList<String>();
        
        try {
            while (!(line = in.readLine()).equals("done")) {
                lines.add(line);
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("got problem from client");
        
        //System.out.println(lines);
        this.problem = String.join(System.lineSeparator(), lines);
	}
	
	
}
	