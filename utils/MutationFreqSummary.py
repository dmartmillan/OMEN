import pandas as pd
import os
import sys
import matplotlib.pyplot as plt

#Tables and plots of the non corrected and corrected mutation frequency for the whole, aggressive and non aggressive data set. 

#1 Load the input data
df = pd.read_csv(os.path.join(sys.path[0],'TCGA_PRAD_CADD.csv'),sep='\t')
df["key"] = "chr" + df["#Chrom"].astype(str) + ";" + df["Pos"].astype(str) + ";" + df["Ref"] + ";" + df["Alt"]
dfCADD = df[['#Chrom','Pos','Ref','Alt','key','PHRED']]
dfCADD_2 = dfCADD[['key','PHRED']]
dfCADD_3 = dfCADD_2.set_index('key')
dfCADD_4 = dfCADD_3.T
D = dfCADD_4.to_dict()

df2 = pd.read_csv(os.path.join(sys.path[0],'TCGA_prad_maf.csv'),sep=',') #Has all the genes
dfMAF = df2[['Hugo_Symbol','Chromosome','Start_Position','Reference_Allele','Allele','Tumor_Sample_Barcode']]
dfMAF["key"] = dfMAF["Chromosome"] + ";" + dfMAF["Start_Position"].astype(str) + ";" + dfMAF["Reference_Allele"] + ";" + dfMAF["Allele"]
dfMAF["patient"] = dfMAF["Tumor_Sample_Barcode"].str.slice(0,12)#Equivalent to "case_submitter_id" column on the clinical data
dfMAF = dfMAF.set_index("patient")
saved = dfMAF

#2 Load the files and classificate them according to cancer type (Whole, non aggressive and aggresive)
cancertypesfile = ["clinical.tsvaggresive.csv","clinical.tsvnon-aggresive.csv"]
for cancertype in cancertypesfile:
    dfMAF = saved
    cancerfile  = pd.read_csv(os.path.join(sys.path[0],cancertype),sep=',',index_col=1) #Has the patients with the cancer type
    typeind = dfMAF.index.intersection(cancerfile.index) #Get the intersection of the index (patients) so you get the one in common, other ways may cause problem as looking one on the other pop up some "Key errors"
    dfMAF = dfMAF.loc[typeind] #Get just the patients with the cancer type
    dfMAF = dfMAF.reset_index() #Restore the index to counts, as it is used later
    dfMAF = dfMAF.drop('index', axis=1) #Eliminate the row (axis=1) created after reseting the index
    dfMAF["patient"] = dfMAF["Tumor_Sample_Barcode"].str.slice(0,12) #To make everything as it was
    
    cancertype = cancertype.replace("clinical.tsv","").replace(".csv","")
    #From this point you can work as on the regular MutationFreq.py file (modifications to store the name in a more effective)