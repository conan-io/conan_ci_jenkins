import os
import re
import subprocess
import unittest

from packaging.version import VERSION_PATTERN, parse
from parameterized import parameterized

import pytest


@pytest.mark.outside
class OutsideDockerTests(unittest.TestCase):

    @pytest.mark.skipif(os.environ["IMAGE"] != "ci-unittests", reason="Tests only for image 'ci-unittests'")
    def test_unittests_image(self):
        out, _ = subprocess.Popen(['docker', 'run', os.environ["IMAGE"], '-c', 'cat /etc/lsb-release'], stdout=subprocess.PIPE, shell=False).communicate()
        self.assertIn("DISTRIB_ID=Ubuntu", out.decode())

    @pytest.mark.skipif(os.environ["IMAGE"] != "ci-functional", reason="Tests only for image 'ci-functional'")
    def test_functional_image(self):
        out, _ = subprocess.Popen(['docker', 'run', os.environ["IMAGE"], 'gcc', '7', 'clang', '9', 'cmake', '3.16.4', '-c', 'cat /etc/lsb-release'], stdout=subprocess.PIPE, shell=False).communicate()
        self.assertIn("DISTRIB_ID=Ubuntu", out.decode())
