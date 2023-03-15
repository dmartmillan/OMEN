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
- ‘coverage_corrected.pl’ → mutation frequencies corrected (all prostate cancer)
- ‘coverage_corrected_aggr.pl’ → mutation frequencies corrected and only aggresive
- ‘coverage_corrected_non_aggr.pl’ → mutation frequencies corrected and only non-aggresive

Networks:
-’network.pl’ → network1
-’network8.pl’ → network8

## Runs
- Default prostate cancer run: alpha 0.8, threshold 0.67 (No corrected gene lengths)

With default network nº 1 (with corrected gene length):
- [ ] Alpha 0 - all
- [ ] Alpha 0.2 - all
- [ ] Alpha 0.5 - all
- [ ] Alpha 0.65 - all
- [ ] Alpha 0.8 - all
- [ ] Alpha 0.9 - all
- [ ] Alpha 1 - all
- [ ] Alpha 0 - agressive 
- [ ] Alpha 0.2 - agressive
- [ ] Alpha 0.5 - agressive
- [ ] Alpha 0.65 - agressive
- [ ] Alpha 0.8 - agressive
- [ ] Alpha 0.9 - agressive
- [ ] Alpha 1 - agressive
- [ ] Alpha 0 - non-agressive 
- [ ] Alpha 0.2 - non-agressive
- [ ] Alpha 0.5 - non-agressive
- [ ] Alpha 0.65 - non-agressive
- [ ] Alpha 0.8 - non-agressive
- [ ] Alpha 0.9 - non-agressive
- [ ] Alpha 1 - non-agressive

With network nº 8 (with the corrected gene length):
- [ ] Alpha 0.65 - all
- [ ] Alpha 0.65 - agressive 
- [ ] Alpha 0.65 - non-agressive 
