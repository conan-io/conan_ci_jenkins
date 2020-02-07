import os
import platform
import sys
import uuid

from conf import (Extender, chdir, environment_append, get_environ,
                  linuxpylocation, macpylocation, win_msbuilds_logs_folder,
                  winpylocation)

pylocations = {"Windows": winpylocation,
               "Linux": linuxpylocation,
               "Darwin": macpylocation}[platform.system()]


def run_tests(module_path, pyver, source_folder, tmp_folder, flavor, excluded_tags, include_tags,
              num_cores=3, verbosity=None):

    verbosity = verbosity or (2 if platform.system() == "Windows" else 1)
    venv_dest = os.path.join(tmp_folder, "venv")
    if not os.path.exists(venv_dest):
        os.makedirs(venv_dest)
    venv_exe = os.path.join(venv_dest,
                            "bin" if platform.system() != "Windows" else "Scripts",
                            "activate")

    tags_str = []
    if excluded_tags:
        for tag in excluded_tags:
            tags_str.append("not {}".format(tag))
        tags_str = '-A "%s"' % " and ".join(tags_str)
    if include_tags:
        for tag in include_tags:
            tags_str.append("{}".format(tag))
        tags_str = '-A "%s"' % " or ".join(tags_str)

    pyenv = pylocations[pyver]
    source_cmd = "." if platform.system() != "Windows" else ""
    # Prevent OSX to lock when no output is received
    debug_traces = ""  # "--debug=nose,nose.result" if platform.system() == "Darwin" and pyver != "py27" else ""
    # pyenv = "/usr/local/bin/python2"
    multiprocess = ("--processes=%s --process-timeout=1000 "
                    "--process-restartworker --with-coverage" % num_cores) if platform.system() != "Darwin" else ""

    if num_cores <= 1:
        multiprocess = ""

    pip_installs = "pip install -r conans/requirements.txt && " \
                   "pip install -r conans/requirements_dev.txt && " \
                   "pip install -r conans/requirements_server.txt && "

    if platform.system() == "Darwin":
        pip_installs += "pip install -r conans/requirements_osx.txt && "

    traverse_namespace = ""
    if platform.system() == "Windows" and pyver == "py38":
        traverse_namespace = "--traverse-namespace"

    #  --nocapture
    command = "virtualenv --python \"{pyenv}\" \"{venv_dest}\" && " \
              "{source_cmd} \"{venv_exe}\" && " \
              "{pip_installs} " \
              "python setup.py install && " \
              "conan --version && conan --help && " \
              "nosetests {module_path} {tags_str} --verbosity={verbosity} " \
              "{multiprocess} " \
              "{debug_traces} " \
              "{traverse_namespace} " \
              "--with-xunit " \
              "&& codecov -t f1a9c517-3d81-4213-9f51-61513111fc28".format(
                                    **{"module_path": module_path,
                                       "pyenv": pyenv,
                                       "tags_str": tags_str,
                                       "venv_dest": venv_dest,
                                       "verbosity": verbosity,
                                       "venv_exe": venv_exe,
                                       "source_cmd": source_cmd,
                                       "debug_traces": debug_traces,
                                       "traverse_namespace": traverse_namespace,
                                       "multiprocess": multiprocess,
                                       "pip_installs": pip_installs})

    env = get_environ(tmp_folder)
    env["PYTHONPATH"] = source_folder
    env["CONAN_LOGGING_LEVEL"] = "50" if platform.system() == "Darwin" else "50"
    env["CHANGE_AUTHOR_DISPLAY_NAME"] = ""
    if flavor == "enabled_revisions":
        env["TESTING_REVISIONS_ENABLED"] = "True"
    if pyver.startswith("py2"):
        env["USE_UNSUPPORTED_CONAN_WITH_PYTHON_2"] = "True"
    # Related with the error: LINK : fatal error LNK1318: Unexpected PDB error; RPC (23) '(0x000006BA)'
    # More info: http://blog.peter-b.co.uk/2017/02/stop-mspdbsrv-from-breaking-ci-build.html
    # Update, this doesn't solve the issue, other issues arise:
    # LINK : fatal error LNK1101: incorrect MSPDB140.DLL version; recheck installation of this product
    #env["_MSPDBSRV_ENDPOINT_"] = str(uuid.uuid4())
    # Try to specify a known folder to keep there msbuild failure logs
    env["MSBUILDDEBUGPATH"] = win_msbuilds_logs_folder

    with chdir(source_folder):
        with environment_append(env):
            run(command)


def run(command):
    print("--CALLING: %s" % command)
    # return os.system(command)
    import subprocess

    # ret = subprocess.call("bash -c '%s'" % command, shell=True)
    shell = '/bin/bash' if platform.system() != "Windows" else None
    ret = subprocess.call(command, shell=True, executable=shell)
    if ret != 0:
        raise Exception("Error running: '%s'" % command)


if __name__ == "__main__":

    import argparse

    parser = argparse.ArgumentParser(description='Launch tests in a venv')
    parser.add_argument('module', help='e.g.: conans.test')
    parser.add_argument('pyver', help='e.g.: py27')
    parser.add_argument('source_folder', help='Folder containing the conan source code')
    parser.add_argument('tmp_folder', help='Folder to create the venv inside')
    parser.add_argument('--include_tags', '-i', nargs=1, action=Extender,
                        help='Tags to test e.g.: rest_api')
    parser.add_argument('--num_cores', type=int, help='Number of cores to use', default=3)
    parser.add_argument('--exclude_tags', '-e', nargs=1, action=Extender,
                        help='Tags to exclude from testing, e.g.: rest_api')
    parser.add_argument('--flavor', '-f', help='enabled_revisions (or nothing)')
    args = parser.parse_args()

    if args.include_tags and args.exclude_tags:
        raise Exception("exclude_tags and include_tags cannot be used at the same time")

    run_tests(args.module, args.pyver, args.source_folder, args.tmp_folder, args.flavor,
              args.exclude_tags, args.include_tags, num_cores=args.num_cores)
