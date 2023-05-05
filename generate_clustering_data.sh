#!/bin/bash

# generate graph file, cannot links file, and must links file
echo "Generating graph file, cannot links file, and must links file."
filtered_patterns_file=$1
number_of_nodes=$2
network_file=$4
cadd_file=$5


# Design project, put the files in the current directory instead of in /tmp
mkdir $3/clustering$1_$2
node_file=$(mktemp --tmpdir=$3/clustering$1_$2)

head -n ${number_of_nodes} $3'/'${filtered_patterns_file}'.probabilistic_network.desc' > "$node_file"
swipl -g "init_go('${network_file}', '${cadd_file}', '$3/${filtered_patterns_file}', '${node_file}'), halt" generate_clustering_data.pl

graph_file=${node_file}'_1'
cannot_links_file=${node_file}'_1_cannot_links'
must_links_file=${node_file}'_1_must_links'

printf "Top ranked genes: %s Cannot links: %s Must links: %s\n" "$2" "$(wc -l ${cannot_links_file})" "$(wc -l ${must_links_file} | awk '{ print $1 }')" >> $3/links_vs_N


