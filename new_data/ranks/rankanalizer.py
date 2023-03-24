import pandas as pd
import re
import sys 
import os

var_holder,tableinfo,tablename,i = {},[],[],0 #Varibles for iteration
filelist = [file for file in os.listdir(sys.path[0]) if file.endswith(".desc") ] #To only open .desc files
for filename in filelist: #This part could be merged with the filelist to save one line, but for a correction in the order of files I did it separately
    var_holder[filename.replace(".desc","")] =  pd.read_csv(os.path.join(sys.path[0], filename),sep="\t",header=None)
references = pd.read_csv(os.path.join(sys.path[0], "reference.txt"),sep="\t")

#locals().update(var_holder) #To transform the dictionary keys into variables (usefull to test but the dictionaries are better to work with)

for name in var_holder:
    file = var_holder[name].iloc[0:100,0:1] #Double slice to store it as a data frame
    genes = var_holder[name].iloc[0:100,0] #One slice and specify a column so will be a list and not a data frame
    file.columns = ["Gene"] #Add a name to the column in the data frame
    file["Rank"] = [i+1 for i in range(100)] #Create the rank column
    matched = references[references["Gene"].isin(genes)] #.isin uses a list
    ranked = file[file["Gene"].isin(matched["Gene"])] 
    ranked.set_index("Gene", inplace = True) #Make the Gene column as index to find the genes with easy
    Rank = [ranked.loc[gene,"Rank"] for gene in matched["Gene"]] #Get the exact value need to specify row and col
    matched.insert(0,"Rank",Rank) #Insert the value in column
    matched = matched.sort_values(by="Rank")
    Output = matched.to_csv(sep="\t", index=False)

    with open(os.path.join(sys.path[0], "summary", name + "summary.csv"), "w") as thefile:
        thefile.write(Output)

