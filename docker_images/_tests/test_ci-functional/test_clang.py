import os
import re
import subprocess
import unittest

from packaging.version import VERSION_PATTERN, parse
from parameterized import parameterized

clangxx_7 = os.environ.get("CLANGXX_7", None)
clang_7 = os.environ.get("CLANG_7", None)
clangxx_9 = os.environ.get("CLANGXX_9", None)
clang_9 = os.environ.get("CLANG_9", None)


class ClangTestCase(unittest.TestCase):
    """ Check Clang versions exist and work: clang-9 """

    def test_clang7_versions(self):
        expected = "7.0.1"
        for it in [clangxx_7, clang_7]:
            out, _ = subprocess.Popen([it, '--version'], stdout=subprocess.PIPE).communicate()
            first_line = out.splitlines()[0]
            # clang version 7.0.1-9build1 (tags/RELEASE_701/final)
            self.assertEqual(first_line.decode(), f'clang version {expected}-9build1 (tags/RELEASE_701/final)')

    def test_gcc9_versions(self):
        # Clang 9 returns the proper version for '-dumpversion' command
        expected = "9.0.0"
        for it in [clangxx_9, clang_9]:
            out, _ = subprocess.Popen([it, '-dumpversion'], stdout=subprocess.PIPE).communicate()
            self.assertEqual(out.decode().strip(), expected)

    @staticmethod
    def _grep_cplusplus(cmd):
        p1 = subprocess.Popen(cmd, stdout=subprocess.PIPE)
        p2 = subprocess.Popen(['grep', '-F', '__cplusplus'], stdin=p1.stdout, stdout=subprocess.PIPE)
        p1.stdout.close()  # Allow p1 to receive a SIGPIPE if p2 exits.
        out, _ = p2.communicate()
        return out.strip()

    def test_clang9_default(self):
        cmd = f"{clang_9} -dM -E -x c++ /dev/null"
        out = self._grep_cplusplus(cmd.split())
        self.assertEqual(out.decode(), '#define __cplusplus 201402L')  # This is C++14

    @parameterized.expand([("-std=c++98", "199711L"),
                           ("-std=c++11", "201103L"),
                           ("-std=c++14", "201402L"),
                           ("-std=c++17", "201703L"),
                           ("-std=c++2a", "201707L"),])
    def test_clang9(self, std_flag, std_output):
        cmd = f"{clang_9} -dM -E {std_flag} -x c++ /dev/null"
        out = self._grep_cplusplus(cmd.split())
        self.assertEqual(out.decode(), f'#define __cplusplus {std_output}')

    def test_clang7_default(self):
        cmd = f"{clang_7} -dM -E -x c++ /dev/null"
        out = self._grep_cplusplus(cmd.split())
        self.assertEqual(out.decode(), '#define __cplusplus 201402L')  # This is C++14

    @parameterized.expand([("-std=c++98", "199711L"),
                           ("-std=c++11", "201103L"),
                           ("-std=c++14", "201402L"),
                           ("-std=c++17", "201703L"),
                           ("-std=c++2a", "201707L"),])
    def test_clang7(self, std_flag, std_output):
        cmd = f"{clang_7} -dM -E {std_flag} -x c++ /dev/null"
        out = self._grep_cplusplus(cmd.split())
        self.assertEqual(out.decode(), f'#define __cplusplus {std_output}')
