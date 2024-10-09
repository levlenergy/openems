#!/bin/bash

# listen to ctrl-c
trap ctrl_c INT

TOTAL_COUNT=0
SUCCESS_COUNT=0
FAIL_COUNT=0

FAILING_TESTS=

OPENEMS_EXECUTION_TIME_SECONDS=14

FULL_OUTPUT_NAME="full.out"
LEVL_OUTPUT_NAME="levl.out"

OPENEMS_URL=http://localhost:8085/jsonrpc
OPENEMS_WAIT_BEFORE_CURL_SECONDS=8

function ctrl_c {
  kill -INT -$pid
  exit
}

function cleanup {
  find -name "$FULL_OUTPUT_NAME" | xargs rm
  find -name "$LEVL_OUTPUT_NAME" | xargs rm
}

function run() {
  testName=$1
  configDir=$2

  echo -e "\nRUNNING TEST $testName ($OPENEMS_EXECUTION_TIME_SECONDS seconds)"
  timeout $OPENEMS_EXECUTION_TIME_SECONDS java -Dfelix.cm.dir=$(pwd)/$configDir -jar ../../build/openems-edge.jar >$FULL_OUTPUT_NAME &
  pid=$!
  wait $pid
  cat $FULL_OUTPUT_NAME | grep -Ea 'remaining|levlWorkflow|levl soc' | sed -e 's/.*\(levlWorkflow0.*\).*/\1/' -e 's/.*\(remaining.*\).*/\1/' -e 's/.*\(levl soc.*\).*/\1/' > $LEVL_OUTPUT_NAME
  sleep 2
}

function runWithRequest() {
  testName=$1
  configDir=$2
  levlRequest=$3
  echo -e "\nRUNNING TEST $testName with request $levlRequest ($OPENEMS_EXECUTION_TIME_SECONDS seconds)"
  echo $(pwd)
  timeout $OPENEMS_EXECUTION_TIME_SECONDS java -Dfelix.cm.dir=$(pwd)/$configDir -jar ../../build/openems-edge.jar >$FULL_OUTPUT_NAME & pid=$!
  sleep $OPENEMS_WAIT_BEFORE_CURL_SECONDS
  curl --location --connect-timeout 15 --request POST "$OPENEMS_URL" --header 'Authorization: Basic YWRtaW46YWRtaW4=' --header 'Content-Type: application/json' -d @$levlRequest
  echo
  wait $pid
  cat $FULL_OUTPUT_NAME | grep -Ea 'remaining|levlWorkflow|levl soc' | sed -e 's/.*\(levlWorkflow0.*\).*/\1/' -e 's/.*\(remaining.*\).*/\1/' -e 's/.*\(levl soc.*\).*/\1/' > $LEVL_OUTPUT_NAME
  sleep 2
}

function runAndLogOutput() {
  configDir=$1
  run $configDir
  cat $LEVL_OUTPUT_NAME
}

function check {
  testName=$1
  levlRequest=$2

  tempConfigDir="run_config"
  if [ ! -d $testName ]; then
    echo "ERROR: test dir $testName does not exist"
    exit 1
  fi
  if [ ! -d $testName/config ]; then
    echo "ERROR: config dir $testName/config does not exist"
    exit 1
  fi

  cd $testName

  rm -rf $tempConfigDir
  cp -r config $tempConfigDir

  if [ -z "$levlRequest" ]; then
    run $testName $tempConfigDir
    expected_files=("expected")
  else
    runWithRequest $testName $tempConfigDir $levlRequest
    expected_files=("expected1" "expected2" "expected3" "expected4" "expected5")
  fi

  TOTAL_COUNT=$((TOTAL_COUNT + 1))

  validLines=0
  expectedFilesMatched=false
  missingLine=
  outputIfFailed=

  # Wenn expected files nicht vorhanden sind, failed der testcase
  for expected_file in "${expected_files[@]}"; do
    if [ ! -f $expected_file ]; then
        echo "$testName: ERROR: missing expected file $expected_file"
        break
    fi
    echo "validate logs against file \"$expected_file\""
    expectedFileMatched=true

    # expected file Zeile für Zeile einlesen
    while read -r line; do
      # leere Zeilen überspringen
      if [ -z "$line" ]; then
        continue
      fi
      # '\r' aus der Zeile entfernen
      line=$(echo $line | tr -d '\r')

      validLines=$((validLines + 1))

      # Überprüfen, ob die Zeile in der Ausgabedatei vorhanden ist
      if ! grep -Fqa "$line" $LEVL_OUTPUT_NAME; then
        # Wenn die Zeile nicht gefunden wird, speichern wir die Fehler zwischen und brechen die Schleife ab
        missingLine="    $line (part of expected file $expected_file)"
        outputIfFailed=$(cat $LEVL_OUTPUT_NAME | sed -e 's/^/    /')
        expectedFileMatched=false
        break
      fi
    done <$expected_file

    if [ "$expectedFileMatched" = true ]; then
      echo "$testName: log matched with file \"$expected_file\""
      expectedFilesMatched=true
      break
    fi
  done

  cd ..

  # Wenn kein expected File gematched hat
  if [ "$expectedFilesMatched" = false ]; then
    echo "$testName: ERROR: missing in $LEVL_OUTPUT_NAME:"
    echo "$missingLine"
    echo "actual output:"
    echo "$outputIfFailed"
    FAIL_COUNT=$((FAIL_COUNT + 1))
    FAILING_TESTS="$FAILING_TESTS\n$testName"
    return
  fi
  # Wenn keine gültigen Zeilen gefunden wurden
  if [ $validLines -eq 0 ]; then
    echo "$testName: ERROR: no expected lines $expected"
    FAIL_COUNT=$((FAIL_COUNT + 1))
    return
  fi

  # Wenn der Test erfolgreich war
  echo "$testName: SUCCESS"
  SUCCESS_COUNT=$((SUCCESS_COUNT + 1))
}

function suiteCi() {
  OPENEMS_EXECUTION_TIME_SECONDS=16
  OPENEMS_WAIT_BEFORE_CURL_SECONDS=8
  suite
}

function suite() {

    check balancing_charge_capacity_reserved_primary_use_case_should_not_charge
    check balancing_charge_capacity_reserved_levl_discharges_further_primary_use_case_should_not_charge
    check balancing_charge_capacity_reserved_levl_use_case_may_charge
    check balancing_charge_capacity_reserved_primary_use_case_may_charge
    check balancing_discharge_capacity_reserved_levl_charges_further_primary_use_case_should_not_discharge
    check balancing_discharge_capacity_reserved_primary_use_case_should_not_discharge
    check balancing_discharge_capacity_reserved_levl_use_case_may_discharge
    check balancing_discharge_capacity_reserved_primary_use_case_may_discharge

    check balancing_discharge_request ../levlDischargeRequest.json
    check balancing_charge_request ../levlChargeRequest.json
    check balancing_avoid_integer_overflow_with_large_discharge_request ../levlLargeDischargeRequest.json
    check symm_peakshaving_charge_request_forbid_influence_sell_to_grid ../levlChargeRequestPeakshavingForbidInfluenceSellToGrid.json
    check symm_peakshaving_discharge_request_forbid_influence_sell_to_grid ../levlDischargeRequestPeakshavingForbidInfluenceSellToGrid.json
    check balancing_charge_request_forbid_influence_sell_to_grid ../levlChargeRequestForbidInfluenceSellToGrid.json
    check balancing_discharge_request_forbid_influence_sell_to_grid ../levlDischargeRequestForbidInfluenceSellToGrid.json
    check balancing_discharge_request_update_levlsoc_primary_use_case_may_discharge ../levlDischargeRequestUpdatedLevlSoc.json

    check balancing_respect_grid_limits_levldischarge
    check balancing_respect_levl_lower_soc_limit_levlcharge
    check balancing_respect_levl_lower_soc_limit_levldischarge
    check balancing_respect_levl_upper_soc_limit_levlcharge
    check balancing_respect_levl_upper_soc_limit_levldischarge
    check symm_peakshaving_peakshaving_levlcharge
    check symm_peakshaving_peakshaving_levldischarge
    check symm_peakshaving_recharge_levlcharge
    check symm_peakshaving_recharge_levldischarge

  echo "###############"
  echo "TOTAL: $TOTAL_COUNT"
  echo "SUCCESS: $SUCCESS_COUNT"
  echo "FAIL: $FAIL_COUNT"
  if [ $FAIL_COUNT -gt 0 ]; then
    echo -e "\nFAILING TESTS:$FAILING_TESTS"
    exit 1
  fi
}

case $1 in
"suite")
  suite
  ;;
"suiteCi")
  suiteCi
  ;;
"clean")
  cleanup
  ;;
"check")
  check $2
  ;;
"checkWithRequest")
  check $2 $3
  ;;
"run")
  runAndLogOutput $2
  ;;
*)
  echo "Usage: $0 ( suite | clean | check expected_<testname> | run <configdir> )"
  exit 1
  ;;
esac
