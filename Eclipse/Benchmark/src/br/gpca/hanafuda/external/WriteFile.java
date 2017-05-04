package br.gpca.hanafuda.external;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;


public class WriteFile {
	
	private String path;
	private boolean append = false;
	
	public WriteFile( String file_path) {
		path = file_path;
	}
	
	public WriteFile( String file_path , boolean append_value ) {

		path = file_path;
		append = append_value;
	}
    
	public void println( String textLine ) {
		try{
			FileWriter write = new FileWriter( path , append);
			PrintWriter print_line = new PrintWriter(write);
			print_line.println(textLine);
			print_line.close();
		} catch (IOException ex){
			ex.printStackTrace();
		}
	}
	
	public void writeNode(String parent, String child){
		println(parent + " -> " + child + ";");
	}
	
	public void writeNodeAttr(String node, String label, String value, String color){
		println(node + " [label=" + (char)(34) + label + "\n value=" + value + (char)(34) + " color=" + color + "];");
	}
	
	public void delete() {
	    // A File object to represent the filename
	    File f = new File(path);

	    // Make sure the file or directory exists and isn't write protected
	    if (!f.exists())
	    {
	    	System.out.println("Delete: no such file or directory: " + path);
	    	return;
	    }	

	    if (!f.canWrite())
	      throw new IllegalArgumentException("Delete: write protected: "
	          + path);

	    // If it is a directory, make sure it is empty
	    if (f.isDirectory()) {
	      String[] files = f.list();
	      if (files.length > 0)
	        throw new IllegalArgumentException(
	            "Delete: directory not empty: " + path);
	    }

	    // Attempt to delete it
	    boolean success = f.delete();

	    if (!success)
	      throw new IllegalArgumentException("Delete: deletion failed");
	  }
}



  


