echo "Running tests"
$PY_DEFAULT -m pip install pytest
$PY_DEFAULT -m pytest test_$IMAGE/
