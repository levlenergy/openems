## Description
Simulation of a peak shaving scenario where the battery should discharge for levl and levl is not allowed to influence the sell to grid.
Contains five different types of expected files, as it depends on the load of the machine at what point in the simulation the request is made.

## OpenEMS Conficuration
Peak shaving power: 1000 W
Recharge power: 500 W

### Production:
0 W

### Consumption:
- cycle 1: 400 W
- cycle 2: 500 W
- cycle 3: 900 W
- cycle 4: 1000 W
- cycle 5: 1100 W
- cycle 6: 400 W
- cycle 7: 500 W
- cycle 8: 900 W
- cycle 9: 1000 W
- cycle 10: 1100 W
- cycle 11: 400 W

## Request
discharge power: 1000 W
sellToGridLimit: 0 W


## Scenarios

Request received after ...
- ... cycle 3
  - starts at cycle 5
  - expected1 (cycle 5-7)
- ... cycle 4
  - starts at cycle 6
  - expected2 (cycle 6-8)
- ... cycle 5
  - starts at cycle 7
  - expected3 (cycle 7-9)
- ... cycle 6
  - starts at cycle 8
  - expected4 (cycle 8-10)
- ... cycle 7
  - starts at cycle 9
  - expected5 (cycle 9-11)