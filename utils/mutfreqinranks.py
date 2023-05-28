import pandas as pd
import os
import sys
import csv

#For the mutation freq ranks
MFn1 = pd.read_csv(os.path.join(sys.path[0], "MutationFreq","net1_TCGA_prad_mafMutationFreq.csv"),sep='\t')
MFagn1 = pd.read_csv(os.path.join(sys.path[0], "MutationFreq","net1_TCGA_prad_mafaggresiveMutationFreq.csv"),sep='\t')
MFnonagn1 = pd.read_csv(os.path.join(sys.path[0], "MutationFreq","net1_TCGA_prad_mafnon-aggresiveMutationFreq.csv"),sep='\t')
MFn8 = pd.read_csv(os.path.join(sys.path[0], "MutationFreq","net8_TCGA_prad_mafMutationFreq.csv"),sep='\t')
MFagn8 = pd.read_csv(os.path.join(sys.path[0], "MutationFreq","net8_TCGA_prad_mafaggresiveMutationFreq.csv"),sep='\t')
MFnonagn8 = [[]] #For some random reason pd is not reading it so use csv
with open(os.path.join(sys.path[0], "MutationFreq","net8_TCGA_prad_mafnon-aggresiveMutationFreq.csv"), 'r') as file:
    csvreader = csv.reader(file,delimiter="\t")
    i = 0
    for row in csvreader:
        if i % 2 == 0:
            MFnonagn8.append(row)
        i+=1
MFnonagn8 = pd.DataFrame(MFnonagn8[2:], columns = MFnonagn8[1]) #Solve
MutFrq = [MFn1,MFn8,MFagn1,MFagn8,MFnonagn1,MFnonagn8] #[MFn1,MFagn1,MFnonagn1,MFn8,MFagn8,MFnonagn8]

#For the OMEN ranks
Rn1 = pd.read_csv(os.path.join(sys.path[0], "ranks","alpha_0.desc"),header=None,sep='\t')
Ragn1 = pd.read_csv(os.path.join(sys.path[0], "ranks","alpha_0_aggr.desc"),header=None,sep='\t')
Rnonagn1 = pd.read_csv(os.path.join(sys.path[0], "ranks","alpha_0_non_aggr.desc"),header=None,sep='\t')
Rn8 = pd.read_csv(os.path.join(sys.path[0], "ranks","network8_alpha_0.desc"),header=None,sep='\t')
Ragn8 = pd.read_csv(os.path.join(sys.path[0], "ranks","network8_alpha_0_aggr.desc"),header=None,sep='\t')
Rnonagn8 = pd.read_csv(os.path.join(sys.path[0], "ranks","network8_alpha_0_non_aggr.desc"),header=None,sep='\t')
Rank = [Rn1,Rn8,Ragn1,Ragn8,Rnonagn1,Rnonagn8]

Titles = [["Top for Net1","Position in Rank","Top for Net8","Position in Rank"],[],["Top for Aggr Net1","Position in Rank","Top for Aggr Net8","Position in Rank"],[],["Top for NoAggr Net1","Position in Rank","Top for NoAggr Net8","Position in Rank"],[]]
#Create the summary tables #If you want the non corrected gene freq just sort for that
for k in range(6)[::2]:
    head = [Titles[k]]
    Rank[k].columns =["Gene"]
    Rank[k].insert(1,"Rank",[i+1 for i in range(len(Rank[k].index))]) #Create the rank column
    Rank[k] = Rank[k].set_index("Gene") #gene as index for easy accessing
    Rank[k+1].columns =["Gene"]
    Rank[k+1].insert(1,"Rank",[i+1 for i in range(len(Rank[k+1].index))]) #Create the rank column
    Rank[k+1] = Rank[k+1].set_index("Gene") #gene as index for easy accessing
    for j in range(10):
        C1 = MutFrq[k].loc[j]["Gene"]
        try:
            C2 = Rank[k].loc[C1]["Rank"]
        except:
            C2 = "Not in Rank"
        C3 = MutFrq[k+1].loc[j]["Gene"]
        try:
            C4 = Rank[k+1].loc[C3]["Rank"]
        except:
            C4 = "Not in Rank"        
        head.append([C1,C2,C3,C4])
    head = pd.DataFrame(head[1:], columns = head[0]) 
    head = head.to_csv(sep="\t", index=False)

    with open(os.path.join(sys.path[0], str(k+10) + "MutFreqinRank.csv"), "w") as thefile:
        thefile.write(head)
    thefile.close()