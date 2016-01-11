# MedicalLSTM
TensorFlow application: LSTM with Word2Vec on a medical database.

# ToDo
* Remove the patientnumbers and days from the states to get more general events
* Represent the days as differences between the events -> Better representation of the problem BUT still keep the date also to find relations with certain time periods as winter
* Make the LSTM network
* Look into DeepLearning4J as it offers a lot more documentation and feels a lot more natural to program in. It also offers LSTM and Sequence2Vec!

# Experiments
* Check graph links between diseases after word2vec (like in paper) (with TSNE)
* Check  if preprocessing has effect on model with cross-validation
* Check how well word2vec works with complete new examples with cross-validation
* General accuracy of model of course

