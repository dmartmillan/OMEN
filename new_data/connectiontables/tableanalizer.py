import pandas as pd
import sys 
import os


"""
Creates the file 'SummaryTable' with the top 10 hubs per network
"""

var_holder,tableinfo,tablename,i = {},[],[],0 #Varibles for iteration
filelist = [file for file in os.listdir(sys.path[0]) if file.endswith(".csv") ] #To only open .csv files
filelist = filelist[1:10] + [filelist[0]] #To correct that net10_ is first than net1_
for filename in filelist: #This part could be merged with the filelist to save one line, but for a correction in the order of files I did it separately
    var_holder['Connection_table' + str(i+1)] =  pd.read_csv(os.path.join(sys.path[0], filename))
    tablename.append(filename.replace(".csvconnectiontable.csv",""))
    tableinfo.append('Connection_table' + str(i+1)+" correspond to the file "+tablename[i])
    i += 1
 
#locals().update(var_holder) #To transform the dictionary keys into variables (usefull to test but the dictionaries are better to work with)
print("There are "+str(len(tableinfo))+" tables")
for x in range(len(tableinfo)):
    print(tableinfo[x])

summarytable = []
headers = ["Network","Genes (No. Connections)"]
for tablename in var_holder:
    gencon = ""
    for x in range(10):
        gencon += "%s(%d) "%(var_holder[tablename].iloc[x,0],var_holder[tablename].iloc[x,1])
    summarytable.append([tablename,gencon])

SummaryTable = pd.DataFrame(summarytable)
SummaryTable.columns = headers
Data_in_csv = SummaryTable.to_csv(encoding='utf-8',index=False)
Data_in_csv = Data_in_csv.replace("\r\n","\n")

with open(os.path.join(sys.path[0],"SummaryTable"), "w") as thefile:
    thefile.write(Data_in_csv)
