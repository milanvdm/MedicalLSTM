package deepwalk.transformers.impl;

import lombok.NonNull;

import org.deeplearning4j.graph.api.IGraph;
import org.deeplearning4j.models.sequencevectors.sequence.Sequence;
import org.deeplearning4j.models.sequencevectors.sequence.SequenceElement;
import org.deeplearning4j.models.word2vec.Huffman;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import deepwalk.graph.walkers.GraphWalker;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * This class is used to build vocabulary and sequences out of graph, via GraphWalkers
 *
 * WORK IS IN PROGRESS, DO NOT USE
 * @author raver119@gmail.com
 */
public class GraphTransformer<T extends SequenceElement> implements Iterable<Sequence<T>> {
    protected IGraph<T, ?> sourceGraph;
    protected GraphWalker<T> walker;
    protected AtomicInteger counter = new AtomicInteger(0);
    protected boolean shuffle = true;
    protected VocabCache<T> vocabCache;

    protected static final Logger log = LoggerFactory.getLogger(GraphTransformer.class);

    protected GraphTransformer() {
        ;
    }

    /**
     * This method handles required initialization for GraphTransformer
     */
    protected void initialize() {
        log.info("Building Huffman tree for source graph...");
        int nVertices = sourceGraph.numVertices();
        //int[] degrees = new int[nVertices];
        //for( int i=0; i<nVertices; i++ )
           // degrees[i] = sourceGraph.getVertexDegree(i);
/*
        for (int y = 0; y < nVertices; y+= 20) {
            int[] copy = Arrays.copyOfRange(degrees, y, y+20);
            System.out.println("D: " + Arrays.toString(copy));
        }
*/
//        GraphHuffman huffman = new GraphHuffman(nVertices);
//        huffman.buildTree(degrees);

        log.info("Transferring Huffman tree info to nodes...");
        for (int i = 0; i < nVertices; i++) {
            T element = sourceGraph.getVertex(i).getValue();
            element.setElementFrequency(sourceGraph.getConnectedVertices(i).size());

            if (vocabCache != null) vocabCache.addToken(element);
        }

        if (vocabCache != null) {
            Huffman huffman = new Huffman(vocabCache.vocabWords());
            huffman.build();
            huffman.applyIndexes(vocabCache);
        }
    }


    @Override
    public Iterator<Sequence<T>> iterator() {
        this.counter.set(0);
        this.walker.reset(shuffle);
        return new Iterator<Sequence<T>>() {
            private GraphWalker<T> walker = GraphTransformer.this.walker;

            @Override
            public void remove() {
                throw new UnsupportedOperationException("This is not supported on read-only iterator");
            }

            @Override
            public boolean hasNext() {
                return walker.hasNext();
            }

            @Override
            public Sequence<T> next() {
                Sequence<T> sequence = walker.next();
                sequence.setSequenceId(counter.getAndIncrement());
                
                return sequence;
            }
        };
    }

    public static class Builder<T extends SequenceElement> {
        protected IGraph<T, ?> sourceGraph;
        protected GraphWalker<T> walker;
        protected boolean shuffle = true;
        protected VocabCache<T> vocabCache;

        public Builder(IGraph<T, ?> sourceGraph) {
            this.sourceGraph = sourceGraph;
        }


     

        public Builder<T> setGraphWalker(@NonNull GraphWalker<T> walker) {
            this.walker = walker;
            return this;
        }

        public Builder<T> setVocabCache(@NonNull VocabCache<T> vocabCache) {
            this.vocabCache = vocabCache;
            return this;
        }

        public Builder<T> shuffleOnReset(boolean reallyShuffle) {
            this.shuffle = reallyShuffle;
            return this;
        }

        public GraphTransformer<T> build() {
            GraphTransformer<T> transformer = new GraphTransformer<T>();
            transformer.sourceGraph = this.sourceGraph;
            transformer.shuffle = this.shuffle;
            transformer.vocabCache = this.vocabCache;

            if (this.walker == null)
                throw new IllegalStateException("Please provide GraphWalker instance.");
            else transformer.walker = this.walker;

            transformer.initialize();

            return transformer;
        }
    }
}
