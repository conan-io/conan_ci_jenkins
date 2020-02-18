import os
import re
import subprocess
import unittest

from packaging.version import VERSION_PATTERN, parse
from parameterized import parameterized

import pytest


@pytest.mark.outside
class OutsideDockerTests(unittest.TestCase):
    docker_image = f'{os.environ["DOCKER_USERNAME"]}/{os.environ["IMAGE"]}'

    def test_functional_image(self):
        out, _ = subprocess.Popen(['docker', 'run', self.docker_image, 'gcc', '7', 'clang', '9', 'cmake', '3.16.4', '-c', 'cat /etc/lsb-release'], stdout=subprocess.PIPE, shell=False).communicate()
        self.assertIn("DISTRIB_ID=Ubuntu", out.decode())

    @parameterized.expand([("7", ), ("9", ), ])
    def test_change_gcc_version(self, gcc_version):
        out, _ = subprocess.Popen(['docker', 'run', self.docker_image, 'gcc', gcc_version, 'clang', '9', 'cmake', '3.16.4', '-c', 'gcc -dumpversion'], stdout=subprocess.PIPE, shell=False).communicate()
        last_line = out.splitlines()[-1]
        self.assertEqual(last_line.decode().strip(), gcc_version)

    @parameterized.expand([("7", "7.0.1-9build1 (tags/RELEASE_701/final)"), 
                           ("9", "9.0.0-2 (tags/RELEASE_900/final)"), ])
    def test_change_clang_version(self, clang_version, expected):
        out, _ = subprocess.Popen(['docker', 'run', self.docker_image, 'gcc', '7', 'clang', clang_version, 'cmake', '3.16.4', '-c', 'clang --version'], stdout=subprocess.PIPE, shell=False).communicate()
        self.assertIn(f'clang version {expected}', out.decode())

    @parameterized.expand([("3.16.4", ), ("3.16.3", ), ])
    def test_change_cmake_version(self, cmake_version):
        out, _ = subprocess.Popen(['docker', 'run', self.docker_image, 'gcc', '7', 'clang', '9', 'cmake', cmake_version, '-c', 'cmake --version'], stdout=subprocess.PIPE, shell=False).communicate()
        self.assertIn(f'cmake version {cmake_version}', out.decode())
