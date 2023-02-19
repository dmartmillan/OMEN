#!/usr/bin/env python3
import sys 
import csv
import os

# Functions
def GraphConnections(graphfile): 
    with open(os.path.join(sys.path[0], graphfile), newline='') as csvfile: #To load the data of the graph
        spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
        handable = [row for row in spamreader]
    gene_table = "Gene, Number of connections, Connected genes \n"

    unique_genes = list(set([handable[x][0] for x in range(1,len(handable))] + [handable[y][1] for y in range(1,len(handable))])) #Creates a list of all genes, transformn into a set to keep unique values and then back to list
    unique_genes.sort()
    for gene in unique_genes:
        nconn = 0
        conngen = ""
        for n1 in range(1,len(handable)):
            if gene in handable[n1]: #Check if the unique gene has a connection
                nconn += 1 #Count the connection
                conngen += "".join(handable[n1]) + " "  #Store the connection (It also has the unique gene so need to be remove)
        gene_table += ",".join([gene,str(nconn),conngen.replace(gene,"")]) + "\n" #Transformn the count in to a str for operability and remove the main gene name in the connection

    with open(os.path.join(sys.path[0], sys.argv[1] + "connectiontable"), "x") as newfile:
        newfile.write(gene_table)
        print("The file " + sys.argv[1] + "connectiontable was created on location: "+ sys.path[0])

    return (gene_table)

#Main
if __name__ == "__main__": #This prevents a lauch when load it from another script
    if len(sys.argv) !=2: 
        sys.stderr.write("USAGE: %s uses a gene network as input" %(sys.argv[0]))
        sys.exit(1)
    GraphConnections(sys.argv[1])
