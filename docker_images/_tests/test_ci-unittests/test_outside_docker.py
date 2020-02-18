import os
import re
import subprocess
import unittest

from packaging.version import VERSION_PATTERN, parse

import pytest


@pytest.mark.outside
class OutsideDockerTests(unittest.TestCase):

    def test_unittests_image(self):
        out, _ = subprocess.Popen(['docker', 'run', 'conanio/' + os.environ["IMAGE"], '-c', 'cat /etc/lsb-release'], stdout=subprocess.PIPE, shell=False).communicate()
        self.assertIn("DISTRIB_ID=Ubuntu", out.decode())
