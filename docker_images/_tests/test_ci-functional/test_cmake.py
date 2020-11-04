import os
import subprocess
import unittest

from parameterized import parameterized


class CMakeTestCase(unittest.TestCase):
    """ Check CMake versions exist and work"""

    @parameterized.expand([("CMAKE_2_8_12", "2.8.12"),
                           ("CMAKE_3_7_2", "3.7.2"),
                           ("CMAKE_3_18_4", "3.18.4"),])
    def test_versions(self, envvar, version):
        cmake_bin = os.environ[envvar]
        out, _ = subprocess.Popen([cmake_bin, '--version'], stdout=subprocess.PIPE, shell=False).communicate()
        first_line = out.splitlines()[0]
        self.assertEqual(first_line.decode().strip(), f'cmake version {version}')
