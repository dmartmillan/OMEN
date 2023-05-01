## Files setup

3 input files: 
- CADD
- Coverage
- Network

CADD: 
- ‘CADD.pl’ → for all the mutations in the prostate data
- ’CADD_aggr.pl’ → Only for the mutations in the aggresive prostate data
- ’CADD_non_aggr.pl’ → Only for the mutations in the non-aggresive prostate data

Coverage:
- ‘coverage.pl’ → mutation frequencies not corrected (all prostate cancer)
- ‘coverage_aggr.pl’ → mutation frequencies not corrected and only aggresive
- ‘coverage_non_aggr.pl’ → mutation frequencies not corrected and only non-aggresive
- ‘coverage_corrected.pl’ → mutation frequencies corrected (all prostate cancer)
- ‘coverage_corrected_aggr.pl’ → mutation frequencies corrected and only aggresive
- ‘coverage_corrected_non_aggr.pl’ → mutation frequencies corrected and only non-aggresive

Networks:
- ’network.pl’ → network1
- ’network8.pl’ → network8
- ´network_MR_stack01.pl´ - MR_stack01
- ´network_new_Reactome.pl´ - Reactome
- ´network_Reactome_Dorothea_Aracne.pl´ - Reactome_Dorothea_Aracne

## Runs
- Default prostate cancer run: alpha 0.8, threshold 0.67 (No corrected gene lengths)

With default network nº 1 (with corrected gene length):
- [X] Alpha 0 - all
```ssh
nohup python main.py new_data/CADD.pl new_data/coverage_corrected.pl new_data/network.pl -alpha 0 &> outputfile_all_corrected_0.0 2>&1&
```
- [X] Alpha 0.2 - all
```ssh
nohup python main.py new_data/CADD.pl new_data/coverage_corrected.pl new_data/network.pl -alpha 0.2 &> outputfile_all_corrected_0.2 2>&1&
```
- [X] Alpha 0.5 - all
```ssh
nohup python main.py new_data/CADD.pl new_data/coverage_corrected.pl new_data/network.pl -alpha 0.5 &> outputfile_all_corrected_0.5 2>&1&
```
- [X] Alpha 0.65 - all
```ssh
nohup python main.py new_data/CADD.pl new_data/coverage_corrected.pl new_data/network.pl -alpha 0.65 &> outputfile_all_corrected_0.65 2>&1&
```
- [X] Alpha 0.8 - all
```ssh
nohup python main.py new_data/CADD.pl new_data/coverage_corrected.pl new_data/network.pl -alpha 0.8 &> outputfile_all_corrected_0.8 2>&1&
```
- [X] Alpha 0.9 - all 
```ssh
nohup python main.py new_data/CADD.pl new_data/coverage_corrected.pl new_data/network.pl -alpha 0.9 &> outputfile_all_corrected_0.9 2>&1&
```
- [X] Alpha 1 - all 
```ssh
nohup python main.py new_data/CADD.pl new_data/coverage_corrected.pl new_data/network.pl -alpha 1 &> outputfile_all_corrected_1 2>&1&
```
- [X] Alpha 0 - agressive 
```ssh
nohup python main.py new_data/CADD_aggr.pl new_data/coverage_corrected_aggr.pl new_data/network.pl -alpha 0 &> outputfile_aggr_corrected_0.0 2>&1&
```
- [X] Alpha 0.2 - agressive
```ssh
nohup python main.py new_data/CADD_aggr.pl new_data/coverage_corrected_aggr.pl new_data/network.pl -alpha 0.2 &> outputfile_aggr_corrected_0.2 2>&1&
```
- [X] Alpha 0.5 - agressive
```ssh
nohup python main.py new_data/CADD_aggr.pl new_data/coverage_corrected_aggr.pl new_data/network.pl -alpha 0.5 &> outputfile_aggr_corrected_0.5 2>&1&
```
- [X] Alpha 0.65 - agressive
```ssh
nohup python main.py new_data/CADD_aggr.pl new_data/coverage_corrected_aggr.pl new_data/network.pl -alpha 0.65 &> outputfile_aggr_corrected_0.65 2>&1&
```
- [X] Alpha 0.8 - agressive
```ssh
nohup python main.py new_data/CADD_aggr.pl new_data/coverage_corrected_aggr.pl new_data/network.pl -alpha 0.8 &> outputfile_aggr_corrected_0.8 2>&1&
```
- [X] Alpha 0.9 - agressive
```ssh
nohup python main.py new_data/CADD_aggr.pl new_data/coverage_corrected_aggr.pl new_data/network.pl -alpha 0.9 &> outputfile_aggr_corrected_0.9 2>&1&
```
- [X] Alpha 1 - agressive
```ssh
nohup python main.py new_data/CADD_aggr.pl new_data/coverage_corrected_aggr.pl new_data/network.pl -alpha 1 &> outputfile_aggr_corrected_1 2>&1&
```
- [X] Alpha 0 - non-agressive 
```ssh
nohup python main.py new_data/CADD_non_aggr.pl new_data/coverage_corrected_non_aggr.pl new_data/network.pl -alpha 0 &> outputfile_nonaggr_corrected_0.0 2>&1&
```
- [X] Alpha 0.2 - non-agressive
```ssh
nohup python main.py new_data/CADD_non_aggr.pl new_data/coverage_corrected_non_aggr.pl new_data/network.pl -alpha 0.2 &> outputfile_nonaggr_corrected_0.2 2>&1&
```
- [X] Alpha 0.5 - non-agressive
```ssh
nohup python main.py new_data/CADD_non_aggr.pl new_data/coverage_corrected_non_aggr.pl new_data/network.pl -alpha 0.5 &> outputfile_nonaggr_corrected_0.5 2>&1&
```
- [X] Alpha 0.65 - non-agressive
```ssh
nohup python main.py new_data/CADD_non_aggr.pl new_data/coverage_corrected_non_aggr.pl new_data/network.pl -alpha 0.65 &> outputfile_nonaggr_corrected_0.65 2>&1&
```
- [X] Alpha 0.8 - non-agressive
```ssh
nohup python main.py new_data/CADD_non_aggr.pl new_data/coverage_corrected_non_aggr.pl new_data/network.pl -alpha 0.8 &> outputfile_nonaggr_corrected_0.8 2>&1&
```
- [X] Alpha 0.9 - non-agressive
```ssh
nohup python main.py new_data/CADD_non_aggr.pl new_data/coverage_corrected_non_aggr.pl new_data/network.pl -alpha 0.9 &> outputfile_nonaggr_corrected_0.9 2>&1&
```
- [X] Alpha 1 - non-agressive
```ssh
nohup python main.py new_data/CADD_non_aggr.pl new_data/coverage_corrected_non_aggr.pl new_data/network.pl -alpha 1 &> outputfile_nonaggr_corrected_1 2>&1&
```
With network nº 8 (with the corrected gene length):
- [X] Alpha 0 - all
```ssh
nohup python main.py new_data/CADD.pl new_data/coverage_corrected.pl new_data/network8.pl -alpha 0.0 &> outputfile_all_net8_0.0 2>&1&
```
- [X] Alpha 0.65 - all
```ssh
nohup python main.py new_data/CADD.pl new_data/coverage_corrected.pl new_data/network8.pl -alpha 0.65 &> outputfile_all_net8_0.65 2>&1&
```
- [X] Alpha 1 - all
```ssh
nohup python main.py new_data/CADD.pl new_data/coverage_corrected.pl new_data/network8.pl -alpha 1 &> outputfile_all_net8_1 2>&1&
```
- [X] Alpha 0 - agressive
```ssh
nohup python main.py new_data/CADD_aggr.pl new_data/coverage_corrected_aggr.pl new_data/network8.pl -alpha 0 &> outputfile_aggr_net8_0.0 2>&1&
```
- [X] Alpha 0.65 - agressive
```ssh
nohup python main.py new_data/CADD_aggr.pl new_data/coverage_corrected_aggr.pl new_data/network8.pl -alpha 0.65 &> outputfile_aggr_net8_0.65 2>&1&
```
- [X] Alpha 1 - agressive
```ssh
nohup python main.py new_data/CADD_aggr.pl new_data/coverage_corrected_aggr.pl new_data/network8.pl -alpha 1 &> outputfile_aggr_net8_1 2>&1&
```
- [X] Alpha 0 - non-agressive
```ssh
nohup python main.py new_data/CADD_non_aggr.pl new_data/coverage_corrected_non_aggr.pl new_data/network8.pl -alpha 0.0 &> outputfile_nonaggr_net8_0.0 2>&1&
```
- [X] Alpha 0.65 - non-agressive
```ssh
nohup python main.py new_data/CADD_non_aggr.pl new_data/coverage_corrected_non_aggr.pl new_data/network8.pl -alpha 0.65 &> outputfile_nonaggr_net8_0.65 2>&1&
```
- [X] Alpha 1 - non-agressive
```ssh
nohup python main.py new_data/CADD_non_aggr.pl new_data/coverage_corrected_non_aggr.pl new_data/network8.pl -alpha 1 &> outputfile_nonaggr_net8_1 2>&1&
```

## Plots
Scripts:
- Compare_rankings.ipynb
- differences_in_gene_ranking.ipynb


## New runs for non-corrected
- [X] Alpha 0 - non-corrected - agressive 
```ssh
nohup python main.py new_data/CADD_aggr.pl new_data/coverage_aggr.pl new_data/network.pl -alpha 0 &> outputfile_aggr_0.0 2>&1&
```
- [X] Alpha 0.65 - non-corrected - agressive 
```ssh
nohup python main.py new_data/CADD_aggr.pl new_data/coverage_aggr.pl new_data/network.pl -alpha 0.65 &> outputfile_aggr_0.65 2>&1&
```
- [X] Alpha 1 - non-corrected - agressive 
```ssh
nohup python main.py new_data/CADD_aggr.pl new_data/coverage_aggr.pl new_data/network.pl -alpha 0.65 &> outputfile_aggr_0.65 2>&1&
```

- [X] Alpha 0 - non-corrected - non-agressive 
```ssh
nohup python main.py new_data/CADD_non_aggr.pl new_data/coverage_non_aggr.pl new_data/network.pl -alpha 0 &> outputfile_nonaggr_0.0 2>&1&
```
- [X] Alpha 0.65 - non-corrected - non-agressive 
```ssh
nohup python main.py new_data/CADD_non_aggr.pl new_data/coverage_non_aggr.pl new_data/network.pl -alpha 0.65 &> outputfile_nonaggr_0.65 2>&1&
```
- [X] Alpha 1 - non-corrected - non-agressive 
```ssh
nohup python main.py new_data/CADD_non_aggr.pl new_data/coverage_non_aggr.pl new_data/network.pl -alpha 1 &> outputfile_nonaggr_1 2>&1&
```

## New runs for non-corrected in Network8
- [X] Alpha 0 - non-corrected - all
```ssh
nohup python main.py new_data/CADD.pl new_data/coverage.pl new_data/network8.pl -alpha 0.0 &> outputfile_not_all_net8_0.0 2>&1&
```
- [X] Alpha 0.65 - non-corrected - all
```ssh
nohup python main.py new_data/CADD.pl new_data/coverage.pl new_data/network8.pl -alpha 0.65 &> outputfile_not_all_net8_0.65 2>&1&
```
- [X] Alpha 1 - non-corrected - all
```ssh
nohup python main.py new_data/CADD.pl new_data/coverage.pl new_data/network8.pl -alpha 1 &> outputfile_not_all_net8_1 2>&1&
```

- [X] Alpha 0 - non-corrected - agressive
```ssh
nohup python main.py new_data/CADD_aggr.pl new_data/coverage_aggr.pl new_data/network8.pl -alpha 0 &> outputfile_not_aggr_net8_0.0 2>&1&
```
- [X] Alpha 0.65 - non-corrected - agressive
```ssh
nohup python main.py new_data/CADD_aggr.pl new_data/coverage_aggr.pl new_data/network8.pl -alpha 0.65 &> outputfile_not_aggr_net8_0.65 2>&1&
```
- [X] Alpha 1 - non-corrected - agressive
```ssh
nohup python main.py new_data/CADD_aggr.pl new_data/coverage_aggr.pl new_data/network8.pl -alpha 1 &> outputfile_not_aggr_net8_1 2>&1&
```

- [X] Alpha 0 - non-corrected - non-agressive
```ssh
nohup python main.py new_data/CADD_non_aggr.pl new_data/coverage_non_aggr.pl new_data/network8.pl -alpha 0.0 &> outputfile_not_nonaggr_net8_0.0 2>&1&
```
- [X] Alpha 0.65 - non-corrected - non-agressive
```ssh
nohup python main.py new_data/CADD_non_aggr.pl new_data/coverage_non_aggr.pl new_data/network8.pl -alpha 0.65 &> outputfile_not_nonaggr_net8_0.65 2>&1&
```
- [X] Alpha 1 - non-corrected - non-agressive
```ssh
nohup python main.py new_data/CADD_non_aggr.pl new_data/coverage_non_aggr.pl new_data/network8.pl -alpha 1 &> outputfile_not_nonaggr_net8_1 2>&1&
```

## Reduced non-aggressive

- [X] Alpha 0.65 - non-corrected - non-agressive 
```ssh
nohup python main.py new_data/CADD_non_aggr_reduced.pl new_data/coverage_non_aggr_reduced.pl new_data/network.pl -alpha 0.65 &> outputfile_nonaggr_0.65_reduced 2>&1&
```
- [X] Alpha 0.65 - non-corrected - non-agressive
```ssh
nohup python main.py new_data/CADD_non_aggr_reduced.pl new_data/coverage_non_aggr_reduced.pl new_data/network8.pl -alpha 0.65 &> outputfile_not_nonaggr_net8_0.65_reduced 2>&1&
```

## New networks
### network_MR_stack01.pl
- [X] Alpha 0.65 - non-corrected - all
```ssh
nohup python main.py new_data/CADD.pl new_data/coverage.pl new_data/network_MR_stack01.pl -alpha 0.65 &> outputfile_not_all_MR_stack01_0.65 2>&1&
```
- [ ] Alpha 0.65 - non-corrected - agressive
```ssh
nohup python main.py new_data/CADD_aggr.pl new_data/coverage_aggr.pl new_data/network_MR_stack01.pl -alpha 0.65 &> outputfile_not_aggr_MR_stack01_0.65 2>&1&
```
- [X] Alpha 0.65 - non-corrected - non-agressive
```ssh
nohup python main.py new_data/CADD_non_aggr.pl new_data/coverage_non_aggr.pl new_data/network_MR_stack01.pl -alpha 0.65 &> outputfile_not_nonaggr_MR_stack01_0.65 2>&1&
```

### network_new_Reactome.pl
- [ ] Alpha 0.65 - non-corrected - all
```ssh
nohup python main.py new_data/CADD.pl new_data/coverage.pl new_data/network_new_Reactome.pl -alpha 0.65 &> outputfile_not_all_new_Reactome_0.65 2>&1&
```
- [ ] Alpha 0.65 - non-corrected - agressive
```ssh
nohup python main.py new_data/CADD_aggr.pl new_data/coverage_aggr.pl new_data/network_new_Reactome.pl -alpha 0.65 &> outputfile_not_aggr_new_Reactome_0.65 2>&1&
```
- [ ] Alpha 0.65 - non-corrected - non-agressive
```ssh
nohup python main.py new_data/CADD_non_aggr.pl new_data/coverage_non_aggr.pl new_data/network_new_Reactome.pl -alpha 0.65 &> outputfile_not_nonaggr_new_Reactome_0.65 2>&1&
```

### network_Reactome_Dorothea_Aracne.pl
- [ ] Alpha 0.65 - non-corrected - all
```ssh
nohup python main.py new_data/CADD.pl new_data/coverage.pl new_data/network_Reactome_Dorothea_Aracne.pl -alpha 0.65 &> outputfile_not_all_Dorothea_Aracne_0.65 2>&1&
```
- [ ] Alpha 0.65 - non-corrected - agressive
```ssh
nohup python main.py new_data/CADD_aggr.pl new_data/coverage_aggr.pl new_data/network_Reactome_Dorothea_Aracne.pl-alpha 0.65 &> outputfile_not_aggr_Dorothea_Aracne_0.65 2>&1&
```
- [ ] Alpha 0.65 - non-corrected - non-agressive
```ssh
nohup python main.py new_data/CADD_non_aggr.pl new_data/coverage_non_aggr.pl new_data/network_Reactome_Dorothea_Aracne.pl -alpha 0.65 &> outputfile_not_nonaggr_Dorothea_Aracne_0.65 2>&1&
```
