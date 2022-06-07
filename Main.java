import java.lang.*;
import java.io.*;
import java.util.*;

public class Main extends HeapProcess {
	private static String heapFilename;
	private static String outFilename;
	private static Parser parser;
	private static int idSize;
	
	static final int STRING = 1;
    static final int LOAD_CLASS = 2;
    private static final int UNLOAD_CLASS = 3;
    static final int STACK_FRAME = 4;
    static final int STACK_TRACE = 5;
    private static final int ALLOC_SITES = 6;
    static final int HEAP_SUMMARY = 7;
    private static final int START_THREAD = 0xa;
    private static final int END_THREAD = 0xb;
    private static final int HEAP_DUMP = 0xc;
    private static final int HEAP_DUMP_SEGMENT = 0x1c;
    private static final int HEAP_DUMP_END = 0x2c;
    private static final int CPU_SAMPLES = 0xd;
    private static final int CONTROL_SETTINGS = 0xe;
    
    public static int dataLen;
	
	public static void main(String[] args) throws IOException {
		if(args.length != 2) {
			System.out.println("Provide heapdump file as first argument and output file as second argument");
			System.exit(1);
		}
		heapFilename = args[0];
		outFilename = args[1];
		parser = new Parser(heapFilename, outFilename, DEBUG);
		parser.parseHeader();
		idSize = parser.getIDSize();
		while(true) {
			byte tagType = parser.readTag();
			if(tagType == 0) {
				parser.write("Exitting successfully after reaching EOF");
				break;
			}
			if(tagType == -1) {
				parser.write("Could not read Tagtype");
				break;
			}
			dataLen = parser.TagLength();
			if(DEBUG) {
				parser.write("New Tag of type: " + tagType + " DataLength: " + dataLen);
			}
			switch(tagType) {
			case STRING: processTagString(); break;
			case LOAD_CLASS: processLoadClass(); break;
			case UNLOAD_CLASS: processUnloadClass(); break;
			case STACK_FRAME: processStackFrame(); break;
			case STACK_TRACE: processStackTrace();  break;
			case ALLOC_SITES: processAllocSites(); break;
			case HEAP_SUMMARY: processHeapSummary(); break;
			case START_THREAD: processStartThread(); break;
			case END_THREAD: processEndThread(); break;
			case HEAP_DUMP: processHeapDump(); break;
			case HEAP_DUMP_SEGMENT: processHeapDumpSeg(); break;
			case HEAP_DUMP_END: processHeapDumpEnd(); break;
			case CPU_SAMPLES: processCPUSamples(); break;
			case CONTROL_SETTINGS: processControlSettings(); break;
			default: parser.write("Unknown tag type: " + tagType); parser.skipNbytes(dataLen);
			}
			parser.flush();
		}
	}

	/**
	* ID ID for this string
	* [u1]* UTF8 characters for string (NOT NULL terminated)
	* @throws IOException 
	*/
	private static void processTagString() throws IOException {
		long ID = parser.getID();
		parser.write("ID: " + ID);
		parser.flush();
		byte str[] = parser.readNBytes(dataLen - idSize);
		parser.write("TAG_STRING: " + new String(str));
	}

	/**
	* u4 class serial number (always > 0)
	* ID class object ID
	* u4 stack trace serial number
	* ID class name string ID
	 * @throws IOException 
	*/
	private static void processLoadClass() throws IOException {
		int ClassSerialNo = parser.readInt();
		long ID = parser.getID();
		int StackTraceSerialNo = parser.readInt();
		long ClassNameID = parser.getID();
		parser.write("TAG_CLASS_LOADER ID: " + ID + " Serial No. " + ClassSerialNo + " StackTraceSerialNumber " + StackTraceSerialNo + " ClassNameID: " + ClassNameID);
	}

	/**
	* u4 class serial number
	 * @throws IOException 
	*/
	private static void processUnloadClass() throws IOException {
		int classSerialNo = parser.readInt();
		parser.write("TAG_UNLOAD_CLASS - ClassSerialNumber: "+ classSerialNo);
	}

	/**
	* ID stack frame ID
	* ID method name string ID
	* ID method signature string ID
	* ID source file name string ID
	* u4 class serial number
	* u4 >  0 line number
	*    =  0 no line information available
	*    = -1 unknown location
	*    = -2 compiled method (Not implemented)
	*    = -3 native method (Not implemented)
	 * @throws IOException 
	*/
	private static void processStackFrame() throws IOException {
		long stackFrameID = parser.getID();
		long mathodNameID = parser.getID();
		long methodSigID = parser.getID();
		long sourceFilenameID = parser.getID();
		int classSerialNo = parser.readInt();
		int lineInfo = parser.readInt();
		parser.write("TAG_STACK_FRAME: ID: "+ stackFrameID + " Method name: "+ mathodNameID + 
						" Method Signature: "+ methodSigID + " SourceFileName: " + sourceFilenameID +" Class Serial Number: " + classSerialNo + 
							" LineInfo: "+ lineInfo);
	}

	/**
	* u4 stack trace serial number
	* u4 thread serial number
	* u4 number of frames
	* [ID]* series of stack frame ID's
	 * @throws IOException 
	*/
	private static void processStackTrace() throws IOException {
		int stackTraceSerialNo = parser.readInt();
		int threadSerialNo = parser.readInt();
		int NoOfFrames = parser.readInt();
		parser.write("TAG_STACK_TRACE: StackTraceSerialNumber: " + stackTraceSerialNo + 
						" ThreadSerialNumber: " + threadSerialNo);
		for(int i=0; i<NoOfFrames; ++i) {
			processStackTraceFrame();
		}
	}
	
	/**
	* ID stack frame ID
	 * @throws IOException 
	**/
	private static void processStackTraceFrame() throws IOException {
		long stackFrameID = parser.getID();
		parser.write("Stack Frame ID: "+ stackFrameID);
	}

	/**
	* u2 Bit mask flags:
	*   0x1 incremental vs. complete
	*   0x2 sorted by allocation vs. line
	*   0x4 whether to force GC (Not Implemented)
	*
	* u4 cutoff ratio (floating point)
	* u4 total live bytes
	* u4 total live instances
	* u8 total bytes allocated
	* u8 total instances allocated
	* u4 number of sites that follow:
	*   for each:
	*     u1 array indicator: 0 means normal object, non-zero means an array of this type (See Basic Type)
	*     u4 class serial number
	*     u4 stack trace serial number
	*     u4 number of live bytes
	*     u4 number of live instances
	*     u4 number of bytes allocated
	*     u4 number of instances allocated
	 * @throws IOException 
	*/
	private static void processAllocSites() throws IOException {
		short gcFlags = parser.readShort();
		float cutOffRatio = parser.readFloat();
		int totalLiveBytes = parser.readInt();
		int totalLiveInstances = parser.readInt();
		long bytesAllocated = parser.readLong();
		long totalInstancesAllocated = parser.readLong();
		int NoOfSites = parser.readInt();
		parser.write("TAG_ALLOC_SITES: GC_Flags: "+ gcFlags + " cutOffRatio: "+ cutOffRatio + " totalLiveBBytes: " + 
					totalLiveBytes + " totalLiveInstances: " + totalLiveInstances + " Bytes Allocated: "+bytesAllocated + 
					" TotalInstancesAllocated: " + totalInstancesAllocated + " NoOfSites: " + NoOfSites);
		for(int i=0; i<NoOfSites; ++i) {
			byte arrayInd = parser.readByte();
			int classSerialNo = parser.readInt();
			int stackSerialNo = parser.readInt();
			int NumLiveBytes = parser.readInt();
			int NumLiveInstances = parser.readInt();
			int NumBytesAllocated = parser.readInt();
			int NumInstancesAllocated = parser.readInt();
			parser.write("ALLOC_SITE - " + i + " ArrayIndicator: " + arrayInd + " Class Serial Number: " +
						classSerialNo + " StackTraceSerialNumber: "+stackSerialNo + " Number of Live bytes: " +
						NumLiveBytes + " Number of Live instances: " + NumLiveInstances + " Number of Bytes Allocated: " +
						NumBytesAllocated + " Number of Live Instances: " + NumInstancesAllocated);
		}
	}

	/**
	* u4 total live bytes
	* u4 total live instances
	* u8 total bytes allocated
	* u8 total instances allocated
	 * @throws IOException 
	*/
	private static void processHeapSummary() throws IOException {
		int TotalLiveBytes = parser.readInt();
		int TotalLiveInstances = parser.readInt();
		long TotalBytesAllocated = parser.readLong();
		long TotalInstancesAllocated = parser.readLong();
		parser.write("TAG_HEAP_SUMMARY: TotalLiveBytes: " + TotalLiveBytes + " TotalLiveInstances: "+
					TotalLiveInstances + " TotalBytesAllocated: " + TotalBytesAllocated + " TotalInstancesAllocated: "+ TotalInstancesAllocated);
	}

	/**
	u4 thread serial number
	ID thread object ID
	u4 stack trace serial number
	ID thread name string ID
	ID thread group name ID
	ID thread parent group name ID
	 * @throws IOException 
	*/
	private static void processStartThread() throws IOException {
		int ThreadSerialNo = parser.readInt();
		long threadID = parser.getID();
		int StackTraceSerialNo = parser.readInt();
		long ThreadName = parser.getID();
		long ThreadGroupID = parser.getID();
		long ThreadParentGroupID = parser.getID();
		parser.write("TAG_START_THREAD: ThreadSerialNumber: " + ThreadSerialNo + " ThreadID: " + threadID +
					" StackTraceSerialNumber: " + StackTraceSerialNo + " ThreadName: "+ ThreadName + 
					" ThreadGroupID: " + ThreadGroupID + " ThreadParentGroupID: " + ThreadParentGroupID);
	}

	/**
	u4 thread serial number
	 * @throws IOException 
	*/
	private static void processEndThread() throws IOException {
		int ThreadSerialNumber = parser.readInt();
		parser.write("TAG_THREAD_END: SerialNumber: " + ThreadSerialNumber);
	}

	private static void processHeapDump() throws IOException {
		// TODO Auto-generated method stub
		parser.skipNbytes(dataLen);
	}

	private static void processHeapDumpSeg() throws IOException {
		// TODO Auto-generated method stub
		parser.skipNbytes(dataLen);
	}

	/**
	* Terminates a series of HEAP DUMP SEGMENTS.  Concatenation of HEAP DUMP SEGMENTS equals a HEAP DUMP.
	*/
	private static void processHeapDumpEnd() {
		if(dataLen != 0) {
			parser.write("TAG_HEAP_DUMP_END has dataLen: " + dataLen + " but wanted it to be 0");
		}
		parser.write("TAG_HEAP_DUMP_END");
	}

	/**
	u4 total number of samples
	u4 number of traces that follow:
	  for each
	    u4 number of samples
	    u4 stack trace serial number
	 * @throws IOException 
	*/
	private static void processCPUSamples() throws IOException {
		int NumSamples = parser.readInt();
		int NumTraces = parser.readInt();
		parser.write("TAG_CPU_SAMPLES: Number of Samples: "+NumSamples + " Number Of Traces: " + NumTraces);
		for(int i=0; i<NumTraces; ++i) {
			int NumberOfSamples = parser.readInt();
			int StackTraceSerialNo = parser.readInt();
			parser.write("SAMPLE " + i + " Number of Samples: " + NumberOfSamples + " StackTraceSerialNumber: " + StackTraceSerialNo);
		}
	}

	/**
	u4 Bit mask flags:
	0x1 alloc traces on/off
	0x2 cpu sampling on/off

	u2 stack trace depth
	 * @throws IOException 
	*/
	private static void processControlSettings() throws IOException {
		int controlFlag = parser.readInt();
		short StackTraceDepth = parser.readShort();
		parser.write("TAG_CONTROL_SETTINGS: controlFlags: " + controlFlag + " StackTraceDepth: " + StackTraceDepth);
	}
}