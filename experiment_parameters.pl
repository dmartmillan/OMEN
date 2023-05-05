data('new_data/CADD_non_aggr.pl', 'new_data/coverage_non_aggr.pl').
network_file('new_data/network_Reactome_Dorothea_Aracne.pl').
threshold(1e-06).
pattern_quality_metric_threshold(0.67).
mcda(weighted_sum, [0.65, 0]).
path_definition_file('example_data/path_definition.dslp').
