import csv
from random import randint


def addRandomLabels():

    labels = ["Cured", "Cured after 1 year" "Dead", "Dead after 1 year"]

    input = open('../TestData/diabetes-input.csv', "r")
    reader = csv.reader(input)

    output = open('../TestData/diabetes-labeled.csv', "w")
    writer = csv.writer(output)

    dictionary = dict()

    patientColumn = 2

    rownum = 0
    for row in reader:

        if rownum == 0:
            row.append("label")

            writer.writerow(row)

        else:
            random = randint(0, len(labels)-1)
            row.append(labels[random])

            patientNumber = row[patientColumn]

            if patientNumber in dictionary:
                dictionary[patientNumber].append(row)
            else:
                dictionary[patientNumber] = [row]

        rownum += 1

    for key in dictionary.keys():
        writer.writerows(dictionary[key])

    input.close()
    output.close()


if __name__ == "__main__":
    addRandomLabels()

