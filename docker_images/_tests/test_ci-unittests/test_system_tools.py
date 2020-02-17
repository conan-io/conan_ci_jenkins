import os
import re
import subprocess
import unittest


class SystemToolsTestCase(unittest.TestCase):
    """ This image doesn't have any system tool """

    def test_cmake_not_available(self):
        with self.assertRaisesRegex(FileNotFoundError, "No such file or directory: 'cmake'"):
            subprocess.Popen(['cmake',]).communicate()

    def test_git_not_available(self):
        with self.assertRaisesRegex(FileNotFoundError, "No such file or directory: 'git'"):
            subprocess.Popen(['git',]).communicate()

    def test_gcc_not_available(self):
        with self.assertRaisesRegex(FileNotFoundError, "No such file or directory: 'gcc'"):
            subprocess.Popen(['gcc',]).communicate()

    def test_clang_not_available(self):
        with self.assertRaisesRegex(FileNotFoundError, "No such file or directory: 'clang'"):
            subprocess.Popen(['clang',]).communicate()
