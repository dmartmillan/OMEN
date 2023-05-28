#!/usr/bin/env python3
import sys 
import csv
import os

def AggressiveSeparator(patientfile,lymphcol="ajcc_pathologic_n",naggre="N0",aggre="N1"): 
    with open(os.path.join(sys.path[0], patientfile), newline='') as csvfile: #To load the data of the graph
        spamreader = csv.reader(csvfile, delimiter='\t', quotechar='|')
        handable = [row for row in spamreader]
        indx,naggrefile,aggrefile = 0,[handable[0]],[handable[0]]
        for rowi in handable[0]:
            if rowi == lymphcol:
                for row in handable:
                    if row[indx] == naggre: naggrefile.append(row)
                    if row[indx] == aggre: aggrefile.append(row)
            indx +=1

    with open(os.path.join(sys.path[0], sys.argv[1]+"non-aggresive.csv"),"w") as nfile :
        csv_writer = csv.writer(nfile)
        csv_writer.writerows(naggrefile)

    with open(os.path.join(sys.path[0], sys.argv[1]+"aggresive.csv"),"w") as nfile :
        csv_writer = csv.writer(nfile)
        csv_writer.writerows(aggrefile)
    
    return print("Data set split !!!")

if __name__ == "__main__": #This prevents a lauch when load it from another script
    if len(sys.argv) == 1: 
        sys.stderr.write("USAGE: %s uses a patient anottation file to separate patients from non-aggresive and aggressive cancer type in base of the lymph nodes status (file, name of the col with node status, status for non-aggressive, status for aggressive)" %(sys.argv[0]))
        sys.exit(1)
    if len(sys.argv) == 2:
        AggressiveSeparator(sys.argv[1]) 
    else:
        AggressiveSeparator(sys.argv[1],sys.argv[2],sys.argv[3],sys.argv[4]) 
    print("The file " + sys.argv[1] + "non-aggresive.csv was created on location: "+ sys.path[0])
    print("The file " + sys.argv[1] + "aggresive.csv was created on location: "+ sys.path[0])