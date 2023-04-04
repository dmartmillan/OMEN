import pandas as pd
import os
import sys

#To compare which relevant genes are present on each ranking
def CompSummary(DictRanks):
    reference = pd.read_csv(os.path.join(sys.path[0],"reference.txt"),sep="\t")
    N = len(reference)
    TheTop = ["Gene"]
    for name in DictRanks:
        TheTop.append(name.replace(".desc",""))
    CompTable = [TheTop]
    for i in range(N):
        Gene = reference.iat[i,0]
        RowN = [Gene]
        for name in DictRanks:
            Rankn = DictRanks[name]
            Find = Gene in Rankn.index.values
            if Find == True : Find = Rankn._get_value(Gene, "Rank")
            RowN.append(Find)
        CompTable.append(RowN)
    CompTable = pd.DataFrame(CompTable[1:],columns=CompTable[0])
    CompTable = CompTable.sort_values(by=["Gene"])
    CompOutput = CompTable.to_csv(sep="\t", index=False)
    with open(os.path.join(sys.path[0], "summary","RelevantGenesComp.csv"), "w") as thefile:
        thefile.write(CompOutput.replace("\r\n","\n"))
    thefile.close()
    return CompTable

#To compare if there are genes always found on all the ranks
def CompareRanks(DictRanks, Top = 100, filenames = None):
    Listi = []
    if filenames == None : filenames = [x for x in DictRanks]
    for name in filenames:
        if Listi == []:
            Listi = DictRanks[name].index.to_list()[:Top]
        RankN =DictRanks[name]
        Listj = RankN.index.to_list()[:Top]
        Listi = list(set(Listi) & set(Listj))
    return Listi

#To generate the files
def WorkingRanks():
    var_holder = {}
    filenames = [file for file in os.listdir(sys.path[0]) if file.endswith(".desc") ] #To only open .csv files
    for filename in filenames:
        var_holder[filename] =  pd.read_csv(os.path.join(sys.path[0], filename),sep="\t",header=None)
        Workdf = var_holder[filename]
        Ranked = [i+1 for i in range(len(Workdf))]
        Workdf.columns = ["Gene"]
        Workdf.insert(1,"Rank",Ranked) #Insert the value in column
        var_holder[filename] = Workdf.set_index("Gene")
    return var_holder

#Main
var_holder = WorkingRanks()

#Compare which relevant genes are present on each ranking
CompSummary(var_holder)

#compare if there are genes always found on all the ranks
WAll = CompareRanks(var_holder)
print("For all ",WAll,len(WAll))

#Filters to identiphy the sets:
#.startswith() #net1,net8,TCGA
#.endswith() #Freq,Corrected
# in  #mafMuta,mafnon-aggr,mafaggr

#Do it per type
Subvar = [x for x in var_holder if "d_aggr" in x] #For net8
Agg = CompareRanks(var_holder, filenames = Subvar)
print("For Aggre ",Agg,len(Agg))
Subvar = [x for x in var_holder if "non_aggr" in x] #For net8
Nagg = CompareRanks(var_holder, filenames = Subvar)
print("For Non Aggre ",Nagg,len(Nagg))

#Per network
Subvar = [x for x in var_holder if x.startswith("alpha")] #For net8
W1 = CompareRanks(var_holder, filenames = Subvar)
print("For net1 ",W1,len(W1))
Subvar = [x for x in var_holder if x.startswith("network8")] #For net8
W8 = CompareRanks(var_holder, filenames = Subvar)
print("For net8 ",W8,len(W8))

#Per alpha 0, 0.65, 1
Subvar = [x for x in var_holder if "alpha_0" in x] 
Wa0 = CompareRanks(var_holder, filenames = Subvar)
print("For alpha 0 ",Wa0,len(Wa0))
Subvar = [x for x in var_holder if "alpha_0.65" in x] 
Wa065 = CompareRanks(var_holder, filenames = Subvar)
print("For alpha 0.65 ",Wa065,len(Wa065))
Subvar = [x for x in var_holder if "alpha_1" in x] 
Wa1 = CompareRanks(var_holder, filenames = Subvar)
print("For alpha 1 ",Wa1,len(Wa1))