#!/bin/bash

# listen to ctrl-c
trap 'kill -INT -$pid' INT

TOTAL_COUNT=0
SUCCESS_COUNT=0
FAIL_COUNT=0

FAILING_TESTS=

OPENEMS_EXECUTION_TIME_SECONDS=14

EXPECTED_FILENAME_LOCAL=expected
EXPECTED_FILENAME_CI=expectedci
FULL_OUTPUT_NAME="full.out"
LEVL_OUTPUT_NAME="levl.out"

OPENEMS_URL=http://127.0.0.1:8084/jsonrpc
EXPECTED_FILENAME=$EXPECTED_FILENAME_LOCAL
OPENEMS_WAIT_BEFORE_CURL_SECONDS=8

function extractTestName() {
  withoutConfig=${1/config_/}
  withoutExpected=${withoutConfig/$EXPECTED_FILENAME/}
  echo $withoutExpected
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
  cat $FULL_OUTPUT_NAME | grep -a levlWorkflow0 | sed -e 's/.*\(levlWorkflow0.*\).*/\1/' >$LEVL_OUTPUT_NAME
  sleep 2
}

function runWithRequest() {
  testName=$1
  configDir=$2
  levlRequest=$3
  echo -e "\nRUNNING TEST $testName with request $levlRequest ($OPENEMS_EXECUTION_TIME_SECONDS seconds)"
  echo $(pwd)
  timeout $OPENEMS_EXECUTION_TIME_SECONDS java -Dfelix.cm.dir=$(pwd)/$configDir -jar ../../build/openems-edge.jar >$FULL_OUTPUT_NAME &
  pid=$!
  sleep $OPENEMS_WAIT_BEFORE_CURL_SECONDS
  curl --location --connect-timeout 15 --request POST "$OPENEMS_URL" --header 'Authorization: Basic YWRtaW46YWRtaW4=' --header 'Content-Type: application/json' -d @$levlRequest
  echo
  wait $pid
  cat $FULL_OUTPUT_NAME | grep -Ea 'remaining|levlWorkflow' | sed -e 's/.*\(levlWorkflow0.*\).*/\1/' | sed -e 's/.*\(remaining.*\).*/\1/' > $LEVL_OUTPUT_NAME
  cat $FULL_OUTPUT_NAME
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

  success=0
  fail=0

  expected="$EXPECTED_FILENAME"
  if [ -z "$levlRequest" ]; then
    run $testName $tempConfigDir
  else
    runWithRequest $testName $tempConfigDir $levlRequest
  fi

  TOTAL_COUNT=$((TOTAL_COUNT + 1))

  validLines=0
  error=0

  if [ ! -f $expected ]; then
    echo "creating new dummy expectation file $expected"
    echo "NEW EXPECTATION FILE" >$expected
  fi

  while read -r line; do
    if [ -z "$line" ]; then
      continue
    fi
    line=$(echo $line | tr -d '\r')
    validLines=$((validLines + 1))
    if ! grep -Fqa "$line" $LEVL_OUTPUT_NAME; then
      echo "$testName: ERROR: missing in $LEVL_OUTPUT_NAME:"
      echo "    $line"
      echo "actual output:"
      cat $LEVL_OUTPUT_NAME | sed -e 's/^/    /'
      echo
      fail=$((fail + 1))
      error=1
      break
    fi
  done <$expected

  cd ..

  if [ $error -gt 0 ]; then
    FAIL_COUNT=$((FAIL_COUNT + 1))
    FAILING_TESTS="$FAILING_TESTS\n$testName"
    return
  fi
  if [ $validLines -eq 0 ]; then
    echo "$testName: ERROR: no expected lines $expected"
    FAIL_COUNT=$((FAIL_COUNT + 1))
    return
  fi

  echo "$testName: SUCCESS"
  SUCCESS_COUNT=$((SUCCESS_COUNT + 1))
}

function suiteCi() {
  EXPECTED_FILENAME=$EXPECTED_FILENAME_CI
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
"run")
  runAndLogOutput $2
  ;;
*)
  echo "Usage: $0 ( suite | clean | check expected_<testname> | run <configdir> )"
  exit 1
  ;;
esac
