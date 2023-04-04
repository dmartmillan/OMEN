import pandas as pd
import os
import sys

var_holder = {}
filenames = [file for file in os.listdir(sys.path[0]) if file.endswith(".txt") ] #To only open .csv files
for filename in filenames:
    var_holder[filename] =  pd.read_csv(os.path.join(sys.path[0], filename),sep="\t")

SumTable = [["DataSet","GeneSet","Description","pVal","FDR"]]
for name in filenames:
    Df = var_holder[name]
    SumTable.append([name.replace(".txt",""),Df.iat[0,0],Df.iat[0,1],round(Df.iat[0,7], 5),round(Df.iat[0,8], 5)])

SumTable = pd.DataFrame(SumTable[1:], columns=SumTable[0])
SumOutput = SumTable.to_csv(sep="\t", index=False)
with open(os.path.join(sys.path[0], "GOSummary.csv"), "w") as thefile:
    thefile.write(SumOutput.replace("\r\n","\n"))
thefile.close()