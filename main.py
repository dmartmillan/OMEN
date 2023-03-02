
import argparse
import subprocess
import sys
import datetime
import os
import shutil

#
# A straightforward interface to interacting with OMEN. By default all parameters have been set to repeat the original experiment
# thus given that all required software has been installed (see README.md), the original experiment can be repeated by calling
#
# $ python3 main.py
#
# (if the execution time takes too long, the original threshold probability of 10**(-6) can be changed to e.g. 2*10**(-5))
# Note also the importance of setting a sensible value for the parameter -cores (default=7) as much of the computational
# load can be parallellized
#
# Using the provided parameters, experiments on your own data can be performed. Note however that for the data:
# (cadd_file, coverage_file and network_file) you should provide the required set of predicates in its proper format as outlined in README.md
#
# Modifying the path_definition_file is provided as a possibility, but should be only considered by experts, as this file is effectively
# a core component of OMEN.
#

# PARSE ARGUMENTS

parser = argparse.ArgumentParser(description="OMEN")
# NOTE that valid filepaths should not contain single quotes
parser.add_argument("cadd_file", type=str, nargs='?', help="CADD probabilities .pl filepath", default='example_data/tokheim_pancancer_somatic_CADD.pl')
parser.add_argument("coverage_file", type=str, nargs='?', help="Coverage .pl filepath", default='example_data/tokheim_pancancer_somatic_coverage_ranks.pl')
parser.add_argument("network_file", type=str, nargs='?', help="Network filepath", default='example_data/network.pl')
parser.add_argument("-path_definition_file", type=str, nargs='?', help="file specifying the path distribution", default='example_data/path_definition.dslp')

parser.add_argument("-pattern_quality_threshold", nargs='?', type=float, help="float specifying the quality a pattern has to exceed to be considered", default=0.67)
parser.add_argument("-threshold_probability", nargs='?', type=str, help="math expression for the SLP threshold", default='10**(-6)')
parser.add_argument("-alpha", type=float, nargs='?', help="value between 0 and 1 managing trade-off between the mutex term and the gene_freq term", default=0.8)

parser.add_argument("-cores", type=str, nargs='?', help="number of CPU threads to employ", default=10)


parser.add_argument("-step", type=int, nargs='?', help="step to execute OMEN", default=0)
parser.add_argument("-outlast", type=str, nargs='?', help="output folder where continue OMEN run")


args = parser.parse_args()
original_stdout = sys.stdout

outdir = "output/" + datetime.datetime.now().strftime("%d_%m_%Y.%H_%M_%s")
outlast = str(args.outlast)
step = args.step

os.mkdir(outdir)

# GENERATE PARAMETERS FILE FOR PATTERN COLLECTION 
print("GENERATE PARAMETERS FILE FOR PATTERN COLLECTION step")
with open('experiment_parameters.pl', 'w') as f:
    sys.stdout = f
    print("data('{}', '{}').".format(args.cadd_file, args.coverage_file))
    print("network_file('{}').".format(args.network_file))
    print("threshold({}).".format(eval(args.threshold_probability)))
    print("pattern_quality_metric_threshold({}).".format(args.pattern_quality_threshold))
    print("mcda(weighted_sum, [{}, 0]).".format(args.alpha))
    print("path_definition_file('{}').".format(args.path_definition_file))
    sys.stdout = original_stdout

# Only run a step if necessary because can take a lot of time (Design Project)

# STEP 1
# PERFORM PATTERN COLLECTION
print("STEP 1: PATTERN COLLECTION step")
if (step <= 1):
    subprocess.call(['./pattern_collection.sh', str(args.cores), outdir + "/experiment_file_1", outdir + "/output_file_1", outdir])
else:
    shutil.copy(outlast + "/experiment_file_1", outdir + "/experiment_file_1")
    shutil.copy(outlast + "/output_file_1", outdir + "/output_file_1")

# STEP 2
# EVALUATE PATHS
print("STEP 2: EVALUATE PATHS step")
if (step <= 2):
    subprocess.call(['./evaluate_paths.sh', outdir + "/experiment_file_1", outdir + "/output_file_1"])
else:
    shutil.copy(outlast + "/experiment_file_1", outdir + "/experiment_file_1")
    shutil.copy(outlast + "/output_file_1", outdir + "/output_file_1")


# STEP 3
# FILTER PATTERNS
print("STEP 3: FILTER PATTERNS step")
if (step <= 3):
    subprocess.call(['./filter_patterns.sh', outdir + "/output_file_1.evaluated", str(args.pattern_quality_threshold)])
else:
    shutil.copy(outlast + "/output_file_1.evaluated", outdir + "/output_file_1.evaluated")

# STEP 4
# GENERATE PROBABILISTIC NETWORK
print("STEP 4: GENERATE PROBABILISTIC NETWORK step")
if (step <= 4):
    subprocess.call(['./probabilistic_network.sh', outdir + "/output_file_1.evaluated.filtered_" + str(args.pattern_quality_threshold)])
else:
    shutil.copy(outlast + "/output_file_1.evaluated.filtered_" + str(args.pattern_quality_threshold), outdir + "/output_file_1.evaluated.filtered_" + str(args.pattern_quality_threshold))

# STEP 5
# GENERATE RANKING
print("STEP 5: GENERATE RANKING step")
if (step <= 5):
    subprocess.call(['./ranking.sh', outdir + "/output_file_1.evaluated.filtered_" + str(args.pattern_quality_threshold) + '.probabilistic_network', args.network_file])
else:
    shutil.copy(outlast + "/output_file_1.evaluated.filtered_" + str(args.pattern_quality_threshold) + '.probabilistic_network', outdir + "/output_file_1.evaluated.filtered_" + str(args.pattern_quality_threshold) + '.probabilistic_network')
    shutil.copy(outlast + "/output_file_1.evaluated.filtered_" + str(args.pattern_quality_threshold) + '.probabilistic_network.desc', outdir + "/output_file_1.evaluated.filtered_" + str(args.pattern_quality_threshold) + '.probabilistic_network.desc')



# We added a new subprocess (Design Project)
# New dataset --> change paths to CADD and network file manually in the generate_clustering_data.sh

# STEP 6
# CLUSTERING 
print("STEP 6: CLUSTERING step")
N = [50,80,100,120,150,200]
if (step <= 6):
    subprocess.call(['./generate_file_to_keep_counts_of_links_in_function_of_N.sh',outdir])
    for n in N:
        subprocess.call(['./generate_clustering_data.sh', "output_file_1.evaluated.filtered_" + str(args.pattern_quality_threshold),str(n),outdir])
else:
    shutil.copytree(outlast + "/links_vs_N", outdir + "/links_vs_N")
