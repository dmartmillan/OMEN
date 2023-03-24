data('new_data/CADD_aggr.pl', 'new_data/coverage_corrected_aggr.pl').
network_file('new_data/network8.pl').
threshold(1e-06).
pattern_quality_metric_threshold(0.67).
mcda(weighted_sum, [0.0, 0]).
path_definition_file('example_data/path_definition.dslp').
