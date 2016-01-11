package word2vec;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.deeplearning4j.models.sequencevectors.sequence.SequenceElement;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.NonNull;

public class StateCache<T extends SequenceElement> implements VocabCache<T> {


	private static final long serialVersionUID = 1L;

	// map for label->object dictionary
    private volatile Map<String, T> vocabulary = new ConcurrentHashMap<>();

    private volatile Map<Integer, T> idxMap = new ConcurrentHashMap<>();

    private AtomicLong totalWordCount = new AtomicLong(0);

    private Logger logger = LoggerFactory.getLogger(AbstractCache.class);

    /**
     * Deserialize vocabulary from specified path
     */
    @Override
    public void loadVocab() {
        // TODO: this method should be static and accept path
    }

    /**
     * Returns true, if number of elements in vocabulary > 0, false otherwise
     *
     * @return
     */
    @Override
    public boolean vocabExists() {
        return vocabulary.size() > 0;
    }

    /**
     * Serialize vocabulary to specified path
     *
     */
    @Override
    public void saveVocab() {
        // TODO: this method should be static and accept path
    }

    /**
     * Returns collection of labels available in this vocabulary
     *
     * @return
     */
    @Override
    public Collection<String> words() {
        return Collections.unmodifiableCollection(vocabulary.keySet());
    }

    /**
     * Increment frequency for specified label by 1
     *
     * @param word the word to increment the count for
     */
    @Override
    public void incrementWordCount(String word) {
        incrementWordCount(word, 1);
    }


    /**
     * Increment frequency for specified label by specified value
     *
     * @param word the word to increment the count for
     * @param increment the amount to increment by
     */
    @Override
    public void incrementWordCount(String word, int increment) {
        if (vocabulary.containsKey(word)) {
            vocabulary.get(word).increaseElementFrequency(increment);
            totalWordCount.addAndGet(increment);
        }

    }

    /**
     * Returns the SequenceElement's frequency over training corpus
     *
     * @param word the word to retrieve the occurrence frequency for
     * @return
     */
    @Override
    public int wordFrequency(String word) {
        if (vocabulary.containsKey(word))
            return (int) vocabulary.get(word).getElementFrequency();
        return 0;
    }

    /**
     * Checks, if specified label exists in vocabulary
     *
     * @param word the word to check for
     * @return
     */
    @Override
    public boolean containsWord(String word) {
        return vocabulary.containsKey(word);
    }

    /**
     * Checks, if specified element exists in vocabulary
     *
     * @param element
     * @return
     */
    public boolean containsElement(T element) {
        return vocabulary.values().contains(element);
    }

    /**
     * Returns the label of the element at specified Huffman index
     *
     * @param index the index of the word to get
     * @return
     */
    @Override
    public String wordAtIndex(int index) {
        if (idxMap.containsKey(index))
            return idxMap.get(index).getLabel();
        return null;
    }

    /**
     * Returns SequenceElement at specified index
     *
     * @param index
     * @return
     */
    @Override
    public T elementAtIndex(int index) {
        return idxMap.get(index);
    }

    /**
     * Returns Huffman index for specified label
     *
     * @param label the label to get index for
     * @return >=0 if label exists, -1 if Huffman tree wasn't built yet, -2 if specified label wasn't found
     */
    @Override
    public int indexOf(String label) {
        if (containsWord(label)) {
            return tokenFor(label).getIndex();
        } else return -2;
    }

    /**
     * Returns collection of SequenceElements stored in this vocabulary
     *
     * @return
     */
    @Override
    public Collection<T> vocabWords() {
        return vocabulary.values();
    }

    /**
     * Returns total number of elements observed
     *
     * @return
     */
    @Override
    public long totalWordOccurrences() {
        return totalWordCount.get();
    }

    /**
     * Returns SequenceElement for specified label
     *
     * @param label to fetch element for
     * @return
     */
    @Override
    public T wordFor(@NonNull String label) {
        return vocabulary.get(label);
    }

    /**
     * This method allows to insert specified label to specified Huffman tree position.
     * CAUTION: Never use this, unless you 100% sure what are you doing.
     *
     * @param index
     * @param label
     */
    @Override
    public void addWordToIndex(int index, String label) {
        if (index >= 0 && hasToken(label))
            idxMap.put(index, tokenFor(label));
    }

    @Override
    @Deprecated
    public void putVocabWord(String word) {
        if (!containsWord(word))
            throw new IllegalStateException("Specified label is not present in vocabulary");
    }

    /**
     * Returns number of elements in this vocabulary
     *
     * @return
     */
    @Override
    public int numWords() {
        return vocabulary.size();
    }

    /**
     * Not applicable
     */
    @Override
    public int docAppearedIn(String word) {
        return 0;
    }

    /**
     * Not applicable
     */
    @Override
    public void incrementDocCount(String word, int howMuch) {
        
    }

    /**
     * Not applicable
     */
    @Override
    public void setCountForDoc(String word, int count) {
        
    }

    /**
     * Not applicable
     */
    @Override
    public int totalNumberOfDocs() {
        return -1;
    }

    /**
     * Not applicable
     */
    @Override
    public void incrementTotalDocCount() {
        
    }

    /**
     * Not applicable
     */
    @Override
    public void incrementTotalDocCount(int by) {
       
    }

    /**
     * Returns collection of SequenceElements from this vocabulary. The same as vocabWords() method
     *
     * @return collection of SequenceElements
     */
    @Override
    public Collection<T> tokens() {
        return vocabWords();
    }

    /**
     * This method adds specified SequenceElement to vocabulary
     *
     * @param element the word to add
     */
    @Override
    public void addToken(T element) {
        if (!vocabulary.containsKey(element.getLabel())) {
            vocabulary.put(element.getLabel(), element);

   
        } else vocabulary.get(element.getLabel()).increaseElementFrequency((int) element.getElementFrequency());
        
        totalWordCount.addAndGet((long) element.getElementFrequency());
        
    }

    /**
     * Returns SequenceElement for specified label. The same as wordFor() method.
     *
     * @param label the label to get the token for
     * @return
     */
    @Override
    public T tokenFor(String label) {
        return wordFor(label);
    }

    /**
     * Checks, if specified label already exists in vocabulary. The same as containsWord() method.
     *
     * @param label the token to test
     * @return
     */
    @Override
    public boolean hasToken(String label) {
        return containsWord(label);
    }


    /**
     * This method imports all elements from VocabCache passed as argument
     * If element already exists,
     *
     * @param vocabCache
     */
    public void importVocabulary(@NonNull VocabCache<T> vocabCache) {
        for (T element: vocabCache.vocabWords()) {
            this.addToken(element);
        }
    }

    @Override
    public void updateWordsOccurencies() {
        totalWordCount.set(0);
        for (T element: vocabulary.values()) {
            long value = (long) element.getElementFrequency();

            if (value > 0) totalWordCount.addAndGet(value);
        }
        logger.info("Updated counter: ["+ totalWordCount.get()+"]");
    }

    @Override
    public void removeElement(String label) {
        if (vocabulary.containsKey(label)) {
            totalWordCount.getAndAdd((long) vocabulary.get(label).getElementFrequency() * -1);
            idxMap.remove(label);
            vocabulary.remove(label);
        } else throw new IllegalStateException("Can't get label: '" + label + "'");
    }

    @Override
    public void removeElement(T element) {
        removeElement(element.getLabel());
    }

	
}
