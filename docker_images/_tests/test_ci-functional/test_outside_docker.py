import unittest
import pytest


@pytest.mark.outside
class OutsideDockerTests(unittest.TestCase):
    def test_entrypoint(self):
        self.fail("Not expected to run")
