import numpy as np
import csv

from sklearn.cluster import DBSCAN
from sklearn.neighbors import KDTree
from scipy.spatial.distance import cdist
from sklearn.preprocessing import StandardScaler

import matplotlib.pyplot as plt

##############################################################################
# CAN RUN OUT OF MEMORY FOR VERY LARGE DATASETS
##############################################################################

def findEps():
    # Get the data from the csv
    input = open('../TestData/diabetes-sequences.csv', "r")
    reader = csv.reader(input)

    data = []

    for row in reader:
        data.append(row)

    input.close()

    scaler = StandardScaler()
    X = scaler.fit_transform(data)

    tree = KDTree(X)
    dist, ind = tree.query(X, k=10)

    distk = [row[2] for row in dist]

    distk.sort()

    print len(distk)

    plt.plot(range(0,len(X)), distk)
    plt.show()



def cluster():
    # Get the data from the csv
    input = open('../TestData/diabetes-sequences.csv', "r")
    reader = csv.reader(input)

    batch = []
    batchSize = 1000

    dbscan = DBSCAN(eps=1.5, min_samples=10) #doesn't have a partialfit method so maybe MiniBatchKMeans is better suited for very large DB's

    amount = 0
    for row in reader:
        batch.append(row)

    input.close()

    scaler = StandardScaler()

    X = scaler.fit_transform(batch)

    ##############################################################################
    # Compute DBSCAN
    result = dbscan.fit(X)
    core_samples_mask = np.zeros_like(result.labels_, dtype=bool)
    core_samples_mask[result.core_sample_indices_] = True
    labels = result.labels_

    # Number of clusters in labels, ignoring noise if present.
    n_clusters_ = len(set(labels)) - (1 if -1 in labels else 0)

    print('Estimated number of clusters: %d' % n_clusters_)
    print('Amount of core samples: ' + str(len(result.core_sample_indices_)))

    X = scaler.inverse_transform(X)
    unique_labels = set(labels)
    coreSamples = []
    for k in unique_labels:

        class_member_mask = (labels == k)

        coreSamplesCluster = X[class_member_mask & core_samples_mask]
        coreSamples.insert(k,coreSamplesCluster)

    return coreSamples, X, labels

def toTrainingData():

    print "STARTING CLUSTERING"

    coreSamples, result, labels = cluster()

    print "FINISHED CLUSTERING"

    print "STARTING APROX"

    output = open('../TestData/diabetes-trainingdata.txt', "w")

    patientColumn = 2

    sequenceBuffer = []
    previousPatientNumber = 1

    stateNb = 0
    for row in result:
        currentPatientNumber = row[patientColumn]

        if str(currentPatientNumber) != str(previousPatientNumber):
            previousPatientNumber = currentPatientNumber

            writeSequence(sequenceBuffer, output)

            sequenceBuffer = []


        if labels[stateNb] == -1:
            sequenceBuffer.append(row)

        elif len(coreSamples[labels[stateNb]]) == 0:
            sequenceBuffer.append(row)

        else:
            distances = cdist([row], coreSamples[labels[stateNb]])
            smallestDistanceIndex = np.nonzero(distances == min(distances))[0][0]
            aprox = coreSamples[labels[stateNb]][smallestDistanceIndex]
            sequenceBuffer.append(aprox)

        stateNb += 1

    output.close()

    print "FINISHED APROX"


def writeSequence(sequence, output):
    skipGramWindowSize = 5

    beforePadding = "START"
    betweenPadding = "BETWEEN"
    afterPadding = "END"

    for i in range(1, skipGramWindowSize):
        output.write(beforePadding + " ")
    output.write(beforePadding)

    for state in sequence:
        output.write(" " + "[" + ','.join([str(x) for x in state]) + "]")

    output.write(" " + afterPadding)
    for i in range(1, skipGramWindowSize):
        output.write(" " + afterPadding)

    output.write(" " + betweenPadding)
    for i in range(1, skipGramWindowSize):
        output.write(" " + betweenPadding)
    output.write(" ")


if __name__ == "__main__":
    findEps()
    #toTrainingData()

