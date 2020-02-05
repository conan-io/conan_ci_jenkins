import unittest
import os
import subprocess


class CIUnittestsTestCase(unittest.TestCase):
    """ Check Python versions are available and environment variables """
    
    def test_py27(self):
        python_bin = os.environ["PY27"]
        out, _ = subprocess.Popen([python_bin, '--version'], stdout=subprocess.PIPE, shell=False).communicate()
        self.assertTrue(out.startswith("Python 2.7"))

    def test_py35(self):
        python_bin = os.environ["PY35"]
        out, _ = subprocess.Popen([python_bin, '--version'], stdout=subprocess.PIPE, shell=False).communicate()
        self.assertTrue(out.startswith("Python 3.5"))

    def test_py37(self):
        python_bin = os.environ["PY37"]
        out, _ = subprocess.Popen([python_bin, '--version'], stdout=subprocess.PIPE, shell=False).communicate()
        self.assertTrue(out.startswith("Python 3.7"))

    def test_py38(self):
        python_bin = os.environ["PY38"]
        out, _ = subprocess.Popen([python_bin, '--version'], stdout=subprocess.PIPE, shell=False).communicate()
        self.assertTrue(out.startswith("Python 3.8"))

