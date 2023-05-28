import pandas as pd
import sys 
import os
import math
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches

"""
Creates '.png' files with the relevant info of the networks
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

# Show barplots of networks: number of genes, most connected genes, number of genes with only 1 connection and average number of connections
def conbarplot():
    ## Data storage
    tableval = []
    A = [var_holder[filename].shape[0] for filename in var_holder] # Number of genes
    B = [round(var_holder[filename].sum()["Number of connections"]/var_holder[filename].shape[0],2) for filename in var_holder] # Average number of connections
    C = [var_holder[filename].iat[0,1] for filename in var_holder] # No. of Connection of Most Connected Gene 
    Cn = [var_holder[filename].iat[0,0] for filename in var_holder] #Most Connected Gene 
    D = [round(var_holder[filename][var_holder[filename]["Number of connections"] == 1].count()["Number of connections"]/var_holder[filename].shape[0],2) for filename in var_holder] # Proportion of genes with one connection
    for n in [A,B,C,D]:
        tableval.append(n)

    ylabels = ["No. of Genes","No. of Connections","Number of Connections","Proportion"]
    titles = ["Number of Genes per Network","Average Number of Connections","Most Connected Gene per Network","Proportion of Genes with One Connection in the Network"]
    colors = ["blue","orange","green","red","purple","brown","pink","gray","olive","cyan"] # Horizontal
    #Look to get all the figures
    for j in range(len(tableval)):
        fig,ax = plt.subplots(figsize =(12, 12)) # Figure Size
        ax.bar(tablename[0:10], tableval[j][0:10], color = colors) # Horizontal Bar Plot
        #plt.xlabel("Interaction Network")
        plt.ylabel(ylabels[j],fontsize = 14)
        #plt.title(titles[j])
        #plt.xticks(rotation=16,fontsize=8) # Correct overlapping labels and size

        for i in range(len(tablename)): #To add the values to the bars
            if j == 2:
                if i == 5:
                    plt.text(i,tableval[j][i],"IGKV1\nOR2-108\n"+str(tableval[j][i]), ha = 'center',fontsize = 16)
                else:
                    plt.text(i,tableval[j][i],Cn[i]+"\n"+str(tableval[j][i]), ha = 'center',fontsize = 16)
            else:
                plt.text(i,tableval[j][i],tableval[j][i], ha = 'center',fontsize = 16)

        patches = []
        for i in range(10):
            Apatch = mpatches.Patch(color=colors[i], label=tablename[i])
            patches.append(Apatch)

        #ax.legend(handles=patches) #Create your own legend with patches

        plt.savefig(os.path.join(sys.path[0], titles[j]+'.png'),format="png") #Save plot, always above .show !!! Are saved in user
        #plt.show() # Show Plot, it erase the plot of the memory after show it
#,"net3_correlation_08","net4_correlation_09","net5_MRrank_10","net6_MRrank_20","net7_MRrank_30","net8_reactome_remap_exprs","net9_reactome_remap_active_inferred","net10_reactome_remap_notriangles"
conbarplot()

def netsummary(nbins):
    plotnames = ["Summary","RelSummary","RelogSummary"]
    valx = [i+1 for i in range(nbins)]
    A = []
    B = []
    C = []
    valy = []
    for filename in var_holder: #To create the valy of all the files

        rangex = len(var_holder[filename]["Number of connections"])//nbins
        dif = len(var_holder[filename]["Number of connections"]) - rangex*nbins

        rangeincres = []
        nn = 0
        for n in range(nbins):
            if n < dif:
                nn+=1
                rangeincres.append(nn)
            else:
                rangeincres.append(nn)

        rangexs = [i*rangex for i in range(nbins+1)] #Is the base for the slices, thats why it begin with a 0
        for m in range(len(rangeincres)): #Increase the range to add the diff
            rangexs[m+1] += rangeincres[m]
   
        Temval = [sum(var_holder[filename]["Number of connections"].tolist()[rangexs[i]:rangexs[i+1]]) for i in range(nbins)]
        A.append(Temval)

        Temval = [sum(var_holder[filename]["Number of connections"].tolist()[rangexs[i]:rangexs[i+1]])/(rangexs[i+1]-rangexs[i]) for i in range(nbins)]
        B.append(Temval)

        Temval = [math.log(sum(var_holder[filename]["Number of connections"].tolist()[rangexs[i]:rangexs[i+1]])/(rangexs[i+1]-rangexs[i]),2) for i in range(nbins)]
        C.append(Temval)

    #C.reverse()
    for n in [A,B,C]:
        valy.append(n)

    for plot in range(len(plotnames)):
        for i in range(len(var_holder)): # Create the curves of all files
            plt.plot(valx,valy[plot][i],label = tablename[i])
        plt.legend()
        plt.savefig(os.path.join(sys.path[0], plotnames[plot]+".png"),format="png")
        plt.show()

#netsummary(100)