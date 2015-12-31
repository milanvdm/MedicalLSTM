import csv


def addPaddingForSkipGram():

    skipGramWindowSize = 5

    beforePadding = [["START"]]*skipGramWindowSize
    afterPadding = [["END"]]*skipGramWindowSize

    input = open('../TestData/diabetes-labeled.csv', "r")
    reader = csv.reader(input)

    output = open('../TestData/diabetes-labeled-skipgram.csv', "w")
    writer = csv.writer(output)

    patientColumn = 2

    headers = reader.next()
    writer.writerow(headers)
    writer.writerows(beforePadding)

    previousPatientNumber = 344
    for row in reader:

        currentPatientNumber = row[patientColumn]

        if str(currentPatientNumber) != str(previousPatientNumber):
            previousPatientNumber = currentPatientNumber

            writer.writerows(afterPadding)
            writer.writerows(beforePadding)


        writer.writerow(row)


    writer.writerows(afterPadding)
    input.close()
    output.close()


if __name__ == "__main__":
    addPaddingForSkipGram()