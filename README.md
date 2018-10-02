JSCover2 - Deprecated
=====================

This repository will probably not be maintained:
1. [Nashorn has been deprecated](http://openjdk.java.net/jeps/335)
1. [NodeJS now has native coverage support](https://blog.npmjs.org/post/178487845610/rethinking-javascript-test-coverage)




A JavaScript code-coverage tool

[![Build Status](https://travis-ci.org/tntim96/JSCover2.svg?branch=master)](https://travis-ci.org/tntim96/JSCover2)
[![codecov](https://codecov.io/gh/tntim96/JSCover2/branch/master/graph/badge.svg)](https://codecov.io/gh/tntim96/JSCover2)
[![Dependency Status](https://www.versioneye.com/user/projects/5404420cd0734e31f400016d/badge.svg?style=flat)](https://www.versioneye.com/user/projects/5404420cd0734e31f400016d)

Motivations
* Swap (NB: JSCover has swapped too) from Rhino to the excellent [Closure-Compiler](https://developers.google.com/closure/compiler/) for several reasons:
  * ES6 Harmony support
  * Designed specifically for AST manipulation and JavaScript code generation
  * Superior code position data for reports
* Modify the instrumentation to better support non-browser environments like Nashorn (NB: test suite uses Nashorn)
* Make build more modular for different environments: API, CLI, browser