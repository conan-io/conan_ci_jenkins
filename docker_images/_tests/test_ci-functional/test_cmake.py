import os
import re
import subprocess
import unittest

from packaging.version import VERSION_PATTERN, parse
from parameterized import parameterized


class CMakeTestCase(unittest.TestCase):
    """ Check CMake versions exist and work"""

    @parameterized.expand([("CMAKE_3_16_4", "3.16.4"),
                           ("CMAKE_3_16_3", "3.16.3"),])
    def test_versions(self, envvar, version):
        cmake_bin = os.environ[envvar]
        out, _ = subprocess.Popen([cmake_bin, '--version'], stdout=subprocess.PIPE, shell=False).communicate()
        first_line = out.splitlines()[0]
        self.assertEqual(first_line.decode().strip(), f'cmake version {version}')
