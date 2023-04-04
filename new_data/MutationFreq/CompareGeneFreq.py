import pandas as pd
import os
import sys

#To compare the different gene freqs, Read all the csv files on the folder so be cautius

var_holder = {} #Varibles for iteration
filelist = [file for file in os.listdir(sys.path[0]) if file.endswith(".csv") ] #To only open .csv files
for filename in filelist: #This part could be merged with the filelist to save one line, but for a correction in the order of files I did it separately
    filetitle = filename.replace(".csv","")
    var_holder[filetitle] =  pd.read_csv(os.path.join(sys.path[0], filename),sep="\t")

#Filters to identiphy the sets:
#.startswith() #net1,net8,TCGA
#.endswith() #Freq,Corrected
# in  #mafMuta,mafnon-aggr,mafaggr
Data0 = [x for x in var_holder]
Nrows = 24

#Comparations you want to made:
#For the non corrected data set (.endswith("Freq") ) The wholes (in mafMuta) for All, in Net1, in Net8 (#.startswith() #TCGA,net1,net8)
Comparation = [14,2,8]
TheTop = [["Rank","Gene","#Mut","Length","Net1","Net8"]]
DataW = var_holder[Data0[Comparation[0]]]
DataN1 = var_holder[Data0[Comparation[1]]]
DataN8 = var_holder[Data0[Comparation[2]]]
for i in range(Nrows):
    Gene = DataW.iat[i,0]
    Comp1 = Gene in DataN1['Gene'].values
    Comp8 = Gene in DataN8['Gene'].values
    TheTop.append([i+1,Gene,DataW.iat[i,1],DataW.iat[i,3],Comp1, Comp8])
CompTable = pd.DataFrame(TheTop[1:],columns=TheTop[0])
TheOutput = CompTable.to_csv(sep="\t", index=False)
with open(os.path.join(sys.path[0], "Comparisons","Comparison.csv"), "w") as thefile:
    thefile.write(TheOutput.replace("\r\n","\n"))
thefile.close()
#For the corrected data set (.endswith("Corrected") ) The wholes (in mafMuta) for All, in Net1, in Net8 (#.startswith() #TCGA,net1,net8)
Comparation = [15,3,9]
TheTop = [["Rank","Gene","#Mut","Length","Net1","Net8"]]
DataW = var_holder[Data0[Comparation[0]]]
DataN1 = var_holder[Data0[Comparation[1]]]
DataN8 = var_holder[Data0[Comparation[2]]]
for i in range(Nrows):
    Gene = DataW.iat[i,0]
    Comp1 = Gene in DataN1['Gene'].values
    Comp8 = Gene in DataN8['Gene'].values
    TheTop.append([i+1,Gene,DataW.iat[i,1],DataW.iat[i,3],Comp1, Comp8])
CompTable = pd.DataFrame(TheTop[1:],columns=TheTop[0])
TheOutput = CompTable.to_csv(sep="\t", index=False)
with open(os.path.join(sys.path[0], "Comparisons","ComparisonCorrected.csv"), "w") as thefile:
    thefile.write(TheOutput.replace("\r\n","\n"))
thefile.close()
#For the non corrected data set (.endswith("Freq") ) The aggressive (in mafaggr) for All, in Net1, in Net8 (#.startswith() #TCGA,net1,net8)
Comparation = [12,0,6]
TheTop = [["Rank","Gene","#Mut","Length","Net1","Net8"]]
DataW = var_holder[Data0[Comparation[0]]]
DataN1 = var_holder[Data0[Comparation[1]]]
DataN8 = var_holder[Data0[Comparation[2]]]
for i in range(Nrows):
    Gene = DataW.iat[i,0]
    Comp1 = Gene in DataN1['Gene'].values
    Comp8 = Gene in DataN8['Gene'].values
    TheTop.append([i+1,Gene,DataW.iat[i,1],DataW.iat[i,3],Comp1, Comp8])
CompTable = pd.DataFrame(TheTop[1:],columns=TheTop[0])
TheOutput = CompTable.to_csv(sep="\t", index=False)
with open(os.path.join(sys.path[0], "Comparisons","Comparisonaggre.csv"), "w") as thefile:
    thefile.write(TheOutput.replace("\r\n","\n"))
thefile.close()
#For the non corrected data set (.endswith("Freq") ) The non aggressive (in mafnon-aggr) for All, in Net1, in Net8 (#.startswith() #TCGA,net1,net8)
Comparation = [16,4,10]
TheTop = [["Rank","Gene","#Mut","Length","Net1","Net8"]]
DataW = var_holder[Data0[Comparation[0]]]
DataN1 = var_holder[Data0[Comparation[1]]]
DataN8 = var_holder[Data0[Comparation[2]]]
for i in range(Nrows):
    Gene = DataW.iat[i,0]
    Comp1 = Gene in DataN1['Gene'].values
    Comp8 = Gene in DataN8['Gene'].values
    TheTop.append([i+1,Gene,DataW.iat[i,1],DataW.iat[i,3],Comp1, Comp8])
CompTable = pd.DataFrame(TheTop[1:],columns=TheTop[0])
TheOutput = CompTable.to_csv(sep="\t", index=False)
with open(os.path.join(sys.path[0], "Comparisons","Comparisonnon-aggre.csv"), "w") as thefile:
    thefile.write(TheOutput.replace("\r\n","\n"))
thefile.close()
#For the corrected data set (.endswith("Corrected") ) The aggressive (in mafaggr) for All, in Net1, in Net8 (#.startswith() #TCGA,net1,net8)
Comparation = [13,1,7]
TheTop = [["Rank","Gene","#Mut","Length","Net1","Net8"]]
DataW = var_holder[Data0[Comparation[0]]]
DataN1 = var_holder[Data0[Comparation[1]]]
DataN8 = var_holder[Data0[Comparation[2]]]
for i in range(Nrows):
    Gene = DataW.iat[i,0]
    Comp1 = Gene in DataN1['Gene'].values
    Comp8 = Gene in DataN8['Gene'].values
    TheTop.append([i+1,Gene,DataW.iat[i,1],DataW.iat[i,3],Comp1, Comp8])
CompTable = pd.DataFrame(TheTop[1:],columns=TheTop[0])
TheOutput = CompTable.to_csv(sep="\t", index=False)
with open(os.path.join(sys.path[0], "Comparisons","ComparisonaggreCorrected.csv"), "w") as thefile:
    thefile.write(TheOutput.replace("\r\n","\n"))
thefile.close()
#For the corrected data set (.endswith("Corrected") ) The non aggressive (in mafnon-aggr) for All, in Net1, in Net8 (#.startswith() #TCGA,net1,net8)
Comparation = [17,5,11]
TheTop = [["Rank","Gene","#Mut","Length","Net1","Net8"]]
DataW = var_holder[Data0[Comparation[0]]]
DataN1 = var_holder[Data0[Comparation[1]]]
DataN8 = var_holder[Data0[Comparation[2]]]
for i in range(Nrows):
    Gene = DataW.iat[i,0]
    Comp1 = Gene in DataN1['Gene'].values
    Comp8 = Gene in DataN8['Gene'].values
    TheTop.append([i+1,Gene,DataW.iat[i,1],DataW.iat[i,3],Comp1, Comp8])
CompTable = pd.DataFrame(TheTop[1:],columns=TheTop[0])
TheOutput = CompTable.to_csv(sep="\t", index=False)
with open(os.path.join(sys.path[0], "Comparisons","Comparisonnon-aggreCorrected.csv"), "w") as thefile:
    thefile.write(TheOutput.replace("\r\n","\n"))
thefile.close()