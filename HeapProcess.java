import java.io.*;

public abstract class HeapProcess {
	
	public static int dataLen;
	public static final boolean DEBUG = true;
	
	static final int ROOT_UNKNOWN = 0xff;
    static final int ROOT_JNI_GLOBAL = 1;
    static final int ROOT_JNI_LOCAL = 2;
    static final int ROOT_JAVA_FRAME = 3;
    static final int ROOT_NATIVE_STACK = 4;
    static final int ROOT_STICKY_CLASS = 5;
    static final int ROOT_THREAD_BLOCK = 6;
    static final int ROOT_MONITOR_USED = 7;
    static final int ROOT_THREAD_OBJECT = 8;
    static final int CLASS_DUMP = 0x20;
    static final int INSTANCE_DUMP = 0x21;
    static final int OBJECT_ARRAY_DUMP = 0x22;
    static final int PRIMITIVE_ARRAY_DUMP = 0x23;
    
    static final int HEAP_DUMP_INFO                = 0xfe;
    static final int ROOT_INTERNED_STRING          = 0x89;
    static final int ROOT_FINALIZING               = 0x8a;
    static final int ROOT_DEBUGGER                 = 0x8b;
    static final int ROOT_REFERENCE_CLEANUP        = 0x8c;
    static final int ROOT_VM_INTERNAL              = 0x8d;
    static final int ROOT_JNI_MONITOR              = 0x8e;
    static final int UNREACHABLE                   = 0x90; /* deprecated */
    static final int PRIMITIVE_ARRAY_NODATA_DUMP   = 0xc3;
    
    static final int OBJECT = 2;
    static final int BOOLEAN = 4;
    static final int CHAR = 5;
    static final int FLOAT = 6;
    static final int DOUBLE = 7;
    static final int BYTE = 8;
    static final int SHORT = 9;
    static final int INT = 10;
    static final int LONG = 11;
    
    public static Parser parser;
	public static int idSize;
	
    public static void processHeapTag() throws IOException {
    	int tagType;
    	while(dataLen>0) {
    		tagType = parser.readByte();
    		dataLen -= 1;
    		switch(tagType) {
    		case ROOT_UNKNOWN: processRootUnknown(); break;
    		case ROOT_JNI_GLOBAL: processRootJNIGlobal(); break;
    		case ROOT_JNI_LOCAL: processRootJNILocal(); break;
    		case ROOT_JAVA_FRAME: processRootJavaFrame(); break;
    		case ROOT_NATIVE_STACK: processRootNativeStack(); break;
    		case ROOT_STICKY_CLASS: processRootStickyClass(); break;
    		case ROOT_THREAD_BLOCK: processRootThreadBlock(); break;
    		case ROOT_MONITOR_USED: processRootMonitorUsed(); break;
    		case ROOT_THREAD_OBJECT: processRootThreadObject(); break;
    		case CLASS_DUMP: processClassDump(); break;
    		case INSTANCE_DUMP: processInstanceDump(); break;
    		case OBJECT_ARRAY_DUMP: processObjectArrayDump(); break;
    		case PRIMITIVE_ARRAY_DUMP: processPrimitiveArrayDump(); break;
    		}
    		parser.write("DATA_LEN: " + dataLen);
    		parser.flush();
    	}
    }

    /*
    HEAP_ROOT_UNKNOWN
    ID object ID
    */
	private static void processRootUnknown() throws IOException {
		// TODO Auto-generated method stub
		long ObjectID = parser.getID();
		parser.write("HEAP_ROOT_UNKNOWN: ID: " + ObjectID);
		dataLen -= idSize;
	}

	/*
	HEAP_ROOT_JNI_GLOBAL
	ID object ID
	ID JNI global ref ID
	*/
	private static void processRootJNIGlobal() throws IOException {
		// TODO Auto-generated method stub
		long ObjectID = parser.getID();
		long JNIGlobalReferenceID = parser.getID();
		parser.write("HEAP_ROOT_JNI_GLOBAL: ID: "+ObjectID + " JNI Global Reference ID: " + JNIGlobalReferenceID);
		dataLen -= 2*idSize;
	}

	/*
	HEAP_ROOT_JNI_LOCAL
	ID object ID
	u4 thread serial number
	u4 frame number in stack trace(-1 for empty)
	*/
	private static void processRootJNILocal() throws IOException {
		// TODO Auto-generated method stub
		long ObjectID = parser.readLong();
		int ThreadSerialNo = parser.readInt();
		int FrameNumInStackTrace = parser.readInt();
		parser.write("HEAP_ROOT_JNI_LOCAL: ID: " + ObjectID + " ThreadSerialNumber: " + ThreadSerialNo +
					" Frame Number in Stack Trace: " + FrameNumInStackTrace);
		dataLen -= idSize + 8;
	}

	/*
	HEAP_ROOT_JAVA_FRAME
	ID object ID
	u4 thread serial number
	u4 frame number in stack trace(-1 for empty)
	*/
	private static void processRootJavaFrame() throws IOException {
		// TODO Auto-generated method stub
		long ObjectID = parser.getID();
		int ThreadSerialNo = parser.readInt();
		int FrameNumInStackTrace = parser.readInt();
		parser.write("HEAP_ROOT_JAVA_FRAME: ID: " + ObjectID + " ThreadSerialNumber: " + ThreadSerialNo +
					" Frame Number in Stack Trace: " + FrameNumInStackTrace);
		dataLen -= idSize + 8;
	}

	/*
	HEAP_ROOT_NATIVE_STACK
	ID object ID
	u4 thread serial number
	*/
	private static void processRootNativeStack() throws IOException {
		// TODO Auto-generated method stub
		long ObjectID = parser.getID();
		int ThreadSerialNo = parser.readInt();
		parser.write("HEAP_ROOT_NATIVE_STACK ID: " + ObjectID + " ThreadSerialNumber: " + ThreadSerialNo);
		dataLen -= idSize + 4;
	}

	/*
	HEAP_ROOT_STICKY_CLASS
	ID object ID
	*/
	private static void processRootStickyClass() throws IOException {
		// TODO Auto-generated method stub
		long ObjectID = parser.getID();
		parser.write("HEAP_ROOT_STICKY_CLASS: ID: " + ObjectID);
		dataLen -= idSize;
	}

	/*
	HEAP_ROOT_THREAD_BLOCK
	ID object ID
	u4 thread serial number
	*/
	private static void processRootThreadBlock() throws IOException {
		// TODO Auto-generated method stub
		long ObjectID = parser.getID();
		int ThreadSerialNo = parser.readInt();
		parser.write("HEAP_ROOT_NATIVE_STACK ID: " + ObjectID + " ThreadSerialNumber: " + ThreadSerialNo);
		dataLen -= idSize + 4;
	}

	/*
	HEAP_ROOT_MONITOR_USED
	ID object ID
	*/
	private static void processRootMonitorUsed() throws IOException {
		// TODO Auto-generated method stub
		long ObjectID = parser.getID();
		parser.write("HEAP_ROOT_STICKY_CLASS: ID: " + ObjectID);
		dataLen -= idSize;
	}

	/*
	HEAP_ROOT_THREAD_OBJECT
	ID thread object id
	u4 thread serial number
	u4 stack trace serial number
	*/
	private static void processRootThreadObject() throws IOException {
		// TODO Auto-generated method stub
		long ObjectID = parser.getID();
		int ThreadSerialNo = parser.readInt();
		int StackTraceSerialNo = parser.readInt();
		parser.write("HEAP_ROOT_THREAD_OBJECT: ID: " + ObjectID + " ThreadSerialNumber: " + ThreadSerialNo + 
					" StackTraceSerialNo: " + StackTraceSerialNo);
		dataLen -= idSize + 8;
	}

	/*
	HEAP_CLASS_DUMP
	ID class object ID
	u4 stack trace serial number
	ID super class object ID
	ID class loader object ID
	ID signers object ID
	ID protection domain object ID
	ID reserved
	ID reserved
	u4 instance size(in bytes)
	u2 size of constant pool and number of records that follow :
		u2 constant pool index
		u1 type of entry : (See Basic Type)
		value value of entry(u1, u2, u4, or u8 based on type of entry)

	u2 Number of static fields :
		ID static field name string ID
		u1 type of field : (See Basic Type)
		value: value of entry(u1, u2, u4, or u8 based on type of field)

	u2 Number of instance fields(not including super class's)
		ID field name string ID
		u1 type of field : (See Basic Type)
	*/
	private static void processClassDump() throws IOException {
		// TODO Auto-generated method stub
		long ObjectID = parser.getID();
		int StackTraceSerialNo = parser.readInt();
		long SuperClassObjectID = parser.getID();
		long ClassLoaderObjectID = parser.getID();
		long SignersObjectID = parser.getID();
		long ProtectionDomainObjectID = parser.getID();
		long res1 = parser.getID();
		long res2 = parser.getID();
		int instanceSize = parser.readInt();
		short ConstantPoolSize = parser.readShort();
		dataLen -= 7*idSize + 2*4 + 2;
		parser.write("HEAP_CLASS_DUMP: ID: " + ObjectID + " StackTraceSerialNo: " + StackTraceSerialNo + 
					" SuperClassObjectID: " + SuperClassObjectID + " ClassLoaderObjectID: " + ClassLoaderObjectID + 
					" SignersObjectID: " + SignersObjectID + " ProtectionDomainObjectID: " + ProtectionDomainObjectID);
		for(int i=0; i<ConstantPoolSize; ++i) {
			processConstantPoolRecord(i);
		}
		short StaticFields = parser.readShort();
		dataLen -= 2;
		for(int i=0; i<StaticFields; ++i) {
			processStaticFieldRecord(i);
		}
		short InstanceFields = parser.readShort();
		dataLen -= 2;
		for(int i=0; i<InstanceFields; ++i) {
			processInstanceFieldRecord(i);
		}
	}

	/*
	u2 constant pool index
	u1 type of entry : (See Basic Type)
	value value of entry(u1, u2, u4, or u8 based on type of entry)
	*/
	private static void processConstantPoolRecord(int i) throws IOException {
		// TODO Auto-generated method stub
		short ConstantPoolIndex = parser.readShort();
		parser.write("ConstantPool at Index: " + i + "CONSTANT_POOL_INDEX: " + ConstantPoolIndex);
		dataLen -=2;
		processBasicType(-1);
	}

	/*
	ID static field name string ID
	u1 type of field : (See Basic Type)
	value value of entry(u1, u2, u4, or u8 based on type of field)
	*/
	private static void processStaticFieldRecord(int i) throws IOException {
		// TODO Auto-generated method stub
		long StaticFieldName = parser.getID();
		parser.write("StaticRecord at Index: " + i + "STATIC_FIELD_NAME: " + StaticFieldName);
		dataLen -= idSize;
		processBasicType(-1);
	}

	/*
	BASIC TYPES
	2 object
	4 boolean
	5 char
	6 float
	7 double
	8 byte
	9 short
	10 int
	11 long
	*/
	private static void processBasicType(int knownType) throws IOException {
		// TODO Auto-generated method stub
		byte tagType;
		if(knownType == -1) {
			tagType = parser.readByte();
			--dataLen;
		}
		else
			tagType = (byte) knownType;
		parser.writeWonewLine("BASIC_TYPE: TagType: " + tagType + " value: ");
		switch(tagType) {
		case OBJECT: long ID = parser.getID();
					parser.write("ObjectID: " + ID); dataLen -= idSize; break;
		case BOOLEAN: 
		case BYTE: byte val = parser.readByte();
					parser.write("value: " + val);	dataLen -= 1; break;
		case CHAR:
		case SHORT: short val1 = parser.readShort();
					parser.write("value: " + val1); dataLen -=2; break;
		case INT: int val2 = parser.readInt();
					parser.write("value: "+val2); dataLen -= 4; break;
		case FLOAT: float val3 = parser.readFloat();
					parser.write("value: "+val3); dataLen -= 4; break;
		case DOUBLE: double val4 = parser.readDouble();
					parser.write("value: "+val4); dataLen -= 8; break;
		case LONG: long val5 = parser.readLong();
					parser.write("value: "+val5); dataLen -= 8; break;
		default: parser.write("WRONG_TAG_TYPE: " + tagType); System.exit(-1);
		}
	}

	/*
	ID field name string ID
	u1 type of field : (See Basic Type)
	*/
	private static void processInstanceFieldRecord(int i) throws IOException {
		// TODO Auto-generated method stub
		long FieldNameID = parser.getID();
		byte TagType = parser.readByte();
		parser.write("INSTANCE_FIELD_RECORD: ID: " + FieldNameID + " TagType: " + TagType);
		dataLen -= idSize + 1;
	}

	/*
	HEAP_INSTANCE_DUMP
	ID object ID
	u4 stack trace serial number
	ID class object ID
	u4 number of bytes that follow
		[value] * instance field values(this class, followed by super class, etc)
	*/
	private static void processInstanceDump() throws IOException {
		// TODO Auto-generated method stub
		long ObjectID = parser.getID();
		int StackTraceSerialNo = parser.readInt();
		long ClassObjID = parser.getID();
		int bytesFollow = parser.readInt();
		dataLen -= 2*idSize + 2*4;
		parser.write("HEAP_INSTANCE_DUMP: ObjectID: " + ObjectID + " StackTraceSerialNo: " + StackTraceSerialNo + 
					" ClassObjectID: " + ClassObjID + " BytesToFollow: " + bytesFollow);
		// TODO Traverse superclasses to get data of instance fields
		parser.skipNbytes(bytesFollow);
		dataLen -= bytesFollow;
	}

	/*
	HEAP_OBJECT_ARRAY_DUMP
	ID array object ID
	u4 stack trace serial number
	u4 number of elements
	ID array class object ID
	[ID] * elements
	*/
	private static void processObjectArrayDump() throws IOException {
		// TODO Auto-generated method stub
		long ArrayObjID = parser.getID();
		int StackTraceSerialNo = parser.readInt();
		int NumElem = parser.readInt();
		long ArrayClassObjID = parser.getID();
		parser.write("HEAP_OBJECT_ARRAY_DUMP ID: " + ArrayObjID + " StackTraceSerialNo: " + StackTraceSerialNo +
					" Number of elements: " + NumElem + " ArrayClassObjectID: " + ArrayClassObjID);
		dataLen -= 2*idSize + 2*4;
		for(int i=0; i<NumElem; ++i) {
			processBasicType(OBJECT);
		}
	}

	/*
	HEAP_PRIMITIVE_ARRAY_DUMP
	ID array object ID
	u4 stack trace serial number
	u4 number of elements
	u1 element type(See Basic Type)
	[u1] * elements(packed array)
	*/
	private static void processPrimitiveArrayDump() throws IOException {
		// TODO Auto-generated method stub
		long ArrayObjID = parser.getID();
		int StackTraceSerialNo = parser.readInt();
		int NumElem = parser.readInt();
		byte ElemType = parser.readByte();
		parser.write("HEAP_PRIMITIVE_ARRAY_DUMP ID: " + ArrayObjID + " StackTraceSerialNo: " + StackTraceSerialNo +
				" Number of elements: " + NumElem + " ArrayType: " + ElemType);
		dataLen -= idSize + 2*4 + 1;
		for(int i=0; i<NumElem; ++i) {
			processBasicType(ElemType);
		}
	}
}