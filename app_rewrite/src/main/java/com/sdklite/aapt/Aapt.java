package com.sdklite.aapt;

import static com.sdklite.aapt.Internal.find;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.sdklite.aapt.Internal.Filter;

/**
 * This class represents the Android Assets Packaging Tool
 * 
 * @author johnsonlee
 *
 */
public class Aapt {

    public static final String CLASSES_DEX = "classes.dex";

    public static final String RESOURCES_ARSC = "resources.arsc";

    public static final String ANDROID_MANIFEST_XML = "AndroidManifest.xml";

    /**
     * Generate R.java
     * 
     * @param os
     *            The output stream
     * @param pkg
     *            The package name
     * @param symbols
     *            The resource symbols
     * @throws IOException
     */
    public static void generateR(final OutputStream os, final String pkg, final Symbols symbols) throws IOException {
        final Map<String, List<Symbols.Entry>> types = new TreeMap<String, List<Symbols.Entry>>();
        for (final Symbols.Entry entry : symbols.entries.values()) {
            final List<Symbols.Entry> entries = types.containsKey(entry.type.name) ? types.get(entry.type.name) : new ArrayList<Symbols.Entry>();
            entries.add(entry);
            types.put(entry.type.name, entries);
        }

        final PrintWriter out = new PrintWriter(os, true);
        out.printf("/* AUTO-GENERATED FILE.  DO NOT MODIFY.").println();
        out.printf(" *").println();
        out.printf(" * This class was automatically generated by the").println();
        out.printf(" * aapt tool from the resource data it found.  It").println();
        out.printf(" * should not be modified by hand.").println();
        out.printf(" */").println();
        out.printf("package %s;\r\n", pkg).println();
        out.printf("public final class R {").println();

        for (final Map.Entry<String, List<Symbols.Entry>> pair : types.entrySet()) {
            out.printf("    public static final class %s {", pair.getKey()).println();

            for (final Symbols.Entry entry : pair.getValue()) {
                if (entry instanceof Symbols.Styleable) {
                    out.printf("        public static %s %s = { ", entry.vtype, entry.name);

                    final Symbols.Styleable styleable = (Symbols.Styleable) entry;
                    for (int i = 0, n = styleable.values.size(); i < n; i++) {
                        if (i > 0) {
                            out.print(", ");
                        }

                        out.printf("0x%08x", styleable.values.get(i));
                    }

                    out.printf(" };").println();
                } else {
                    out.printf("        public static %s %s = 0x%08x;", entry.vtype, entry.name, entry.value).println();
                }
            }

            out.printf("    }").println();
        }

        out.printf("}").println();
        out.flush();
    }

    /**
     * Generate R.java
     * 
     * @param r
     *            The R.java file
     * @param pkg
     *            The package name
     * @param symbols
     *            The resource symbols
     * @throws IOException
     */
    public static void generateR(final File r, final String pkg, final Symbols symbols) throws IOException {
        if (!r.getParentFile().exists()) {
            r.getParentFile().mkdirs();
        }

        if (!r.exists()) {
            r.createNewFile();
        }

        final FileOutputStream out = new FileOutputStream(r);

        try {
            generateR(out, pkg, symbols);
        } finally {
            out.close();
        }
    }

    /**
     * Generate R.java
     * 
     * @param r
     *            The R.java file
     * @param pkg
     *            The package name
     * @param symbols
     *            The resource symbols
     * @throws IOException
     */
    public static void generateR(final String r, final String pkg, final Symbols symbols) throws IOException {
        generateR(new File(r), pkg, symbols);
    }

    /**
     * Sets the debuggable attribute of application element to true
     * 
     * @param manifest
     *            The AndroidManifest.xml
     * @return true if successful
     * @throws IOException
     *             if error occurred
     * @throws AaptException
     *             if parsing error
     */
    public static boolean setApplicationDebuggable(final String manifest) throws IOException {
        return setApplicationDebuggable(new File(manifest));
    }

    /**
     * Sets the debuggable attribute of application element to true
     * 
     * @param manifest
     *            The AndroidManifest.xml
     * @return true if successful
     * @throws IOException
     *             if error occurred
     * @throws AaptException
     *             if parsing error
     */
    public static boolean setApplicationDebuggable(final File manifest) throws IOException {
        final AssetEditor parser = new AssetEditor(manifest);

        StringPool pool = null;

        try {
            final ChunkHeader header = parser.parseChunkHeader();
            if (ChunkType.XML != header.type) {
                throw new AaptException(String.format("XML chunk was expected, but 0x%04x found", header.type));
            }

            while (parser.hasRemaining()) {
                final ChunkHeader chunk = parser.parseChunkHeader();

                switch (chunk.type) {
                case ChunkType.STRING_POOL:
                    parser.seek(parser.tell() - ChunkHeader.MIN_HEADER_SIZE);
                    pool = parser.parseStringPool();
                    break;
                case ChunkType.XML_CDATA:
                    parser.skip(chunk.size - ChunkHeader.MIN_HEADER_SIZE);
                    break;
                case ChunkType.XML_END_ELEMENT:
                    parser.skip(chunk.size - ChunkHeader.MIN_HEADER_SIZE);
                    break;
                case ChunkType.XML_END_NAMESPACE:
                    parser.skip(chunk.size - ChunkHeader.MIN_HEADER_SIZE);
                    break;
                case ChunkType.XML_RESOURCE_MAP:
                    parser.skip(chunk.size - ChunkHeader.MIN_HEADER_SIZE);
                    break;
                case ChunkType.XML_START_ELEMENT: {
                    final long p = parser.tell();

                    parser.skip(4); // lineNumber
                    parser.skip(4); // commentIndex
                    parser.skip(4); // namespaceUri

                    if ("application".equals(pool.getStringAt(parser.readInt()))) {
                        parser.skip(2); // attributeStart
                        parser.skip(2); // attributeSize

                        final short attributeCount = parser.readShort();

                        parser.skip(2); // idIndex
                        parser.skip(2); // classIndex
                        parser.skip(2); // styleIndex

                        for (int i = 0; i < attributeCount; i++) {
                            parser.skip(4); // namespace

                            if ("debuggable".equals(pool.getStringAt(parser.readInt()))) {
                                parser.skip(4); // rawValue
                                parser.skip(2); // typedValue.size
                                parser.skip(1); // typedValue.res0
                                parser.skip(1); // typedValue.dataType
                                parser.writeInt(0xffffffff); // true
                                return true;
                            } else {
                                parser.skip(4); // rawValue
                                parser.skip(8); // typedValue
                            }
                        }
                    }

                    parser.seek(p - ChunkHeader.MIN_HEADER_SIZE + chunk.size);
                    break;
                }
                case ChunkType.XML_START_NAMESPACE:
                    parser.skip(chunk.size - ChunkHeader.MIN_HEADER_SIZE);
                    break;
                default:
                    throw new AaptException(String.format("Unexpected chunk type 0x%04x", chunk.type));
                }
            }
        } finally {
            parser.close();
        }

        return false;
    }

    private final File file;

    private final Revision buildToolRevision;

    /**
     * Instantialize with apk file or unzipped apk folder and build tool revision
     * 
     * @param file
     *            The apk file or unzipped apk folder
     * @param buildToolRevision
     *            The Android build tool revision
     */
    public Aapt(final File file, final String buildToolRevision) {
        this(file, Revision.parseRevision(buildToolRevision));
    }

    /**
     * Instantialize with apk file or unzipped apk folder and build tool revision
     * 
     * @param file
     *            The apk file or unzipped apk folder
     * @param buildToolRevision
     *            The Android build tool revision
     */
    public Aapt(final File file, final Revision buildToolRevision) {
        this.file = file;
        this.buildToolRevision = buildToolRevision;
    }

    /**
     * Returns the apk file or unzipped folder
     */
    public File getFile() {
        return this.file;
    }

    /**
     * Returns the Android build tool revision
     */
    public Revision getBuildToolRevision() {
        return this.buildToolRevision;
    }

    /**
     * Delete the resource files related to the specified symbols
     * 
     * @param symbols
     *            The resource symbols
     * @return deleted resource keys
     */
    public Set<String> deleteResources(final Symbols symbols) {
        final Set<String> resources = new HashSet<String>();
        final File resDir = new File(this.file, "res");
        final File[] typeDirs = resDir.listFiles();

        if (null != typeDirs) {
            for (final File typeDir : typeDirs) {
                final File[] entryFiles = typeDir.listFiles();
                if (null == entryFiles || entryFiles.length <= 0) {
                    typeDir.delete();
                    continue;
                }

                final Symbols.Entry entry = find(symbols.entries.values(), new Filter<Symbols.Entry>() {
                    @Override
                    public boolean accept(final Symbols.Entry it) {
                        return typeDir.getName().startsWith(it.type.name);
                    }
                });

                if (null == entry) {
                    for (final File entryFile : entryFiles) {
                        resources.add(String.format("res%s%s%s%s", File.separator, typeDir.getName(), File.separator, entryFile.getName()));
                        entryFile.delete();
                    }
                } else {
                    for (final File entryFile : entryFiles) {
                        final Symbols.Entry resEntry = find(symbols.entries.values(), new Filter<Symbols.Entry>() {
                            @Override
                            public boolean accept(final Symbols.Entry it) {
                                return entryFile.getName().startsWith(it.name + ".");
                            }
                        });

                        if (null != resEntry) {
                            resources.add(String.format("res%s%s%s%s", File.separator, typeDir.getName(), File.separator, entryFile.getName()));
                        }

                        entryFile.delete();
                    }
                }

                typeDir.delete(); // it shall be succeeded if it's empty
            }
        }

        return resources;
    }

}
