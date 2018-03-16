#!/usr/bin/python3

import sys
from collections import defaultdict

import numpy as np

totals_map = defaultdict()
exec_map = defaultdict()

for line in sys.stdin:
    info = [e.strip() for e in line.split(":")]

    # Mangled prefix, ignore it 
    if info[0] != '___':
        continue;

    # Store the total number of executable lines
    if info[2] == 'total':
        totals_map[info[1]] = info[3]
        exec_map[info[1]] = list(range(1, int(info[3]) + 1))

    # Tick off executed instructions
    if info[2] == 'exec':
        if int(info[3]) in exec_map[info[1]]:
            exec_map[info[1]].remove(int(info[3]))
        
   


for name in totals_map:

    percentage = ((int(totals_map[name]) - len(exec_map[name])) / int(totals_map[name])) * 100

    print(f"Method {name} has {percentage}% instruction coverage")

