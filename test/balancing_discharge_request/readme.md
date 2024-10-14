## Description
Simulates and tests the behaviour of an incoming jsonrpc discharge request by levl.
Contains five different types of expected files, as it depends on the load of the machine at what point in the simulation the request is made.

## OpenEMS Conficuration
### Battery:
- capacity: 10.000 Wh
- initial soc: 89 %

### Production:
3000 W

### Consumption:
- cycle 1: 863 W
- cycle 2: 769 W
- cycle 3: 688 W
- cycle 4: 624 W
- cycle 5: 580 W
- cycle 6: 552 W
- cycle 7: 536 W
- cycle 8: 524 W
- cycle 9: 513 W
- cycle 10: 502 W
- cycle 11: 492 W

## Request
discharge power: 6022 W
sellToGridLimit: 4200 W
-> puc controls grid to 0 W
-> levl can always discharge with 4200 W


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