#!/bin/bash

# Since this manipulates a lot of data in the working directory,
# it's imperative that only a single instance of this script is
# running at any time.
shopt -s extglob

# generate the experiment files
parameterFiles=($(swipl -g "generate_experiment(Experiment_File), write(Experiment_File), halt" -l experiment_generator.pl | tr -d '[]' | tr ',' ' '))

# The number of pieces in which to split each experiment.
# This number has to be at least as high (ideally equal) as the 
# number of cores/threds you wish to employ simultaneously
parts="$1"

# the archival directory storing the output and the experiment file
archivedir="$4"

# NOTE that originally experiment_generator.pl generated multiple experiments at once,
# hence the iteration here. In the last version, we've assumed only 1 set of parameters
# is processed at once, making this iteration redundant.
# For ease of use, we copy $element to the given filepath $2. If this iteration were to be reinstated,
# this would have to be changed.
for element in "${parameterFiles[@]}";
do
    # clean the working directory
    #rm -f *.pl.split*

    # split the experiment into $parts pieces
    swipl -g "split(${parts}, '${element}'), halt" -l experiment_splitter.pl
    split_output_files=()

    # generate a file containing the commands to be executed
    for split_file in ${element}.split+([0-9]);
    do
        split_output_files+=(\'"${split_file}".output\')
        test="'$split_file'"
        echo "consult(experiment_framework), generate_experiment("${test}"), halt" >> argfile
    done

    # run the commands in parallel
    #parallel --gnu --ungroup --joblog joblog --workdir $workdir --sshloginfile nodes2 --delay 1 "yap -z" :::: argfile
    parallel --progress --gnu --ungroup --delay 1 "yap -z " :::: argfile

    # merge the output into a single file
    split_output_files_list=$(IFS=,; echo "[${split_output_files[*]}]")
    # for some reason YAP ends up hanging on the findall/3, instead, stick to swipl
    swipl -g "consult(experiment_merger), merge(${split_output_files_list}, new_final_output_file), halt"
    #yap -z "consult(experiment_merger), merge(${split_output_files_list}), halt" > new_final_output_file
  
    # generate a directory to write the output in
    # mkdir -p $archivedir

    cp $element "$2"
    cp new_final_output_file "$3"
    
    mv new_final_output_file $archivedir
    mv $element $archivedir

    # clean the working directory
    for split_file in  ${element}.split+([0-9]);
    do
      	##echo 'not removing file'
        ##mv $split_file  ${unique_directory}/
        ##mv ${split_file}.output ${unique_directory}/
        rm $split_file
        rm ${split_file}.output
    done
    rm argfile
done

shopt -u extglob
