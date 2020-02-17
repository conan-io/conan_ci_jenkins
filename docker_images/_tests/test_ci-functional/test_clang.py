import os
import re
import subprocess
import unittest

from packaging.version import VERSION_PATTERN, parse
from parameterized import parameterized


class ClangTestCase(unittest.TestCase):
    """ Check Clang versions exist and work: clang-9 """

    @staticmethod
    def _grep_cplusplus(cmd):
        p1 = subprocess.Popen(cmd, stdout=subprocess.PIPE)
        p2 = subprocess.Popen(['grep', '-F', '__cplusplus'], stdin=p1.stdout, stdout=subprocess.PIPE)
        p1.stdout.close()  # Allow p1 to receive a SIGPIPE if p2 exits.
        out, _ = p2.communicate()
        return out.strip()

    def test_clang9_default(self):
        cmd = f"clang-9 -dM -E -x c++ /dev/null"
        out = self._grep_cplusplus(cmd.split())
        self.assertEqual(out.decode(), '#define __cplusplus 201402L')  # This is C++14

    @parameterized.expand([("-std=c++98", "199711L"),
                           ("-std=c++11", "201103L"),
                           ("-std=c++14", "201402L"),
                           ("-std=c++17", "201703L"),
                           ("-std=c++2a", "201707L"),])
    def test_clang9(self, std_flag, std_output):
        cmd = f"clang-9 -dM -E {std_flag} -x c++ /dev/null"
        out = self._grep_cplusplus(cmd.split())
        self.assertEqual(out.decode(), f'#define __cplusplus {std_output}')

    def test_clang7_default(self):
        cmd = f"clang-7 -dM -E -x c++ /dev/null"
        out = self._grep_cplusplus(cmd.split())
        self.assertEqual(out.decode(), '#define __cplusplus 201402L')  # This is C++14

    @parameterized.expand([("-std=c++98", "199711L"),
                           ("-std=c++11", "201103L"),
                           ("-std=c++14", "201402L"),
                           ("-std=c++17", "201703L"),
                           ("-std=c++2a", "201707L"),])
    def test_clang7(self, std_flag, std_output):
        cmd = f"clang-7 -dM -E {std_flag} -x c++ /dev/null"
        out = self._grep_cplusplus(cmd.split())
        self.assertEqual(out.decode(), f'#define __cplusplus {std_output}')
