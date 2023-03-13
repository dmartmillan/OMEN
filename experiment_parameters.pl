data('new_data/CADD.pl', 'new_data/coverage.pl').
network_file('new_data/network.pl').
threshold(1e-06).
pattern_quality_metric_threshold(0.67).
mcda(weighted_sum, [0.8, 0]).
path_definition_file('example_data/path_definition.dslp').
