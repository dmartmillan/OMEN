
for i in 0.2 0.5 0.65 0.8 0.9
do
	python main.py -alpha $i > outputfile_$i 2>&1& 
done
