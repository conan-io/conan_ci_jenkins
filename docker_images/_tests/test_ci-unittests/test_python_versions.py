import os
import re
import subprocess
import unittest

from packaging.version import VERSION_PATTERN, parse
from parameterized import parameterized


class CIUnittestsTestCase(unittest.TestCase):
    """ Check Python versions are available and environment variables """

    re_py_version = re.compile(r'^Python (\d+\.\d+\.\d+)')

    @parameterized.expand(["PY27", "2.7"], 
                          ["PY35", "3.5"], 
                          ["PY37", "3.7"], 
                          ["PY38", "3.8"], )
    def test_environment_variables(self, env_var, version):
        python_bin = os.environ[env_var]
        out, _ = subprocess.Popen([python_bin, '--version'], stdout=subprocess.PIPE, shell=False).communicate()
        m = self.re_py_version.match(out)
        v = parse(m.group(1))
        self.assertGreaterEqual(v, parse(version))
