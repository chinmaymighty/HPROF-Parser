import java.lang.*;
import java.io.*;
import java.util.*;

public abstract class HeapProcess {
	//TODO: Process TAG_HEA_PDUMP and TAG_HEAP_SEGMENT
	
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
    public static final boolean DEBUG = true;
    
    
}