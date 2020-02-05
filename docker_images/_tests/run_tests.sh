echo "Running tests using $PYTHON"
$PYTHON -m pip install -U pip
$PYTHON -m pip install pytest parameterized
$PYTHON -m pytest /home/tests/test_$IMAGE/
