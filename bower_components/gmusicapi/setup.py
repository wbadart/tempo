#!/usr/bin/env python
# -*- coding: utf-8 -*-

import re
from setuptools import setup, find_packages
import sys

# Only 2.7 is supported.
if not ((2, 7, 0) <= sys.version_info[:3] < (2, 8)):
    sys.stderr.write('gmusicapi does not officially support this Python version.\n')
    # try to continue anyway

dynamic_requires = []

# This hack is from http://stackoverflow.com/a/7071358/1231454;
# the version is kept in a seperate file and gets parsed - this
# way, setup.py doesn't have to import the package.

VERSIONFILE = 'gmusicapi/_version.py'

version_line = open(VERSIONFILE).read()
version_re = r"^__version__ = ['\"]([^'\"]*)['\"]"
match = re.search(version_re, version_line, re.M)
if match:
    version = match.group(1)
else:
    raise RuntimeError("Could not find version in '%s'" % VERSIONFILE)

setup(
    name='gmusicapi',
    version=version,
    author='Simon Weber',
    author_email='simon@simonmweber.com',
    url='http://pypi.python.org/pypi/gmusicapi/',
    packages=find_packages(),
    scripts=[],
    license=open('LICENSE').read(),
    description='An unofficial api for Google Play Music.',
    long_description=(open('README.rst').read() + '\n\n' +
                      open('HISTORY.rst').read()),
    install_requires=[
        'validictory >= 0.8.0, != 0.9.2',         # error messages
        'decorator >= 3.3.1',                     # > 3.0 likely work, but not on pypi
        'mutagen >= 1.18',                        # EasyID3 module renaming
        'protobuf >= 2.4.1',                      # 2.3.0 uses ez_setup?
        'requests >= 1.1.0, != 1.2.0, != 2.2.1',  # session.close
        'python-dateutil >= 1.3, != 2.0',         # 2.0 is python3-only
        'proboscis >= 1.2.5.1',                   # runs_after
        'oauth2client >= 1.1',                    # TokenRevokeError
        'mock >= 0.7.0',                          # MagicMock
        'appdirs >= 1.1.0',                       # user_log_dir
        'gpsoauth == 0.0.4',                      # mac -> android_id, validation
        'MechanicalSoup',
        'pyopenssl',
        'ndg-httpsclient',
        'pyasn1',
    ] + dynamic_requires,
    classifiers=[
        'Development Status :: 4 - Beta',
        'Intended Audience :: Developers',
        'License :: OSI Approved :: BSD License',
        'Operating System :: OS Independent',
        'Programming Language :: Python',
        'Programming Language :: Python :: 2.7',
        'Topic :: Internet :: WWW/HTTP',
        'Topic :: Multimedia :: Sound/Audio',
        'Topic :: Software Development :: Libraries :: Python Modules',
    ],
    include_package_data=True,
    zip_safe=False,
)
