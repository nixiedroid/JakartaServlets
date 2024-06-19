package com.nixiedroid.jakarta.rest.loader;

import java.util.function.Function;

public final class ClassParser implements Function<byte[], ClassParser.RawInfo> {
    //https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html
    private static final byte CONSTANT_Class = 7;
    private static final byte CONSTANT_Fieldref = 9;
    private static final byte CONSTANT_Methodref = 10;
    private static final byte CONSTANT_InterfaceMethodref = 11;
    private static final byte CONSTANT_String = 8;
    private static final byte CONSTANT_Integer = 3;
    private static final byte CONSTANT_Float = 4;
    private static final byte CONSTANT_Long = 5;
    private static final byte CONSTANT_Double = 6;
    private static final byte CONSTANT_NameAndType = 12;
    private static final byte CONSTANT_Utf8 = 1;
    private static final byte CONSTANT_MethodHandle = 15;
    private static final byte CONSTANT_MethodType = 16;
    private static final byte CONSTANT_Dynamic = 17;
    private static final byte CONSTANT_InvokeDynamic = 18;
    private static final byte CONSTANT_Module = 19;
    private static final byte CONSTANT_Package = 20;
    private static final int SINGLE = 0x80;
    private static final int DOUBLE = 0xE0;
    public static final int ACC_PUBLIC = 0x0001;	//Declared public; may be accessed from outside its package.
    public static final int ACC_FINAL = 0x0010;	//Declared final; no subclasses allowed.
    public static final int ACC_SUPER = 0x0020;	//Treat superclass methods specially when invoked by the invokespecial instruction.
    public static final int ACC_INTERFACE = 0x0200;	//Is an interface, not a class.
    public static final int ACC_ABSTRACT = 0x0400;	//Declared abstract; must not be instantiated.
    public static final int ACC_SYNTHETIC = 0x1000;	//Declared synthetic; not present in the source code.
    public static final int ACC_ANNOTATION = 0x2000;	//Declared as an annotation interface.
    public static final int ACC_ENUM = 0x4000;	//Declared as an enum class.
    public static final int ACC_MODULE = 0x8000;	//Is a module, not a class or interface.



    private ClassParser() {
    }
    public static class RawInfo {
        int modifiers;
        String name;
        String superClassName;
        String[] interfaceNames;

        private RawInfo(int modifiers, String rawName, String superClassRawName, String[] implementedInterfaceRawNames) {
            this.modifiers = modifiers;
            this.name = rawName;
            this.superClassName = superClassRawName;
            this.interfaceNames = implementedInterfaceRawNames;
        }

        public int getModifiers() {
            return modifiers;
        }

        public String getName() {
            return name;
        }

        public String getSuperClassName() {
            return superClassName;
        }

        public String[] getInterfaceNames() {
            return interfaceNames;
        }

    }


    public static RawInfo retrieveInfo(
            final byte[] classBytes
    ) {
        int classFileOffset = 0;
        int constantPoolCount = getUint16BE(classBytes, classFileOffset + 8);
        int[] cpInfoOffsets = new int[constantPoolCount];
        String[] constantUtf8Values = new String[constantPoolCount];
        int cpInfoIdx = 1;
        int cpInfoOffset = classFileOffset + 10;
        int maxStrLen = 0;
        while (cpInfoIdx < constantPoolCount) {
            cpInfoOffsets[cpInfoIdx++] = cpInfoOffset + 1;
            int cpInfoSize;
            byte cpVal = classBytes[cpInfoOffset];
            switch (cpVal) {
                case CONSTANT_Integer:
                case CONSTANT_Float:
                case CONSTANT_Fieldref:
                case CONSTANT_Methodref:
                case CONSTANT_InterfaceMethodref:
                case CONSTANT_NameAndType:
                case CONSTANT_Dynamic:
                case CONSTANT_InvokeDynamic:
                    cpInfoSize = 5;
                    break;
                case CONSTANT_Long:
                case CONSTANT_Double:
                    cpInfoSize = 9;
                    cpInfoIdx++;
                    break;
                case CONSTANT_Utf8:
                    cpInfoSize = 3 + getUint16BE(classBytes, cpInfoOffset + 1);
                    if (cpInfoSize > maxStrLen) maxStrLen = cpInfoSize;
                    break;
                case CONSTANT_MethodHandle:
                    cpInfoSize = 4;
                    break;
                case CONSTANT_Class:
                case CONSTANT_String:
                case CONSTANT_MethodType:
                case CONSTANT_Module:
                case CONSTANT_Package:
                    cpInfoSize = 3;
                    break;
                default:
                    throw new IllegalArgumentException();
            }



            cpInfoOffset += cpInfoSize;
        }
        int maxStringLength = maxStrLen;
        int header = cpInfoOffset;
        int modifiers = getUint16BE(classBytes, header);
        if ((modifiers & ACC_MODULE) == 0) {
            String rawName = readUTF8(
                    classBytes,
                    cpInfoOffsets[getUint16BE(classBytes, header + 2)],
                    new char[maxStringLength],
                    constantUtf8Values,
                    cpInfoOffsets
            );
            String superName = readUTF8(
                    classBytes,
                    cpInfoOffsets[getUint16BE(classBytes, header + 4)],
                    new char[maxStringLength],
                    constantUtf8Values,
                    cpInfoOffsets
            );
            String[] ifNames = getInterfaceNames(classBytes, header, maxStringLength, constantUtf8Values, cpInfoOffsets);
            return new RawInfo(modifiers,rawName,superName,ifNames);
        }
        return null;
    }

    private static String[] getInterfaceNames(
            byte[] classBytes,
            int header,
            int maxStringLength,
            String[] constantUtf8Values,
            int[] cpInfoOffsets
    ) {
        int currentOffset = header + 6;
        int interfacesCount = getUint16BE(classBytes, currentOffset);
        String[] interfaces = new String[interfacesCount];
        if (interfacesCount > 0) {
            char[] charBuffer = new char[maxStringLength];
            for (int i = 0; i < interfacesCount; ++i) {
                currentOffset += 2;
                interfaces[i] = readUTF8(classBytes, cpInfoOffsets[getUint16BE(classBytes, currentOffset)],
                        charBuffer, constantUtf8Values, cpInfoOffsets);
            }
        }
        return interfaces;
    }

    private static String readUTF8(
            byte[] classBytes,
            final int offset,
            final char[] charBuffer,
            String[] constantUtf8Values,
            int[] cpInfoOffsets
    ) {
        int constantPoolEntryIndex = getUint16BE(classBytes, offset);
        if (offset == 0 || constantPoolEntryIndex == 0) {
            return null;
        }
        return readUtf(classBytes, constantPoolEntryIndex, charBuffer, constantUtf8Values, cpInfoOffsets);
    }

    private static String readUtf(
            byte[] classBytes,
            final int constantPoolEntryIndex,
            final char[] charBuffer,
            String[] constantUtf8Values,
            int[] cpInfoOffsets
    ) {
        String value = constantUtf8Values[constantPoolEntryIndex];
        if (value != null) {
            return value;
        }
        int cpInfoOffset = cpInfoOffsets[constantPoolEntryIndex];
        return constantUtf8Values[constantPoolEntryIndex] = readUtf(classBytes, cpInfoOffset + 2, getUint16BE(classBytes, cpInfoOffset),
                charBuffer);
    }

    private static int getUint16BE(
            byte[] classBytes,
            final int offset
    ) {
        return ((classBytes[offset] & 0xFF) << 8) | (classBytes[offset + 1] & 0xFF);
    }

    private static String readUtf(byte[] classBytes, final int utfOffset, final int utfLength, final char[] charBuffer) {
        int currentOffset = utfOffset;
        int endOffset = currentOffset + utfLength;
        int strLength = 0;
        while (currentOffset < endOffset) {
            int currentByte = classBytes[currentOffset++];
            if ((currentByte & 0x80) == 0) {
                charBuffer[strLength++] = (char) (currentByte & 0x7F);
            } else if ((currentByte & 0xE0) == 0xC0) {
                charBuffer[strLength++] = (char) (((currentByte & 0x1F) << 6) + (classBytes[currentOffset++] & 0x3F));
            } else {
                charBuffer[strLength++] = (char) (((currentByte & 0xF) << 12)
                        + ((classBytes[currentOffset++] & 0x3F) << 6) + (classBytes[currentOffset++] & 0x3F));
            }
        }
        return new String(charBuffer, 0, strLength);
    }

    @Override
    public RawInfo apply(byte[] bytes) {
        return retrieveInfo(bytes);
    }
}
