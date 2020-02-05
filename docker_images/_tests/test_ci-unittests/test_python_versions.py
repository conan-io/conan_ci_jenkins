import os
import re
import subprocess
import unittest

from packaging.version import VERSION_PATTERN, parse
from parameterized import parameterized


class CIUnittestsTestCase(unittest.TestCase):
    """ Check Python versions are available and environment variables """

    re_py_version = re.compile(r'^Python (\d+\.\d+\.\d+)')

    @parameterized.expand([("PY27", (2, 7)),
                           ("PY35", (3, 5)),
                           ("PY37", (3, 7)),
                           ("PY38", (3, 8)),])
    def test_python_envvars(self, env_var, version):
        python_bin = os.environ[env_var]
        out, _ = subprocess.Popen([python_bin, '--version'], stdout=subprocess.PIPE, shell=False).communicate()
        m = self.re_py_version.match(out.decode())
        v = parse(m.group(1))
        self.assertEqual(v.release[0], version[0])
        self.assertEqual(v.release[1], version[1])

    def test_cmake_not_available(self):
        with self.assertRaisesRegex(FileNotFoundError, "No such file or directory: 'cmake'"):
            subprocess.Popen(['cmake',]).communicate()

    def test_git_not_available(self):
        with self.assertRaisesRegex(FileNotFoundError, "No such file or directory: 'git'"):
            subprocess.Popen(['git',]).communicate()

    def test_gcc_not_available(self):
        with self.assertRaisesRegex(FileNotFoundError, "No such file or directory: 'gcc'"):
            subprocess.Popen(['gcc',]).communicate()
