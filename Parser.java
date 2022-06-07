import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Parser {
	private DataInputStream heapSource;
	private BufferedWriter out;
	private int idSize;
	private boolean DEBUG;
	
	public Parser(String heapfile, String outFile, boolean debug) {
		try {
			heapSource = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(heapfile))));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(outFile.length()==0) {
			out = new BufferedWriter(new OutputStreamWriter(System.out));
		}
		else {
			try {
				out = new BufferedWriter(new FileWriter(new File(outFile)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		DEBUG = debug;
	}
	
	public int getIDSize() {
		return idSize;
	}
	
	public long getID() throws IOException {
		if(idSize==8)
			return heapSource.readLong();
		else if(idSize==4)
			return heapSource.readInt();
		else {
			if(DEBUG) {
				System.out.println("ID Size is not 4 or 8. It is: " + idSize);
			}
			throw new IOException();
		}
	}

	public void parseHeader() {
		if(DEBUG) {
			byte version[] = new byte[18];
			try {
				heapSource.readFully(version);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				heapSource.skipBytes(1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(new String(version));
		}
		else {
			try {
				heapSource.skipBytes(19);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			idSize=heapSource.readInt();
			heapSource.skip(8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(DEBUG) {
			System.out.println("ID SIZE: "+ idSize);
		}
	}
	
	public byte readTag() {
		try {
			return heapSource.readByte();
		} catch(EOFException e) {
			if(DEBUG) {
				System.out.println("SUCCESSFULLY COMPLETED -- EOF REACHED");
				return (byte)0;
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(DEBUG) {
			System.out.println("ERROR READING TAG");
		}
		return (byte)-1;
	}
	
	public int TagLength() throws IOException {
		heapSource.skipBytes(4);		//SKIP 4 bytes which contains microSince
		return heapSource.readInt();
	}
	
	public void skipNbytes(long n) throws IOException {
		heapSource.skipNBytes(n);
	}
	
	public byte[] readNBytes(int n) throws IOException {
		byte b[] = new byte[n];
		heapSource.readFully(b);
		return b;
	}
	
	public byte readByte() throws IOException {
		return heapSource.readByte();
	}
	
	public short readShort() throws IOException {
		return heapSource.readShort();
	}
	
	public float readFloat() throws IOException {
		return heapSource.readFloat();
	}
	
	public int readInt() throws IOException {
		return heapSource.readInt();
	}
	
	public long readLong() throws IOException {
		return heapSource.readLong();
	}
	
	public void write(String a) {
		try {
			out.write(a + '\n');
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void flush() throws IOException {
		out.flush();
	}
}