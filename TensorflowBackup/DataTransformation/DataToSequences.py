import csv



def sortCsv():

    input = open('../TestData/diabetes-input.csv', "r")
    reader = csv.reader(input)
    csvList = list(reader)
    sortedList = sorted(csvList, key = lambda i: (float(i[2])))

    output = open('../TestData/diabetes-sequences.csv', "w")
    writer = csv.writer(output)

    writer.writerows(sortedList)


if __name__ == "__main__":
    sortCsv()