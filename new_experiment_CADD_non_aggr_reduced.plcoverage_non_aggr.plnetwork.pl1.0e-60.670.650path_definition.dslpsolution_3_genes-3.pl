gene_patient_probability_file('new_data/CADD_non_aggr_reduced.pl').
path_definition_file('example_data/path_definition.dslp').
gene_function_file('new_data/coverage_non_aggr.pl').
network_file('new_data/network.pl').
threshold(1.0e-6).
pattern_quality_metric_threshold(0.67).
mcda(weighted_sum,[0.65,0]).
experiment_query(solution_3_genes(mcda(weighted_sum,[alpha(0.65),beta(0)]),0.67,_3324)).
