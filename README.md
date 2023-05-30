# OMEN: Network-based Driver Gene Identification using Mutual Exclusivity (1.0.0) - Extended version

## Requirements

Linux (tested on Ubuntu 18.04)

* SWI-Prolog version >=7, on your path as 'swipl'
* Yap version >=6.2.2, on your path as 'yap' (tested on Yap 6.2.2)
* GNU Parallel, on your path as 'parallel'
* cgcp from https://github.com/dh-tran/cgcp in order to cluster a probabilistic network into modules
* bash for executing shell scripts

## Usage

Anaconda enviroment can be deployed with the [environment.yml](./environment.yml) file.

```
usage: main.py [-h] [-path_definition_file [PATH_DEFINITION_FILE]]
               [-pattern_quality_threshold [PATTERN_QUALITY_THRESHOLD]]
               [-threshold_probability [THRESHOLD_PROBABILITY]]
               [-alpha [ALPHA]] [-cores [CORES]] [-step [STEP]]
               [-outlast [OUTLAST]] [-filter FILTER [FILTER ...]]
               [cadd_file] [coverage_file] [network_file]

OMEN

positional arguments:
  cadd_file             CADD probabilities .pl filepath
  coverage_file         Coverage .pl filepath
  network_file          Network filepath

options:
  -h, --help            show this help message and exit
  -path_definition_file [PATH_DEFINITION_FILE]
                        file specifying the path distribution
  -pattern_quality_threshold [PATTERN_QUALITY_THRESHOLD]
                        float specifying the quality a pattern has to exceed
                        to be considered
  -threshold_probability [THRESHOLD_PROBABILITY]
                        math expression for the SLP threshold
  -alpha [ALPHA]        value between 0 and 1 managing trade-off between the
                        mutex term and the gene_freq term
  -cores [CORES]        number of CPU threads to employ
  -step [STEP]          step to execute OMEN
  -outlast [OUTLAST]    output folder where continue OMEN run
  -filter FILTER [FILTER ...]
                        filter specific genes
```

### Example

```bash
nohup python main.py new_data/CADD.pl new_data/coverage.pl new_data/network.pl  > outputfile  2>&1& 
```

`nohup` is employed to execute the command in the background and prevent to have the shell session opened.

For more details in OMEN _Usage_ and its construction consult the original repository [OMEN: Network-based Driver Gene Identification using Mutual Exclusivity](https://github.com/DriesVanDaele/OMEN) and the original paper [https://doi.org/10.1093/bioinformatics/btac312](https://doi.org/10.1093/bioinformatics/btac312).

## Folder distribution

Data files and scripts for parsing input data, visualizing and analyzing. 

### ***new_data* folder**

Auxiliar scrits to read and parse new data.

`network.ipynb` Create network.pl from Network data.

`gene_patient_CADD.ipynb` Creation of CADD files in all, aggr and non-aggr.

`gene_patient_CADD_correct.ipynb` Creation of CADD files in all and coverage file.

`cancer_type_separator.py` Separation of cancer type (aggr and non-aggr).

### ***visualization* folder**

Files for visualization of the probabilistic networks. 

`probabilistic_network.ipynb` Creation of `subnetwork` js file.

`probabilistic_network_aggr_vs_non_aggr.ipynb` Creation of `subnetwork` js file, for aggr. vs non-aggr.

To visualize network: `subnetwork.html`, `gonetic.js`, `gonetic_aggr_vs_non_aggr.js` and `subnetwork_*.js`.

### ***utils* folder**

Auxiliar scripts to make plots and analyze the OMEN's output. 

`alpha_links.ipynb` Quantification of different link types in different alpha values.

`alpha_vs_ranking_most_mutated_genes.ipynb` Alpha vs Ranking of the most mutated genes.

`clustering_plot.ipynb` Gene heatmap in different clusters. 

`compare_rankings.ipynb` Comparison of ranking with Venn diagram, Spearman, Overlap genes and Non-overlap genes, in different alpha values.

`compare_rankings_same_alpha.ipynb` Comparison of ranking with Venn diagram, with the same alpha values.

`differences_in_gene_ranking.ipynb` Differences between rankings of different alpha values.

`differences_in_gene_ranking_same_alpha.ipynb` Differences between rankings of same alpha values.

`differences_in_gene_table.ipynb` Differences between rankings of different alpha values, in a table.

`gene_checker.ipynb` Check if some certain genes appear in the rankings.

`network_selection.ipynb` Analyse different input networks with genes overlap.

`probabilistic_network_aggr_vs_non_aggr.ipynb` Comparison between aggr. and non-aggr networks.

`probabilistic_network_aggr_vs_non_aggr_overlapping_genes.ipynb` Comparison between aggr. and non-aggr networks, using gene overlap.

`threshold_distribution.ipynb` Evaluation of threshold distribution in a OMEN's run.

`MutationFreq` Mutation frequency data, plots and scripts. 
