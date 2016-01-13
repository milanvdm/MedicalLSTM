package datahandler.lstm;


import org.apache.commons.io.IOUtils;
import org.canova.api.io.data.Text;
import org.canova.api.records.reader.SequenceRecordReader;
import org.canova.api.records.reader.impl.FileRecordReader;
import org.canova.api.writable.Writable;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * CSV Sequence Record Reader
 * This reader is intended to read sequences of data in CSV format, where
 * each sequence is defined in its own file (and there are multiple files)
 * Each line in the file represents one time step
 */
public class SequenceReader extends FileRecordReader implements SequenceRecordReader {
    
	private static final long serialVersionUID = 2566782461516037172L;
	
	private int skipNumLines = 0;
    private String delimiter = ",";

    public SequenceReader() {
        this(0, ";");
    }

    public SequenceReader(int skipNumLines) {
        this(skipNumLines, ",");
    }

    public SequenceReader(int skipNumLines, String delimiter) {
        this.skipNumLines = skipNumLines;
        this.delimiter = delimiter;
    }

    @Override
    public Collection<Collection<Writable>> sequenceRecord() {
        File next = iter.next();

        Iterator<String> lineIter;
        try {
            lineIter = IOUtils.lineIterator(new InputStreamReader(new FileInputStream(next)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (skipNumLines > 0) {
            int count = 0;
            while (count++ < skipNumLines && lineIter.hasNext()) lineIter.next();
        }

        Collection<Collection<Writable>> out = new ArrayList<>();
        while (lineIter.hasNext()) {
            String line = lineIter.next();
            String[] split = line.split(delimiter);
            ArrayList<Writable> list = new ArrayList<>();
            for (String s : split) list.add(new Text(s));
            out.add(list);
        }

        return out;
    }

    
    public Collection<Collection<Writable>> sequenceRecord(URI uri, DataInputStream dataInputStream) throws IOException {
        Iterator<String> lineIter = IOUtils.lineIterator(new InputStreamReader(dataInputStream));
        if (skipNumLines > 0) {
            int count = 0;
            while (count++ < skipNumLines && lineIter.hasNext()) lineIter.next();
        }

        Collection<Collection<Writable>> out = new ArrayList<>();
        while (lineIter.hasNext()) {
            String line = lineIter.next();
            String[] split = line.split(delimiter);
            ArrayList<Writable> list = new ArrayList<>();
            for (String s : split) list.add(new Text(s));
            out.add(list);
        }

        return out;
    }


}