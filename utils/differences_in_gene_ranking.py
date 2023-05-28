import pandas as pd
import seaborn as sns
import os
import sys

#Iterations from outside to inside, add more if you want according to the format, run all the combinations, for a specific one eliminate from the list
networks = ["","network8_"]
cancertypes = ["_corrected.desc","_corrected_aggr.desc","_corrected_non_aggr.desc"]
alphas = [0,0.65,1]

for network in networks:
    for cancertype in cancertypes:
        rankings = {}
        for alpha in alphas:
            df = pd.read_csv(os.path.join(sys.path[0],"ranks",network+"alpha_"+str(alpha)+cancertype),header = None)
            df = df[df.index<100]
            rankings[str(alpha)] = {k: i for i, k in enumerate(df[0].tolist())}


        intersection_0_065 = set(rankings['0'].keys()).intersection(set(rankings['0.65'].keys())) 
        intersection_0_1 = set(rankings['0'].keys()).intersection(set(rankings['1'].keys())) 
        intersection_065_1 = set(rankings['0.65'].keys()).intersection(set(rankings['1'].keys()))

        ## Intersection 0 - 0.65
        inter = []
        distance = []
        position = []

        inter_0_065_section = []


        for g in intersection_0_065:
            value_0 = rankings['0'][g]
            value_065 = rankings['0.65'][g]
            same_section = "Equal section" if (value_0 > 50 and value_065 > 50) or (value_0 < 50 and value_065 < 50) else "Nonequal section"
            position.append(same_section)
            if (value_0 > 50 and value_065 > 50) or (value_0 < 50 and value_065 < 50):
                inter_0_065_section.append(g)
            total_value = abs(value_0 - value_065)
            inter.append("0 - 0.65")
            distance.append(total_value) 
        inter_0_1_section = []

        ## Intersection 0 - 1
        for i, g in enumerate(intersection_0_1):
            value_0 = rankings['0'][g]
            value_1 = rankings['1'][g]
            same_section = "Equal section" if (value_0 > 50 and value_1 > 50) or (value_0 < 50 and value_1 < 50) else "Nonequal section"
            position.append(same_section)
            if (value_0 > 50 and value_1 > 50) or (value_0 < 50 and value_1 < 50):
                inter_0_1_section.append(g)
            total_value = abs(value_0 - value_1)
            inter.append("0 - 1")
            distance.append(total_value)
        inter_065_1_section = []

        ## Intersection 0.65 - 1
        for i, g in enumerate(intersection_065_1):
            value_065 = rankings['0.65'][g]
            value_1 = rankings['1'][g]
            same_section = "Equal section" if (value_065 > 50 and value_1 > 50) or (value_065 < 50 and value_1 < 50) else "Nonequal section"
            position.append(same_section)
            if (value_065 > 50 and value_1 > 50) or (value_065 < 50 and value_1 < 50):
                inter_065_1_section.append(g)
            total_value = abs(value_065 - value_1)
            inter.append("0.65 - 1")
            distance.append(total_value)

            
        d = {'Intersection': inter, 'Distance': distance, 'Position': position}
        df = pd.DataFrame(data=d)

        sns_plot = sns.catplot(data=df, x="Intersection", y="Distance", hue="Position", kind="swarm")
        sns_plot.savefig(os.path.join(sys.path[0],"plots_ranks", network + "differences_ranking_genes" + cancertype.replace(".desc","")+".png")) 

        print("Intersection 0 - 0.65")
        print("Total genes: " + str(len(intersection_0_065)) + " of 100.")
        print("Equal section: " + str(len(inter_0_065_section)) + ".")
        print("Nonequal section: " + str(len(intersection_0_065) - len(inter_0_065_section)) + ".")
        print("\n")
        print("Intersection 0 - 1")
        print("Total genes: " + str(len(intersection_0_1)) + " of 100.")
        print("Equal section: " + str(len(inter_0_1_section)) + ".")
        print("Nonequal section: " + str(len(intersection_0_1) - len(inter_0_1_section)) + ".")
        print("\n")
        print("Intersection 0.65 - 1")
        print("Total genes: " + str(len(intersection_065_1)) + " of 100.")
        print("Equal section: " + str(len(inter_065_1_section)) + ".")
        print("Nonequal section: " + str(len(intersection_065_1) - len(inter_065_1_section)) + ".")

        inter_eq = set(inter_065_1_section).intersection(set(inter_0_1_section))
        inter_eq_2 = set(inter_0_065_section).intersection(inter_eq)
        print("Overlap between all the Equal section genes: " + str(len(inter_eq_2)))
        print("\n")

        genes_section = []
        pos_0 = []
        pos_1 = []
        pos_065 = []

        for g in inter_eq_2: 
            genes_section.append(g)
            pos_0.append(rankings[str(0)][g])
            pos_065.append(rankings[str(0.65)][g])
            pos_1.append(rankings[str(1)][g])
            
        d_section = {'Gene': genes_section, 'Pos ranking 0': pos_0, 'Pos ranking 0.65': pos_065, 'Pos ranking 1': pos_1}
        df_section = pd.DataFrame(data=d_section)
        print(df_section)
        df_section.to_csv(os.path.join(sys.path[0],"plots_ranks", network + "Overlap_between_all_the_Equal_section" + cancertype.replace(".desc","")+".csv"))  
