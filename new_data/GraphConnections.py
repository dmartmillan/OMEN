#!/usr/bin/env python3
import sys 
import csv
import os
import pandas as pd

# Functions
def GraphConnections(graphfile): 
    with open(os.path.join(sys.path[0], graphfile), newline='') as csvfile: #To load the data of the graph
        spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
        handable = [row for row in spamreader]

    gene_table = [["Gene","Number of connections","Connected genes"]]
    unique_genes = list(set([handable[x][0] for x in range(1,len(handable))] + [handable[y][1] for y in range(1,len(handable))])) #Creates a list of genes in both nodes, transformn into a set to keep unique values and then back to list
    unique_genes.sort()
    for gene in unique_genes:
        nconn = 0
        conngen = ""
        for row in range(1,len(handable)):
            if gene in handable[row]: #Check if the unique gene has a connection
                nconn += 1 #Count the connection
                conngen += "".join(handable[row][0:2]) + " "  #Store the connection (It also has the unique gene so need to be remove)
        gene_table.append([gene,nconn,conngen.replace(gene,"")])  #Transformn the count in to a str for operability and remove the main gene name in the connection
    
    GeneTable = pd.DataFrame(gene_table)
    GeneTable.columns = GeneTable.iloc[0] #Use the first row as header   
    GeneTable = GeneTable[1:] #Remove the 1st row
    GeneTable = GeneTable.sort_values(by="Number of connections",ascending=False) #Sort by number of connecions
    Data_in_csv = GeneTable.to_csv(encoding='utf-8',index=False)
    Data_in_csv = Data_in_csv.replace('"',"") #Eliminate triple " for better out put
    return (Data_in_csv.replace("\r\n","\n")) #Return the data frame to a csv and replace the double line with just one

#Main
if __name__ == "__main__": #This prevents a lauch when load it from another script
    if len(sys.argv) !=2: 
        sys.stderr.write("USAGE: %s uses a gene network as input" %(sys.argv[0]))
        sys.exit(1)
    
    with open(os.path.join(sys.path[0], sys.argv[1] + "connectiontable.csv"), "w") as thefile:
        thefile.write(GraphConnections(sys.argv[1]))
        print("The file " + sys.argv[1] + "connectiontable.csv was created on location: "+ sys.path[0])
