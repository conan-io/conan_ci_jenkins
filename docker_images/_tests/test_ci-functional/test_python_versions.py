import os
import re
import subprocess
import unittest

from packaging.version import VERSION_PATTERN, parse
from parameterized import parameterized


class PythonVersionsTestCase(unittest.TestCase):
    """ Check Python versions are available and environment variables """

    re_py_version = re.compile(r'^Python (\d+\.\d+\.\d+)')

    def test_py2_envar(self):
        """ Look for PY27 env variable and check version """
        python_bin = os.environ["PY27"]
        out, _ = subprocess.Popen([python_bin, '-c', 'import sys; print(sys.version)'], stdout=subprocess.PIPE, shell=False).communicate()
        self.assertRegex(out.decode(), r'^2\.7\.\d+\s')

    @parameterized.expand([("PY35", (3, 5)),
                           ("PY37", (3, 7)),
                           ("PY38", (3, 8)),])
    def test_python_envvars(self, env_var, version):
        python_bin = os.environ[env_var]
        out, _ = subprocess.Popen([python_bin, '--version'], stdout=subprocess.PIPE, shell=False).communicate()
        m = self.re_py_version.match(out.decode())
        v = parse(m.group(1))
        self.assertEqual(v.release[0], version[0])
        self.assertEqual(v.release[1], version[1])
