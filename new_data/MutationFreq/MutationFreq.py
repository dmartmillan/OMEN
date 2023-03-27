import pandas as pd
import os
import sys
import matplotlib.pyplot as plt

df2 = pd.read_csv(os.path.join(sys.path[0],'TCGA_prad_maf.csv'),sep=',') #Has all the genes

dfMAF = df2[['Hugo_Symbol','Chromosome','Start_Position','Reference_Allele','Allele','Tumor_Sample_Barcode']]
dfMAF["key"] = dfMAF["Chromosome"] + ";" + dfMAF["Start_Position"].astype(str) + ";" + dfMAF["Reference_Allele"] + ";" + dfMAF["Allele"]
dfMAF["patient"] = dfMAF["Tumor_Sample_Barcode"].str.slice(0,12)#Equivalent to "case_submitter_id" column on the clinical data

genes = {}
#Counts the number of times a gene is present
for i in dfMAF.index:
    if dfMAF._get_value(i,'Hugo_Symbol') in genes:
        genes[dfMAF._get_value(i,'Hugo_Symbol')] = genes[dfMAF._get_value(i,'Hugo_Symbol')] + 1
    else:
        genes[dfMAF._get_value(i,'Hugo_Symbol')] = 1

genes_df = pd.DataFrame.from_dict(genes, orient='index')

#Get gene lenght
#pip install pyensembl #To install the package
#pyensembl install --release 77 --species human #To install the reference genome
from pyensembl import EnsemblRelease
data = EnsemblRelease()#(109) Last human reference genome
data2 = EnsemblRelease(77)#Human reference genome GRCh38
genes_lenght = {}
nofound = []
for gene in genes.keys():
    try: 
        wg = data.genes_by_name(gene)
        genes_lenght[gene] = wg[0].end - wg[0].start
    except:
        try: 
            wg = data2.genes_by_name(gene)
            genes_lenght[gene] = wg[0].end - wg[0].start
        except : #Average geno size if the gene is not found
            genes_lenght[gene] = 27894 #doi: 10.1126/science.1058040. AND https://bionumbers.hms.harvard.edu/bionumber.aspx?s=n&v=2&id=105336
            nofound.append(gene)

#Correct for gene lenght
#Mut freq : Get the number of times the gene is mutated, divide by the number of patients
#Correct : Longer genes are more likely to mutate by chance, divide by gene lenght
numpatient = dfMAF["patient"].nunique()
geneslen = [genes_lenght[name] for name in genes_lenght]
mutfreq = [100*genes_df.at[gene,0]/numpatient for gene in genes_lenght]
correctedmutfreq = [100*genes_df.at[gene,0]/genes_lenght[gene]/numpatient for gene in genes_lenght]
genes_df.columns = ["Mutations"] #Add a name to the column in the data frame
genes_df.insert(1,"MutationFreq%",mutfreq) #Insert the value in column
genes_df.insert(2,"Lenght",geneslen) #Insert the value in column
genes_df.insert(3,"Corrected",correctedmutfreq) #Insert the value in column
genes_df = genes_df.sort_values(by="Corrected",ascending=False)

Output = genes_df.to_csv(sep="\t", index=True, index_label="Gene")

with open(os.path.join(sys.path[0], 'TCGA_prad_maf.csv'.replace(".csv","")+"MutationFreqCorrected.csv"), "w") as thefile:
    thefile.write(Output)
thefile.close()

#Plots of the corrected mutation freq
fig = plt.figure(figsize =(10, 7)) # Figure Size
plt.bar(list(genes_df.index)[0:10], genes_df.iloc[0:10]["Corrected"]) # Horizontal Bar Plot
plt.ylabel("Mutation Freq [%]")
plt.title("Corrected Mutation Freq")
plt.xticks(rotation=16,fontsize=8) # Correct overlapping labels and size

for i in range(10): #To add the values to the bars
    plt.text(i,genes_df.iloc[i]["Corrected"],round(genes_df.iloc[i]["Corrected"],3), ha = 'center')
plt.savefig(os.path.join(sys.path[0], "Corrected Mutation Freq"+".png"),format="png") #Save plot, always above .show !!! Are saved in user

#Plots of the NOT CORRECTED mutation freq
genes_df = genes_df.sort_values(by="MutationFreq%",ascending=False) #First sort in base of the regular mutation freq
fig = plt.figure(figsize =(10, 7)) # Figure Size
plt.bar(list(genes_df.index)[0:10], genes_df.iloc[0:10]["MutationFreq%"]) # Horizontal Bar Plot
plt.ylabel("Mutation Freq [%]")
plt.title("Mutation Freq")
plt.xticks(rotation=16,fontsize=8) # Correct overlapping labels and size

for i in range(10): #To add the values to the bars
    plt.text(i,genes_df.iloc[i]["MutationFreq%"],round(genes_df.iloc[i]["MutationFreq%"],3), ha = 'center')
plt.savefig(os.path.join(sys.path[0], "Mutation Freq"+".png"),format="png") #Save plot, always above .show !!! Are saved in user

#### Step 2 Keep only the genes on the network ###
#For net1
net1 = pd.read_csv(os.path.join(sys.path[0],"connectiontables","net1_reactome.csvconnectiontable.csv"),sep=',',index_col=0) #Has all the genes
net1ind = genes_df.index.intersection(net1.index) #Get the intersection of the index (genes) so you get the one in common, other ways may cause problem as looking one on the other pop up some "Key erros"
net1genes_df = genes_df.loc[net1ind]#use the intersection to find the genes
net1genes_df = net1genes_df.sort_values(by="Corrected",ascending=False) 
net1Output = net1genes_df.to_csv(sep="\t", index=True, index_label="Gene")
with open(os.path.join(sys.path[0], 'net1_TCGA_prad_maf.csv'.replace(".csv","")+"MutationFreqCorrected.csv"), "w") as thefile:
    thefile.write(net1Output)
thefile.close()

#Plots of the corrected mutation freq
fig = plt.figure(figsize =(10, 7)) # Figure Size
plt.bar(list(net1genes_df.index)[0:10], net1genes_df.iloc[0:10]["Corrected"]) # Horizontal Bar Plot
plt.ylabel("Mutation Freq [%]")
plt.title("Net1 Corrected Mutation Freq")
plt.xticks(rotation=16,fontsize=8) # Correct overlapping labels and size

for i in range(10): #To add the values to the bars
    plt.text(i,net1genes_df.iloc[i]["Corrected"],round(net1genes_df.iloc[i]["Corrected"],3), ha = 'center')
plt.savefig(os.path.join(sys.path[0], "net1_Corrected Mutation Freq"+".png"),format="png") #Save plot, always above .show !!! Are saved in user

#Plots of the NOT CORRECTED mutation freq
net1genes_df = net1genes_df.sort_values(by="MutationFreq%",ascending=False) #First sort in base of the regular mutation freq
fig = plt.figure(figsize =(10, 7)) # Figure Size
plt.bar(list(net1genes_df.index)[0:10], net1genes_df.iloc[0:10]["MutationFreq%"]) # Horizontal Bar Plot
plt.ylabel("Mutation Freq [%]")
plt.title("Net1 Mutation Freq")
plt.xticks(rotation=16,fontsize=8) # Correct overlapping labels and size

for i in range(10): #To add the values to the bars
    plt.text(i,net1genes_df.iloc[i]["MutationFreq%"],round(net1genes_df.iloc[i]["MutationFreq%"],3), ha = 'center')
plt.savefig(os.path.join(sys.path[0], "net1_Mutation Freq"+".png"),format="png") #Save plot, always above .show !!! Are saved in user

#For net8
net8 = pd.read_csv(os.path.join(sys.path[0],"connectiontables","net8_reactome_remap_exprs.csvconnectiontable.csv"),sep=',',index_col=0) #Has all the genes
net8ind = genes_df.index.intersection(net8.index) #Get the intersection of the index (genes) so you get the one in common, other ways may cause problem as looking one on the other pop up some "Key errors"
net8genes_df = genes_df.loc[net8ind]
net8genes_df = net8genes_df.sort_values(by="Corrected",ascending=False) 
net8Output = net8genes_df.to_csv(sep="\t", index=True, index_label="Gene")

with open(os.path.join(sys.path[0], 'net8_TCGA_prad_maf.csv'.replace(".csv","")+"MutationFreqCorrected.csv"), "w") as thefile:
    thefile.write(net8Output)
thefile.close()

#Plots of the corrected mutation freq
fig = plt.figure(figsize =(10, 7)) # Figure Size
plt.bar(list(net8genes_df.index)[0:10], net8genes_df.iloc[0:10]["Corrected"]) # Horizontal Bar Plot
#plt.xlabel("Interaction Network")
plt.ylabel("Mutation Freq [%]")
plt.title("Net8 Corrected Mutation Freq")
plt.xticks(rotation=16,fontsize=8) # Correct overlapping labels and size

for i in range(10): #To add the values to the bars
    plt.text(i,net8genes_df.iloc[i]["Corrected"],round(net8genes_df.iloc[i]["Corrected"],3), ha = 'center')
plt.savefig(os.path.join(sys.path[0], "net8_Corrected Mutation Freq"+".png"),format="png") #Save plot, always above .show !!! Are saved in user

#Plots of the NOT CORRECTED mutation freq
net8genes_df = net8genes_df.sort_values(by="MutationFreq%",ascending=False) #First sort in base of the regular mutation freq
fig = plt.figure(figsize =(10, 7)) # Figure Size
plt.bar(list(net8genes_df.index)[0:10], net8genes_df.iloc[0:10]["MutationFreq%"]) # Horizontal Bar Plot
#plt.xlabel("Interaction Network")
plt.ylabel("Mutation Freq [%]")
plt.title("Net8 Mutation Freq")
plt.xticks(rotation=16,fontsize=8) # Correct overlapping labels and size

for i in range(10): #To add the values to the bars
    plt.text(i,net8genes_df.iloc[i]["MutationFreq%"],round(net8genes_df.iloc[i]["MutationFreq%"],3), ha = 'center')
plt.savefig(os.path.join(sys.path[0], "net8_Mutation Freq"+".png"),format="png") #Save plot, always above .show !!! Are saved in user

#### Step 3: Compare the aggresive with the not aggresive ###
#You can match the clinical data with the TCGA_prad_maf.csv using the "case_id" column
#On a second script "MutationFreq2.py"