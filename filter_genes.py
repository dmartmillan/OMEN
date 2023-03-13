
def filter_genes_output(filter_genes, path_file, outdir):
    with open(outdir + "/" + path_file + ".probabilistic_network", 'r') as filedata:
        inputFilelines = filedata.readlines()
        with open(outdir + "/" + path_file + ".probabilistic_network", 'w') as filedata:
            for textline in inputFilelines:
                textline_spl = textline.split(",")
                if textline_spl[1].replace("\n","") not in set(filter_genes):
                    filedata.write(textline)
    
    with open(outdir + "/" + path_file + ".probabilistic_network.desc", 'r') as filedata:
        inputFilelines = filedata.readlines()
        with open(outdir + "/" + path_file + ".probabilistic_network.desc", 'w') as filedata:
            for textline in inputFilelines:
                if textline.replace("\n","") not in set(filter_genes):
                    filedata.write(textline)
