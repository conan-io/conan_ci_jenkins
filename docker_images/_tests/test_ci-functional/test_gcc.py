import os
import re
import subprocess
import unittest

from packaging.version import VERSION_PATTERN, parse
from parameterized import parameterized


class GCCTestCase(unittest.TestCase):
    """ Check GCC versions exist and work: GCC-7 and GCC-9 """

    def test_gcc9_default(self):
        cmd = f"g++-9 -dM -E -x c++ /dev/null | grep -F __cplusplus"
        out, _ = subprocess.Popen(cmd.split(), stdout=subprocess.PIPE, shell=False).communicate()
        self.assertEqual(out.decode(), '#define __cplusplus 201402L')  # This is C++14

    @parameterized.expand([("-std=c++98", "199711L"),
                           ("-std=c++11", "201103L"),
                           ("-std=c++14", "201402L"),
                           ("-std=c++17", "201703L"),
                           ("-std=c++2a", "201709L"),])
    def test_gcc9(self, std_flag, std_output):
        cmd = f"g++-9 -dM -E {std_flag} -x c++ /dev/null | grep -F __cplusplus"
        out, _ = subprocess.Popen(cmd.split(), stdout=subprocess.PIPE, shell=False).communicate()
        self.assertEqual(out.decode(), f'#define __cplusplus {std_output}')

    def test_gcc7_default(self):
        cmd = f"g++-7 -dM -E -x c++ /dev/null | grep -F __cplusplus"
        out, _ = subprocess.Popen(cmd.split(), stdout=subprocess.PIPE, shell=False).communicate()
        self.assertEqual(out.decode(), '#define __cplusplus 201402L')  # This is C++14

    @parameterized.expand([("-std=c++98", "199711L"),
                           ("-std=c++11", "201103L"),
                           ("-std=c++14", "201402L"),
                           ("-std=c++17", "201703L"),])
    def test_gcc7(self, std_flag, std_output):
        cmd = f"g++-7 -dM -E {std_flag} -x c++ /dev/null | grep -F __cplusplus"
        out, _ = subprocess.Popen(cmd.split(), stdout=subprocess.PIPE, shell=False).communicate()
        self.assertEqual(out.decode(), f'#define __cplusplus {std_output}')


