## Description
This test ensures that the Levl SoC is overwritten by the Levl request

(while adding the energy already realized within the current request cycle).

## OpenEMS Conficuration

- Battery capacity: 10kWh
- Current soc: 10% --> 1kWh
- Max grid power: 10kW


- Current cycle discharge energy: 360.000 Ws (=100 Wh)
- Current cycle realized discharge energy: 3.600 Ws (=1 Wh)
- Current cycle remaining discharge energy: 356.400 Ws (=90 Wh)


- Total realized discharge energy (inverted Levl SoC): -1.000Wh

--> Levl  has charged 1.000Wh on balance, thus all the energy in the battery is reserved for Levl, PUC must not use this energy

### Production:
1000 W

### Consumption:
8000 W

### -> Energy demand:
7000 W

## Request
New Levl SoC: 900Wh (total realized discharge energy: -900Wh)

## Desired behaviour
At first, Levl charges the battery. Thus, total realized energy decreases.
Primary use case cannot discharge because all energy is reserved for Levl.
After request is received, levl Soc is updated to a lower value. As a result, the primary use case can discharge now.


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
  - expected5 (cycle 9-11