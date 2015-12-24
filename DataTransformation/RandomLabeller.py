import csv
from random import randint


labels = ["Cured", "Dead", "Dead after 1 year"]

input = open('../TestData/diabetes-input.csv', "r")
reader = csv.reader(input)

output = open('../TestData/diabetes-labeled.csv', "a")
writer = csv.writer(output)

writeBuffer = []
bufferCounter = 0
bufferMax = 250


def main():

    rownum = 0
    for row in reader:

        if bufferCounter > bufferMax:
            writer.writerows(writeBuffer)
            bufferCounter = 0
            writeBuffer = []

        if rownum == 0:
            row.append("label")
            writeBuffer.append(row)
            writer.writerows(writeBuffer)
            writeBuffer = []
        else:
            random = randint(0, labels.len())
            row.append(labels[random])
            writeBuffer.append(row)
            bufferCounter += 1

        rownum += 1

    input.close()
    output.close()


if __name__ == "__main__":
    main()

